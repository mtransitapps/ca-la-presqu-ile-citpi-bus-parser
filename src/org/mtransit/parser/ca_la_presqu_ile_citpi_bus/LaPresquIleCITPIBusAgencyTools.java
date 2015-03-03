package org.mtransit.parser.ca_la_presqu_ile_citpi_bus;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MSpec;
import org.mtransit.parser.mt.data.MTrip;

// https://www.amt.qc.ca/en/about/open-data
// http://www.amt.qc.ca/xdata/citpi/google_transit.zip
public class LaPresquIleCITPIBusAgencyTools extends DefaultAgencyTools {

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/ca-la-presqu-ile-citpi-bus-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new LaPresquIleCITPIBusAgencyTools().start(args);
	}

	private HashSet<String> serviceIds;

	@Override
	public void start(String[] args) {
		System.out.printf("Generating CITPI bus data...\n");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIds(args, this);
		super.start(args);
		System.out.printf("Generating CITPI bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludeCalendar(GCalendar gCalendar) {
		if (this.serviceIds != null) {
			return excludeUselessCalendar(gCalendar, this.serviceIds);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(GCalendarDate gCalendarDates) {
		if (this.serviceIds != null) {
			return excludeUselessCalendarDate(gCalendarDates, this.serviceIds);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	@Override
	public boolean excludeTrip(GTrip gTrip) {
		if (this.serviceIds != null) {
			return excludeUselessTrip(gTrip, this.serviceIds);
		}
		return super.excludeTrip(gTrip);
	}

	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public String getRouteLongName(GRoute gRoute) {
		String routeLongName = gRoute.route_long_name;
		routeLongName = MSpec.SAINT.matcher(routeLongName).replaceAll(MSpec.SAINT_REPLACEMENT);
		return MSpec.cleanLabel(routeLongName);
	}

	@Override
	public String getRouteShortName(GRoute gRoute) {
		if (gRoute.route_short_name.equals("40")) {
			return "A40";
		}
		return super.getRouteShortName(gRoute);
	}

	private static final String AGENCY_COLOR = "258FE8";

	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@Override
	public String getRouteColor(GRoute gRoute) {
		if ("1".equals(gRoute.route_short_name)) return "00336C";
		if ("2".equals(gRoute.route_short_name)) return "6DC8BF";
		if ("3".equals(gRoute.route_short_name)) return "1E398D";
		if ("4".equals(gRoute.route_short_name)) return "F15A29";
		if ("5".equals(gRoute.route_short_name)) return "EC008C";
		if ("6".equals(gRoute.route_short_name)) return "8DC63F";
		if ("7".equals(gRoute.route_short_name)) return "00ADDC";
		if ("8".equals(gRoute.route_short_name)) return "878A72";
		if ("15".equals(gRoute.route_short_name)) return "3B6E8F";
		if ("21".equals(gRoute.route_short_name)) return "B5121B";
		if ("31".equals(gRoute.route_short_name)) return "576423";
		if ("32".equals(gRoute.route_short_name)) return "FFD200";
		if ("33".equals(gRoute.route_short_name)) return "E9979B";
		if ("35".equals(gRoute.route_short_name)) return "F15D5E";
		if ("40".equals(gRoute.route_short_name)) return "E977AF";
		if ("41".equals(gRoute.route_short_name)) return "8D64AA";
		if ("42".equals(gRoute.route_short_name)) return "B32317";
		if ("43".equals(gRoute.route_short_name)) return "C2A204";
		if ("44".equals(gRoute.route_short_name)) return "B26062";
		if ("46".equals(gRoute.route_short_name)) return "A6228E";
		if ("47".equals(gRoute.route_short_name)) return "B2BB1E";
		if ("51".equals(gRoute.route_short_name)) return "E9979B";
		if ("61".equals(gRoute.route_short_name)) return "F58220";
		if ("91".equals(gRoute.route_short_name)) return "007D68";
		return super.getRouteColor(gRoute);
	}

	@Override
	public void setTripHeadsign(MRoute route, MTrip mTrip, GTrip gTrip) {
		String stationName = cleanTripHeadsign(gTrip.trip_headsign);
		int directionId = Integer.valueOf(gTrip.direction_id);
		if (mTrip.getRouteId() == 51l) {
			if (directionId == 0) {
				stationName = "Vaudreuil";
			} else if (directionId == 1) {
				stationName = "St-Lazare";
			}
		}
		mTrip.setHeadsignString(stationName, directionId);
	}

	private static final Pattern DIRECTION = Pattern.compile("(direction )", Pattern.CASE_INSENSITIVE);
	private static final String DIRECTION_REPLACEMENT = "";

	private static final Pattern SECTEUR = Pattern.compile("(secteur[s]? )", Pattern.CASE_INSENSITIVE);
	private static final String SECTEUR_REPLACEMENT = "";

	private static final Pattern SERVICE = Pattern.compile("(service) ([a|p]m)", Pattern.CASE_INSENSITIVE);
	private static final String SERVICE_REPLACEMENT = "$2";

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		tripHeadsign = DIRECTION.matcher(tripHeadsign).replaceAll(DIRECTION_REPLACEMENT);
		tripHeadsign = SECTEUR.matcher(tripHeadsign).replaceAll(SECTEUR_REPLACEMENT);
		tripHeadsign = SERVICE.matcher(tripHeadsign).replaceAll(SERVICE_REPLACEMENT);
		return MSpec.cleanLabelFR(tripHeadsign);
	}

	private static final Pattern START_WITH_FACE_A = Pattern.compile("^(face à )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern START_WITH_FACE_AU = Pattern.compile("^(face au )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern START_WITH_FACE = Pattern.compile("^(face )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern SPACE_FACE_A = Pattern.compile("( face à )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern SPACE_WITH_FACE_AU = Pattern.compile("( face au )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern SPACE_WITH_FACE = Pattern.compile("( face )", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern[] START_WITH_FACES = new Pattern[] { START_WITH_FACE_A, START_WITH_FACE_AU, START_WITH_FACE };

	private static final Pattern[] SPACE_FACES = new Pattern[] { SPACE_FACE_A, SPACE_WITH_FACE_AU, SPACE_WITH_FACE };

	private static final Pattern AVENUE = Pattern.compile("( avenue)", Pattern.CASE_INSENSITIVE);
	private static final String AVENUE_REPLACEMENT = " av.";

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = AVENUE.matcher(gStopName).replaceAll(AVENUE_REPLACEMENT);
		gStopName = Utils.replaceAll(gStopName, START_WITH_FACES, MSpec.SPACE);
		gStopName = Utils.replaceAll(gStopName, SPACE_FACES, MSpec.SPACE);
		return super.cleanStopNameFR(gStopName);
	}

	@Override
	public String getStopCode(GStop gStop) {
		if ("0".equals(gStop.stop_code)) {
			return null;
		}
		return super.getStopCode(gStop);
	}

	private static final Pattern DIGITS = Pattern.compile("[\\d]+");

	@Override
	public int getStopId(GStop gStop) {
		String stopCode = getStopCode(gStop);
		if (stopCode != null && stopCode.length() > 0) {
			return Integer.valueOf(stopCode); // using stop code as stop ID
		}
		// generating integer stop ID
		Matcher matcher = DIGITS.matcher(gStop.stop_id);
		matcher.find();
		int digits = Integer.parseInt(matcher.group());
		int stopId;
		if (gStop.stop_id.startsWith("DDO")) {
			stopId = 100000;
		} else if (gStop.stop_id.startsWith("HUD")) {
			stopId = 200000;
		} else if (gStop.stop_id.startsWith("LIP")) {
			stopId = 300000;
		} else if (gStop.stop_id.startsWith("NIP")) {
			stopId = 400000;
		} else if (gStop.stop_id.startsWith("PCL")) {
			stopId = 500000;
		} else if (gStop.stop_id.startsWith("PIN")) {
			stopId = 600000;
		} else if (gStop.stop_id.startsWith("RIG")) {
			stopId = 700000;
		} else if (gStop.stop_id.startsWith("SAB")) {
			stopId = 800000;
		} else if (gStop.stop_id.startsWith("SGV")) {
			stopId = 900000;
		} else if (gStop.stop_id.startsWith("SLR")) {
			stopId = 1000000;
		} else if (gStop.stop_id.startsWith("SLZ")) {
			stopId = 1100000;
		} else if (gStop.stop_id.startsWith("VAU")) {
			stopId = 1200000;
		} else {
			System.out.println("Stop doesn't have an ID (start with)! " + gStop);
			System.exit(-1);
			stopId = -1;
		}
		if (gStop.stop_id.endsWith("A")) {
			stopId += 1000;
		} else if (gStop.stop_id.endsWith("B")) {
			stopId += 2000;
		} else if (gStop.stop_id.endsWith("C")) {
			stopId += 3000;
		} else if (gStop.stop_id.endsWith("D")) {
			stopId += 4000;
		} else if (gStop.stop_id.endsWith("E")) {
			stopId += 5000;
		} else if (gStop.stop_id.endsWith("F")) {
			stopId += 6000;
		} else if (gStop.stop_id.endsWith("G")) {
			stopId += 7000;
		} else if (gStop.stop_id.endsWith("H")) {
			stopId += 8000;
		} else {
			System.out.println("Stop doesn't have an ID (end with)! " + gStop);
			System.exit(-1);
		}
		return stopId + digits;
	}

}
