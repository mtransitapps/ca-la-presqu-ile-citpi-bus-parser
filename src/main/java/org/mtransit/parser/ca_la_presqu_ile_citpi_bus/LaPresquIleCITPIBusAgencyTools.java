package org.mtransit.parser.ca_la_presqu_ile_citpi_bus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.parser.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MTrip;

import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mtransit.parser.Constants.EMPTY;

// https://exo.quebec/en/about/open-data
// https://exo.quebec/xdata/citpi/google_transit.zip
public class LaPresquIleCITPIBusAgencyTools extends DefaultAgencyTools {

	public static void main(@Nullable String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/ca-la-presqu-ile-citpi-bus-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new LaPresquIleCITPIBusAgencyTools().start(args);
	}

	@Nullable
	private HashSet<Integer> serviceIdInts;

	@Override
	public void start(@NotNull String[] args) {
		MTLog.log("Generating CITPI bus data...");
		long start = System.currentTimeMillis();
		this.serviceIdInts = extractUsefulServiceIdInts(args, this, true);
		super.start(args);
		MTLog.log("Generating CITPI bus data... DONE in %s.", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludingAll() {
		return this.serviceIdInts != null && this.serviceIdInts.isEmpty();
	}

	@Override
	public boolean excludeCalendar(@NotNull GCalendar gCalendar) {
		if (this.serviceIdInts != null) {
			return excludeUselessCalendarInt(gCalendar, this.serviceIdInts);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(@NotNull GCalendarDate gCalendarDates) {
		if (this.serviceIdInts != null) {
			return excludeUselessCalendarDateInt(gCalendarDates, this.serviceIdInts);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	@Override
	public boolean excludeTrip(@NotNull GTrip gTrip) {
		if (this.serviceIdInts != null) {
			return excludeUselessTripInt(gTrip, this.serviceIdInts);
		}
		return super.excludeTrip(gTrip);
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@NotNull
	@Override
	public String getRouteLongName(@NotNull GRoute gRoute) {
		String routeLongName = gRoute.getRouteLongNameOrDefault();
		routeLongName = CleanUtils.SAINT.matcher(routeLongName).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		routeLongName = CleanUtils.cleanSlashes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	private static final String _40_RSN = "A40";
	private static final String RSN_40 = "40";

	@Nullable
	@Override
	public String getRouteShortName(@NotNull GRoute gRoute) {
		if (RSN_40.equals(gRoute.getRouteShortName())) {
			return _40_RSN;
		}
		return super.getRouteShortName(gRoute);
	}

	private static final String A = "A";
	private static final String B = "B";
	private static final String C = "C";
	private static final String D = "D";
	private static final String E = "E";
	private static final String F = "F";
	private static final String G = "G";
	private static final String H = "H";

	private static final long RID_ENDS_WITH_A = 1_000L;
	private static final long RID_ENDS_WITH_B = 2_000L;

	@Override
	public long getRouteId(@NotNull GRoute gRoute) {
		if (!Utils.isDigitsOnly(gRoute.getRouteShortName())) {
			Matcher matcher = DIGITS.matcher(gRoute.getRouteShortName());
			if (matcher.find()) {
				int digits = Integer.parseInt(matcher.group());
				if (gRoute.getRouteShortName().endsWith(A)) {
					return RID_ENDS_WITH_A + digits;
				} else if (gRoute.getRouteShortName().endsWith(B)) {
					return RID_ENDS_WITH_B + digits;
				}
			}
			throw new MTLog.Fatal("Unexpected route ID for %s!", gRoute);
		}
		return Long.parseLong(gRoute.getRouteShortName());
	}

	private static final String AGENCY_COLOR = "1F1F1F"; // DARK GRAY (from GTFS)

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@Override
	public void setTripHeadsign(@NotNull MRoute mRoute, @NotNull MTrip mTrip, @NotNull GTrip gTrip, @NotNull GSpec gtfs) {
		mTrip.setHeadsignString(
				cleanTripHeadsign(gTrip.getTripHeadsignOrDefault()),
				gTrip.getDirectionIdOrDefault()
		);
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String cleanDirectionHeadsign(boolean fromStopName, @NotNull String directionHeadSign) {
		if (directionHeadSign.endsWith("AM")) {
			return "AM";
		} else if (directionHeadSign.endsWith("PM")) {
			return "PM";
		}
		directionHeadSign = super.cleanDirectionHeadsign(fromStopName, directionHeadSign);
		return directionHeadSign;
	}

	@Override
	public boolean mergeHeadsign(@NotNull MTrip mTrip, @NotNull MTrip mTripToMerge) {
		throw new MTLog.Fatal("Unexpected trips to merge %s & %s!", mTrip, mTripToMerge);
	}

	private static final Pattern DIRECTION_ = Pattern.compile("(direction )", Pattern.CASE_INSENSITIVE);

	private static final Pattern SECTEUR_ = Pattern.compile("(secteur[s]? )", Pattern.CASE_INSENSITIVE);

	private static final Pattern SERVICE = Pattern.compile("(service) ([a|p]m)", Pattern.CASE_INSENSITIVE);
	private static final String SERVICE_REPLACEMENT = "$2";

	private static final Pattern POINT = Pattern.compile("(point)", Pattern.CASE_INSENSITIVE);
	private static final String POINT_REPLACEMENT = "Pt";

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = DIRECTION_.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = SECTEUR_.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = SERVICE.matcher(tripHeadsign).replaceAll(SERVICE_REPLACEMENT);
		tripHeadsign = POINT.matcher(tripHeadsign).replaceAll(POINT_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanStreetTypesFRCA(tripHeadsign);
		return CleanUtils.cleanLabelFR(tripHeadsign);
	}

	private static final Pattern START_WITH_FACE_A = Pattern.compile("^(face à )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.CANON_EQ);
	private static final Pattern START_WITH_FACE_AU = Pattern.compile("^(face au )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern START_WITH_FACE = Pattern.compile("^(face )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern SPACE_FACE_A = Pattern.compile("( face à )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.CANON_EQ);
	private static final Pattern SPACE_WITH_FACE_AU = Pattern.compile("( face au )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern SPACE_WITH_FACE = Pattern.compile("( face )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern[] START_WITH_FACES = new Pattern[]{START_WITH_FACE_A, START_WITH_FACE_AU, START_WITH_FACE};

	private static final Pattern[] SPACE_FACES = new Pattern[]{SPACE_FACE_A, SPACE_WITH_FACE_AU, SPACE_WITH_FACE};

	private static final Pattern AVENUE = Pattern.compile("( avenue)", Pattern.CASE_INSENSITIVE);
	private static final String AVENUE_REPLACEMENT = " av.";

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = AVENUE.matcher(gStopName).replaceAll(AVENUE_REPLACEMENT);
		gStopName = Utils.replaceAll(gStopName, START_WITH_FACES, CleanUtils.SPACE);
		gStopName = Utils.replaceAll(gStopName, SPACE_FACES, CleanUtils.SPACE);
		gStopName = CleanUtils.cleanStreetTypesFRCA(gStopName);
		return CleanUtils.cleanLabelFR(gStopName);
	}

	private static final String ZERO = "0";

	@NotNull
	@Override
	public String getStopCode(@NotNull GStop gStop) {
		if (ZERO.equals(gStop.getStopCode())) {
			return EMPTY;
		}
		return super.getStopCode(gStop);
	}

	private static final Pattern DIGITS = Pattern.compile("[\\d]+");

	private static final String DDO = "DDO";
	private static final String HUD = "HUD";
	private static final String LIP = "LIP";
	private static final String NIP = "NIP";
	private static final String PCL = "PCL";
	private static final String PIN = "PIN";
	private static final String RIG = "RIG";
	private static final String SAB = "SAB";
	private static final String SGV = "SGV";
	private static final String SLR = "SLR";
	private static final String SLZ = "SLZ";
	private static final String VAU = "VAU";

	@Override
	public int getStopId(@NotNull GStop gStop) {
		String stopCode = getStopCode(gStop);
		if (stopCode.length() > 0 && Utils.isDigitsOnly(stopCode)) {
			return Integer.parseInt(stopCode); // using stop code as stop ID
		}
		//noinspection deprecation
		String stopIds = gStop.getStopId();
		stopIds = CleanUtils.cleanMergedID(stopIds);
		stopIds = stopIds.toUpperCase(Locale.ENGLISH);
		Matcher matcher = DIGITS.matcher(stopIds);
		if (matcher.find()) {
			int digits = Integer.parseInt(matcher.group());
			int stopId;
			if (stopIds.startsWith(DDO)) {
				stopId = 100_000;
			} else if (stopIds.startsWith(HUD)) {
				stopId = 200_000;
			} else if (stopIds.startsWith(LIP)) {
				stopId = 300_000;
			} else if (stopIds.startsWith(NIP)) {
				stopId = 400_000;
			} else if (stopIds.startsWith(PCL)) {
				stopId = 500_000;
			} else if (stopIds.startsWith(PIN)) {
				stopId = 600_000;
			} else if (stopIds.startsWith(RIG)) {
				stopId = 700_000;
			} else if (stopIds.startsWith(SAB)) {
				stopId = 800_000;
			} else if (stopIds.startsWith(SGV)) {
				stopId = 900_000;
			} else if (stopIds.startsWith(SLR)) {
				stopId = 1_000_000;
			} else if (stopIds.startsWith(SLZ)) {
				stopId = 1_100_000;
			} else if (stopIds.startsWith(VAU)) {
				stopId = 1_200_000;
			} else {
				throw new MTLog.Fatal("Stop doesn't have an ID (start with)! %s!", gStop);
			}
			if (stopIds.endsWith(A)) {
				stopId += 1_000;
			} else if (stopIds.endsWith(B)) {
				stopId += 2_000;
			} else if (stopIds.endsWith(C)) {
				stopId += 3_000;
			} else if (stopIds.endsWith(D)) {
				stopId += 4_000;
			} else if (stopIds.endsWith(E)) {
				stopId += 5_000;
			} else if (stopIds.endsWith(F)) {
				stopId += 6_000;
			} else if (stopIds.endsWith(G)) {
				stopId += 7_000;
			} else if (stopIds.endsWith(H)) {
				stopId += 8_000;
			} else {
				throw new MTLog.Fatal("Stop doesn't have an ID (end with)! %s!", gStop);
			}
			return stopId + digits;
		}
		throw new MTLog.Fatal("Unexpected stop ID for %s!", gStop);
	}
}
