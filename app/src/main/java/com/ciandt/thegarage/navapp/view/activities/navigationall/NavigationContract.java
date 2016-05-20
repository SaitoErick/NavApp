package com.ciandt.thegarage.navapp.view.activities.navigationall;

import android.content.Context;

import com.ciandt.thegarage.navapp.model.api.BeaconApi;
import com.estimote.sdk.Beacon;

import java.util.List;

/**
 * Created by thales on 5/2/16.
 */
public interface NavigationContract {
    /**
     * Interface ViewFragment
     */
    interface View {
        Context getContext();

        void showMessageBeaconFound();
        void showMessage(String message);
        void showMessage(int idMessage);
        void showMessageBeaconError();
        void showMessageNoBluetooth();
        void hideProgress();
        void showProgress();
        void requestBluetooth();
    }

    /**
     * Interface UserActionsListener
     */
    interface UserActionsListener {
        void initScanBeacons(boolean enable);
        void requestInfoBeacons();
        void saveBeacon(BeaconApi beaconApi);
        void connectToServiceScanBeacon();
        void onDestroy();
        void onResume();
        void onPause();
        void onStart();
    }
}
