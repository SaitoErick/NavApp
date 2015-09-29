package com.ciandt.thegarage.navapp.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by carlos on 9/24/15.
 */
@Table(name = "TblBeaconsNavigation")
public class BeaconsNavigationModel extends Model {

    @Column(name = "proximityUUID")
    private String proximityUUID;

    @Column(name = "name")
    private String name;

    @Column(name = "macAddress")
    private String macAddress;

    @Column(name = "major")
    private int major;

    @Column(name = "minor")
    private int minor;

    @Column(name = "measuredPower")
    private int measuredPower;

    @Column(name = "rssi")
    private int rssi;

    @Column(name = "describeBeacon")
    private String describeBeacon;

    public String getProximityUUID() {
        return proximityUUID;
    }

    public String getName() {
        return name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getMeasuredPower() {
        return measuredPower;
    }

    public int getRssi() {
        return rssi;
    }

    public String getDescribeBeacon() {
        return describeBeacon;
    }

    public BeaconsNavigationModel() {
        super();
    }

    public Long save(String proximityUUID, String name, String macAddress, int major, int minor, int measuredPower, int rssi, String describeBeacon) {

        this.proximityUUID = proximityUUID;
        this.name = name;
        this.macAddress = macAddress;
        this.major = major;
        this.minor = minor;
        this.measuredPower = measuredPower;
        this.rssi = rssi;
        this.describeBeacon = describeBeacon;

        return super.save();
    }

    public static List<BeaconsNavigationModel> getAll() {
        return new Select()
                .from(BeaconsNavigationModel.class)
                .orderBy("rssi ASC")
                .execute();
    }

    public Integer beaconExistByMacAddress(String mMacAddress) {
        List<BeaconsNavigationModel> ListBeaconsNavigationModel = new Select()
                .from(BeaconsNavigationModel.class)
                .where("macAddress = ?", mMacAddress)
                .execute();

        return ListBeaconsNavigationModel.size();
    }
}
