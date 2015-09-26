package com.ciandt.thegarage.navapp.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;

/**
 * Created by carlos on 9/24/15.
 */
@Table(name = "TblBeaconsNavigation")
public class BeaconsNavigationModel extends Model {

    @Column(name = "macAddress")
    private String macAddress;

    @Column(name = "rssi")
    private int rssi;

    @Column(name = "createdAt")
    private String createdAt;

    public BeaconsNavigationModel() {
        super();
    }

    public Long save(String macAddress, int rssi) {
        this.macAddress = macAddress;
        this.rssi = rssi;
        this.createdAt = macAddress;

        return super.save();
    }

    public static List<BeaconsNavigationModel> getAll() {
        return new Select()
                .from(BeaconsNavigationModel.class)
                .orderBy("macAddress ASC")
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
