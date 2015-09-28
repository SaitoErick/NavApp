package com.ciandt.thegarage.navapp.model;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;

/**
 * Created by carlos on 9/27/15.
 */
public class BeaconNavigation extends Beacon {

    private String describeBeacon;

    public BeaconNavigation(String proximityUUID, String name, String macAddress, int major, int minor, int measuredPower, int rssi, String mDescribeBeacon) {
        super(proximityUUID, name, macAddress, major, minor, measuredPower, rssi);
        this.describeBeacon = mDescribeBeacon;
    }

    public String getDescribeBeacon() {
        return this.describeBeacon;
    }
}
