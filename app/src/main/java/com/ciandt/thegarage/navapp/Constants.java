package com.ciandt.thegarage.navapp;

/**
 * Created by alans on 01/09/15.
 */
public class Constants {
    public static String BEACON_ATUAL_KEY = "BEACON_ATUAL";
    public static String BEACON_ANTERIOR_KEY = "BEACON_ANTERIOR";

    public static Integer PERIOD_MILLIS_REGION_MONITORING = 5;
    public static Integer WAIT_TIME_MILLIS_REGION_MONITORING = 5;
    public static Integer PERIOD_MILLIS_SCAN_RANGING = 4;
    public static Integer WAIT_TIME_MILLIS_SCAN_RANGING = 2;

    public static String BEACONS_URL_WEBSERVICE = "http://citbeacons.appspot.com/ws/beacon/findByMacAddress/macAddress=";
}
