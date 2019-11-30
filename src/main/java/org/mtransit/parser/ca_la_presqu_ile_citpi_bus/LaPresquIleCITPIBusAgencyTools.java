package org.mtransit.parser.ca_la_presqu_ile_citpi_bus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

// https://exo.quebec/en/about/open-data
// https://exo.quebec/xdata/citpi/google_transit.zip
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
		this.serviceIds = extractUsefulServiceIds(args, this, true);
		super.start(args);
		System.out.printf("\nGenerating CITPI bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludingAll() {
		return this.serviceIds != null && this.serviceIds.isEmpty();
	}

	@Override
	public boolean excludeRoute(GRoute gRoute) {
		return super.excludeRoute(gRoute);
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

	private static final String A = "A";
	private static final String B = "B";
	private static final String C = "C";
	private static final String D = "D";
	private static final String E = "E";
	private static final String F = "F";
	private static final String G = "G";
	private static final String H = "H";

	private static final long RID_ENDS_WITH_A = 1000L;
	private static final long RID_ENDS_WITH_B = 2000L;

	@Override
	public long getRouteId(GRoute gRoute) {
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
			System.out.printf("\nUnexpected route ID for %s!\n", gRoute);
			System.exit(-1);
			return -1L;
		}
		return Long.parseLong(gRoute.getRouteShortName());
	}

	private static final String AGENCY_COLOR = "1F1F1F"; // DARK GRAY (from GTFS)

	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final String VAUDREUIL = "Vaudreuil";
	private static final String GARE_VAUDREUIL = "Gare " + VAUDREUIL;
	private static final String GARE_DORION = "Gare Dorion";
	private static final String MARIER = "Marier";
	private static final String FLORALIES = "Floralies";
	private static final String POINTE_CLAIRE = "Pte-Claire";
	private static final String SAINTE_ANNE_DE_BELLEVUE = "Ste-Anne-De-Bellevue";

	private static HashMap<Long, RouteTripSpec> ALL_ROUTE_TRIPS2;
	static {
		HashMap<Long, RouteTripSpec> map2 = new HashMap<Long, RouteTripSpec>();
		map2.put(4L, new RouteTripSpec(4L, //
				0, MTrip.HEADSIGN_TYPE_STRING, GARE_VAUDREUIL, //
				1, MTrip.HEADSIGN_TYPE_STRING, FLORALIES) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						"72606", // rue des Floralies / rue des Perce-Neige
								"72605", // ++
								"72912", // ==
								"72975", // != Gare Vaudreuil terminus temporaire été 2019 =>
								"72900", // != Gare Vaudreuil =>
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						"72900", // != Gare Vaudreuil <=
								"72975", // != Gare Vaudreuil terminus temporaire été 2019 <=
								"72911", // ==
								"72948", // ++
								"72606", // rue des Floralies / rue des Perce-Neige
						})) //
				.compileBothTripSort());
		map2.put(5L, new RouteTripSpec(5L, //
				0, MTrip.HEADSIGN_TYPE_STRING, GARE_DORION, //
				1, MTrip.HEADSIGN_TYPE_STRING, GARE_VAUDREUIL) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						"72900", // != Gare Vaudreuil <=
								"72975", // != Gare Vaudreuil terminus temporaire été 2019 <=
								"72911", // == rue Boileau / rue Forbes
								"72946", // Gare Dorion
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						"72946", // Gare Dorion
								"72741", // avenue Brodeur / rue St-Charles
								"72912", // ==
								"72975", // != Gare Vaudreuil terminus temporaire été 2019 =>
								"72900", // != Gare Vaudreuil =>
						})) //
				.compileBothTripSort());
		map2.put(9L, new RouteTripSpec(9L, //
				0, MTrip.HEADSIGN_TYPE_STRING, GARE_VAUDREUIL, //
				1, MTrip.HEADSIGN_TYPE_STRING, MARIER) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						"72831", // rue Valois / face au tunnel <=
								"72889", // != avenue Marier / rue des Noisetiers
								"72887", // != <> avenue Marier / rue des Tilleuls <=
								"72885", // ==
								"72621", // ==
								"72975", // != Gare Vaudreuil terminus temporaire été 2019 =>
								"72900", // != Gare Vaudreuil =>
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						"72900", // != Gare Vaudreuil <=
								"72975", // != Gare Vaudreuil terminus temporaire été 2019 <=
								"72623", // ==
								"72892", // == != avenue Marier / rue des Merisiers
								"72887", // != <> avenue Marier / rue des Tilleuls =>
								"72894", // != avenue Marier / 2e avenue
								"72831", // rue Valois / face au tunnel =>
						})) //
				.compileBothTripSort());
		ALL_ROUTE_TRIPS2 = map2;
	}

	@Override
	public int compareEarly(long routeId, List<MTripStop> list1, List<MTripStop> list2, MTripStop ts1, MTripStop ts2, GStop ts1GStop, GStop ts2GStop) {
		if (ALL_ROUTE_TRIPS2.containsKey(routeId)) {
			return ALL_ROUTE_TRIPS2.get(routeId).compare(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop, this);
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

	@Override
	public Pair<Long[], Integer[]> splitTripStop(MRoute mRoute, GTrip gTrip, GTripStop gTripStop, ArrayList<MTrip> splitTrips, GSpec routeGTFS) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return SplitUtils.splitTripStop(mRoute, gTrip, gTripStop, routeGTFS, ALL_ROUTE_TRIPS2.get(mRoute.getId()), this);
		}
		return super.splitTripStop(mRoute, gTrip, gTripStop, splitTrips, routeGTFS);
	}

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return; // split
		}
		mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), gTrip.getDirectionId());
	}

	@Override
	public boolean mergeHeadsign(MTrip mTrip, MTrip mTripToMerge) {
		List<String> headsignsValues = Arrays.asList(mTrip.getHeadsignValue(), mTripToMerge.getHeadsignValue());
		if (mTrip.getRouteId() == 7L) {
			if (Arrays.asList( //
					"Navette Dumberry", // <>
					SAINTE_ANNE_DE_BELLEVUE, //
					POINTE_CLAIRE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(POINTE_CLAIRE, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					"Navette Dumberry", // <>
					GARE_VAUDREUIL //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(GARE_VAUDREUIL, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 15L) {
			if (Arrays.asList( //
					GARE_VAUDREUIL, //
					VAUDREUIL //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(VAUDREUIL, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 21L) {
			if (Arrays.asList( //
					"Hudson", //
					VAUDREUIL //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(VAUDREUIL, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 35L) {
			if (Arrays.asList( //
					"Île-Perrot", //
					SAINTE_ANNE_DE_BELLEVUE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(SAINTE_ANNE_DE_BELLEVUE, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 335L) {
			if (Arrays.asList( //
					"Île-Perrot", //
					SAINTE_ANNE_DE_BELLEVUE //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(SAINTE_ANNE_DE_BELLEVUE, mTrip.getHeadsignId());
				return true;
			} else if (Arrays.asList( //
					"Gare Pincourt T-V", //
					GARE_DORION //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(GARE_DORION, mTrip.getHeadsignId());
				return true;
			}
		}
		System.out.printf("\nUnexpected trips to merge %s & %s!\n", mTrip, mTripToMerge);
		System.exit(-1);
		return false;
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
	public int getStopId(GStop gStop) {
		String stopCode = getStopCode(gStop);
		if (stopCode != null && stopCode.length() > 0 && Utils.isDigitsOnly(stopCode)) {
			return Integer.valueOf(stopCode); // using stop code as stop ID
		}
		String stopIds = gStop.getStopId();
		stopIds = CleanUtils.cleanMergedID(stopIds);
		stopIds = stopIds.toUpperCase(Locale.ENGLISH);
		Matcher matcher = DIGITS.matcher(stopIds);
		if (matcher.find()) {
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
		System.out.printf("\nUnexpected stop ID for %s!\n", gStop);
		System.exit(-1);
		return -1;
	}
}