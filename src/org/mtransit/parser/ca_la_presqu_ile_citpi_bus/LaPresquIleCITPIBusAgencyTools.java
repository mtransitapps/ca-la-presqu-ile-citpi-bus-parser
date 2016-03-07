package org.mtransit.parser.ca_la_presqu_ile_citpi_bus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.mtransit.parser.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Pair;
import org.mtransit.parser.SplitUtils;
import org.mtransit.parser.SplitUtils.RouteTripSpec;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.gtfs.data.GTripStop;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MTrip;
import org.mtransit.parser.mt.data.MTripStop;

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
		System.out.printf("\nGenerating CITPI bus data...");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIds(args, this);
		super.start(args);
		System.out.printf("\nGenerating CITPI bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
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
		String routeLongName = gRoute.getRouteLongName();
		routeLongName = CleanUtils.SAINT.matcher(routeLongName).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		routeLongName = CleanUtils.cleanSlashes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	private static final String _40_RSN = "A40";
	private static final String RSN_40 = "40";

	@Override
	public String getRouteShortName(GRoute gRoute) {
		if (RSN_40.equals(gRoute.getRouteShortName())) {
			return _40_RSN;
		}
		return super.getRouteShortName(gRoute);
	}

	@Override
	public long getRouteId(GRoute gRoute) {
		return Long.parseLong(gRoute.getRouteShortName());
	}

	private static final String AGENCY_COLOR = "258FE8";

	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final String COLOR_00336C = "00336C";
	private static final String COLOR_6DC8BF = "6DC8BF";
	private static final String COLOR_1E398D = "1E398D";
	private static final String COLOR_F15A29 = "F15A29";
	private static final String COLOR_EC008C = "EC008C";
	private static final String COLOR_8DC63F = "8DC63F";
	private static final String COLOR_00ADDC = "00ADDC";
	private static final String COLOR_878A72 = "878A72";
	private static final String COLOR_3B6E8F = "3B6E8F";
	private static final String COLOR_B5121B = "B5121B";
	private static final String COLOR_576423 = "576423";
	private static final String COLOR_FFD200 = "FFD200";
	private static final String COLOR_F15D5E = "F15D5E";
	private static final String COLOR_E977AF = "E977AF";
	private static final String COLOR_8D64AA = "8D64AA";
	private static final String COLOR_B32317 = "B32317";
	private static final String COLOR_C2A204 = "C2A204";
	private static final String COLOR_B26062 = "B26062";
	private static final String COLOR_A6228E = "A6228E";
	private static final String COLOR_B2BB1E = "B2BB1E";
	private static final String COLOR_E9979B = "E9979B";
	private static final String COLOR_F58220 = "F58220";
	private static final String COLOR_007D68 = "007D68";

	@Override
	public String getRouteColor(GRoute gRoute) {
		if (StringUtils.isEmpty(gRoute.getRouteColor())) {
			int rsn = Integer.parseInt(gRoute.getRouteShortName());
			switch (rsn) {
			// @formatter:off
			case 1: return COLOR_00336C;
			case 2: return COLOR_6DC8BF;
			case 3: return COLOR_1E398D;
			case 4: return COLOR_F15A29;
			case 5: return COLOR_EC008C;
			case 6: return COLOR_8DC63F;
			case 7: return COLOR_00ADDC;
			case 8: return COLOR_878A72;
			case 9: return COLOR_FFD200;
			case 15: return COLOR_3B6E8F;
			case 21: return COLOR_B5121B;
			case 31: return COLOR_576423;
			case 32: return COLOR_FFD200;
			case 33: return COLOR_E9979B;
			case 35: return COLOR_F15D5E;
			case 40: return COLOR_E977AF;
			case 41: return COLOR_8D64AA;
			case 42: return COLOR_B32317;
			case 43: return COLOR_C2A204;
			case 44: return COLOR_B26062;
			case 46: return COLOR_A6228E;
			case 47: return COLOR_B2BB1E;
			case 51: return COLOR_E9979B;
			case 61: return COLOR_F58220;
			case 91: return COLOR_007D68;
			// @formatter:on
			}
			System.out.printf("\nUnexpected route color %s!\n", gRoute);
			System.exit(-1);
			return null;
		}
		return super.getRouteColor(gRoute);
	}

	@Override
	public int compareEarly(long routeId, List<MTripStop> list1, List<MTripStop> list2, MTripStop ts1, MTripStop ts2, GStop ts1GStop, GStop ts2GStop) {
		if (ALL_ROUTE_TRIPS2.containsKey(routeId)) {
			return ALL_ROUTE_TRIPS2.get(routeId).compare(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop);
		}
		return super.compareEarly(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop);
	}

	@Override
	public ArrayList<MTrip> splitTrip(MRoute mRoute, GTrip gTrip, GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return ALL_ROUTE_TRIPS2.get(mRoute.getId()).getAllTrips();
		}
		return super.splitTrip(mRoute, gTrip, gtfs);
	}

	private static final String ST_LAZARE = "St-Lazare";
	private static final String GARE_VAUDREUIL = "Gare Vaudreuil";
	private static final String GARE_DORION = "Gare Dorion";
	private static final String MARIER = "Marier";
	private static final String FLORALIES = "Floralies";

	private static HashMap<Long, RouteTripSpec> ALL_ROUTE_TRIPS2;
	static {
		HashMap<Long, RouteTripSpec> map2 = new HashMap<Long, RouteTripSpec>();
		map2.put(4l, new RouteTripSpec(4l, //
				0, MTrip.HEADSIGN_TYPE_STRING, GARE_VAUDREUIL, //
				1, MTrip.HEADSIGN_TYPE_STRING, FLORALIES) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						"VAU189D", "VAU58B", //
								"VAU259C", "VAU262A", //
								"VAU91B", "VAU61A" //
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						"VAU61A", "VAU91D", //
								"VAU262C", "VAU58A", //
								"VAU58D", //
								"VAU258D", "VAU189D" //
						})) //
				.compileBothTripSort());
		map2.put(9l, new RouteTripSpec(9l, //
				0, MTrip.HEADSIGN_TYPE_STRING, GARE_VAUDREUIL, //
				1, MTrip.HEADSIGN_TYPE_STRING, MARIER) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						"VAU332A", //
								/* + */"VAU609D"/* + */, //
								"VAU609A", "VAU608B", "VAU61A"
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						"VAU61A", "VAU596D", "VAU608D", "VAU609D", "VAU332C", //
								"VAU332A" //
						})) //
				.compileBothTripSort());
		ALL_ROUTE_TRIPS2 = map2;
	}


	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return; // split
		}
		if (mRoute.getId() == 5l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(GARE_DORION, gTrip.getDirectionId());
				return;
			}
		} else if (mRoute.getId() == 51l) {
			if (gTrip.getDirectionId() == 0) {
				mTrip.setHeadsignString(GARE_VAUDREUIL, gTrip.getDirectionId());
				return;
			} else if (gTrip.getDirectionId() == 1) {
				mTrip.setHeadsignString(ST_LAZARE, gTrip.getDirectionId());
				return;
			}
		}
		mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), gTrip.getDirectionId());
	}

	@Override
	public Pair<Long[], Integer[]> splitTripStop(MRoute mRoute, GTrip gTrip, GTripStop gTripStop, ArrayList<MTrip> splitTrips, GSpec routeGTFS) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return SplitUtils.splitTripStop(mRoute, gTrip, gTripStop, routeGTFS, ALL_ROUTE_TRIPS2.get(mRoute.getId()));
		}
		return super.splitTripStop(mRoute, gTrip, gTripStop, splitTrips, routeGTFS);
	}

	private static final Pattern DIRECTION = Pattern.compile("(direction )", Pattern.CASE_INSENSITIVE);
	private static final String DIRECTION_REPLACEMENT = "";

	private static final Pattern SECTEUR = Pattern.compile("(secteur[s]? )", Pattern.CASE_INSENSITIVE);
	private static final String SECTEUR_REPLACEMENT = "";

	private static final Pattern SERVICE = Pattern.compile("(service) ([a|p]m)", Pattern.CASE_INSENSITIVE);
	private static final String SERVICE_REPLACEMENT = "$2";

	private static final Pattern POINT = Pattern.compile("(point)", Pattern.CASE_INSENSITIVE);
	private static final String POINT_REPLACEMENT = "Pt";

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		tripHeadsign = DIRECTION.matcher(tripHeadsign).replaceAll(DIRECTION_REPLACEMENT);
		tripHeadsign = SECTEUR.matcher(tripHeadsign).replaceAll(SECTEUR_REPLACEMENT);
		tripHeadsign = SERVICE.matcher(tripHeadsign).replaceAll(SERVICE_REPLACEMENT);
		tripHeadsign = POINT.matcher(tripHeadsign).replaceAll(POINT_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanStreetTypesFRCA(tripHeadsign);
		return CleanUtils.cleanLabelFR(tripHeadsign);
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
		gStopName = Utils.replaceAll(gStopName, START_WITH_FACES, CleanUtils.SPACE);
		gStopName = Utils.replaceAll(gStopName, SPACE_FACES, CleanUtils.SPACE);
		return CleanUtils.cleanLabelFR(gStopName);
	}

	private static final String ZERO = "0";

	@Override
	public String getStopCode(GStop gStop) {
		if (ZERO.equals(gStop.getStopCode())) {
			return null;
		}
		return super.getStopCode(gStop);
	}

	private static final Pattern DIGITS = Pattern.compile("[\\d]+");

	private static final String _MERGED = "_merged_";

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

	private static final String A = "A";
	private static final String B = "B";
	private static final String C = "C";
	private static final String D = "D";
	private static final String E = "E";
	private static final String F = "F";
	private static final String G = "G";
	private static final String H = "H";

	@Override
	public int getStopId(GStop gStop) {
		String stopCode = getStopCode(gStop);
		if (stopCode != null && stopCode.length() > 0) {
			return Integer.valueOf(stopCode); // using stop code as stop ID
		}
		String stopIds = gStop.getStopId();
		int index = stopIds.indexOf(_MERGED);
		if (index >= 0) {
			stopIds = stopIds.substring(0, index);
		}
		Matcher matcher = DIGITS.matcher(stopIds);
		matcher.find();
		int digits = Integer.parseInt(matcher.group());
		int stopId;
		if (stopIds.startsWith(DDO)) {
			stopId = 100000;
		} else if (stopIds.startsWith(HUD)) {
			stopId = 200000;
		} else if (stopIds.startsWith(LIP)) {
			stopId = 300000;
		} else if (stopIds.startsWith(NIP)) {
			stopId = 400000;
		} else if (stopIds.startsWith(PCL)) {
			stopId = 500000;
		} else if (stopIds.startsWith(PIN)) {
			stopId = 600000;
		} else if (stopIds.startsWith(RIG)) {
			stopId = 700000;
		} else if (stopIds.startsWith(SAB)) {
			stopId = 800000;
		} else if (stopIds.startsWith(SGV)) {
			stopId = 900000;
		} else if (stopIds.startsWith(SLR)) {
			stopId = 1000000;
		} else if (stopIds.startsWith(SLZ)) {
			stopId = 1100000;
		} else if (stopIds.startsWith(VAU)) {
			stopId = 1200000;
		} else {
			System.out.printf("\nStop doesn't have an ID (start with)! %s!\n", gStop);
			System.exit(-1);
			stopId = -1;
		}
		if (stopIds.endsWith(A)) {
			stopId += 1000;
		} else if (stopIds.endsWith(B)) {
			stopId += 2000;
		} else if (stopIds.endsWith(C)) {
			stopId += 3000;
		} else if (stopIds.endsWith(D)) {
			stopId += 4000;
		} else if (stopIds.endsWith(E)) {
			stopId += 5000;
		} else if (stopIds.endsWith(F)) {
			stopId += 6000;
		} else if (stopIds.endsWith(G)) {
			stopId += 7000;
		} else if (stopIds.endsWith(H)) {
			stopId += 8000;
		} else {
			System.out.printf("\nStop doesn't have an ID (end with)! %s!\n", gStop);
			System.exit(-1);
		}
		return stopId + digits;
	}
}
