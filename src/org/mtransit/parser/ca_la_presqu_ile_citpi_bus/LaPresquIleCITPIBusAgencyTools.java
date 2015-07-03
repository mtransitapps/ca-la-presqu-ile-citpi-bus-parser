package org.mtransit.parser.ca_la_presqu_ile_citpi_bus;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
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

	private static final String _40_RSN = "A40";

	@Override
	public String getRouteShortName(GRoute gRoute) {
		if (RSN_40.equals(gRoute.route_short_name)) {
			return _40_RSN;
		}
		return super.getRouteShortName(gRoute);
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

	private static final String RSN_1 = "1";
	private static final String RSN_2 = "2";
	private static final String RSN_3 = "3";
	private static final String RSN_4 = "4";
	private static final String RSN_5 = "5";
	private static final String RSN_6 = "6";
	private static final String RSN_7 = "7";
	private static final String RSN_8 = "8";
	private static final String RSN_15 = "15";
	private static final String RSN_21 = "21";
	private static final String RSN_31 = "31";
	private static final String RSN_32 = "32";
	private static final String RSN_33 = "33";
	private static final String RSN_35 = "35";
	private static final String RSN_40 = "40";
	private static final String RSN_41 = "41";
	private static final String RSN_42 = "42";
	private static final String RSN_43 = "43";
	private static final String RSN_44 = "44";
	private static final String RSN_46 = "46";
	private static final String RSN_47 = "47";
	private static final String RSN_51 = "51";
	private static final String RSN_61 = "61";
	private static final String RSN_91 = "91";

	@Override
	public String getRouteColor(GRoute gRoute) {
		if (RSN_1.equals(gRoute.route_short_name)) return COLOR_00336C;
		if (RSN_2.equals(gRoute.route_short_name)) return COLOR_6DC8BF;
		if (RSN_3.equals(gRoute.route_short_name)) return COLOR_1E398D;
		if (RSN_4.equals(gRoute.route_short_name)) return COLOR_F15A29;
		if (RSN_5.equals(gRoute.route_short_name)) return COLOR_EC008C;
		if (RSN_6.equals(gRoute.route_short_name)) return COLOR_8DC63F;
		if (RSN_7.equals(gRoute.route_short_name)) return COLOR_00ADDC;
		if (RSN_8.equals(gRoute.route_short_name)) return COLOR_878A72;
		if (RSN_15.equals(gRoute.route_short_name)) return COLOR_3B6E8F;
		if (RSN_21.equals(gRoute.route_short_name)) return COLOR_B5121B;
		if (RSN_31.equals(gRoute.route_short_name)) return COLOR_576423;
		if (RSN_32.equals(gRoute.route_short_name)) return COLOR_FFD200;
		if (RSN_33.equals(gRoute.route_short_name)) return COLOR_E9979B;
		if (RSN_35.equals(gRoute.route_short_name)) return COLOR_F15D5E;
		if (RSN_40.equals(gRoute.route_short_name)) return COLOR_E977AF;
		if (RSN_41.equals(gRoute.route_short_name)) return COLOR_8D64AA;
		if (RSN_42.equals(gRoute.route_short_name)) return COLOR_B32317;
		if (RSN_43.equals(gRoute.route_short_name)) return COLOR_C2A204;
		if (RSN_44.equals(gRoute.route_short_name)) return COLOR_B26062;
		if (RSN_46.equals(gRoute.route_short_name)) return COLOR_A6228E;
		if (RSN_47.equals(gRoute.route_short_name)) return COLOR_B2BB1E;
		if (RSN_51.equals(gRoute.route_short_name)) return COLOR_E9979B;
		if (RSN_61.equals(gRoute.route_short_name)) return COLOR_F58220;
		if (RSN_91.equals(gRoute.route_short_name)) return COLOR_007D68;
		return super.getRouteColor(gRoute);
	}

	private static final String ST_LAZARE = "St-Lazare";
	private static final String VAUDREUIL = "Vaudreuil";

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		String stationName = cleanTripHeadsign(gTrip.trip_headsign);
		if (mTrip.getRouteId() == 51l) {
			if (gTrip.direction_id == 0) {
				stationName = VAUDREUIL;
			} else if (gTrip.direction_id == 1) {
				stationName = ST_LAZARE;
			}
		}
		mTrip.setHeadsignString(stationName, gTrip.direction_id);
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

	private static final String ZERO = "0";

	@Override
	public String getStopCode(GStop gStop) {
		if (ZERO.equals(gStop.stop_code)) {
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
