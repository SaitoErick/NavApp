package com.ciandt.thegarage.navapp.view.activities.navigation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.RemoteException;
import android.util.Log;

import com.ciandt.thegarage.navapp.data.remote.BeaconsApi;
import com.ciandt.thegarage.navapp.data.remote.RestClient;
import com.ciandt.thegarage.navapp.infrastructure.Constants;
import com.ciandt.thegarage.navapp.model.BeaconsNavigationModel;
import com.ciandt.thegarage.navapp.model.api.BeaconApi;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thales on 5/2/16.
 */
public class NavigationPresenter implements NavigationContract.UserActionsListener,
        SensorEventListener {

    private NavigationContract.View mView;
    private BeaconManager mBeaconManager;
    private String TAG = NavigationFragment.class.getSimpleName();
    private boolean mScanning = true;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
    private SensorManager mSensorMan;
    private Sensor mAccelerometer;
    private double mAccel;
    private double mAccelCurrent;
    private double mAccelLast;
    private Call<BeaconApi> mCall;
    private Map<String, BeaconData> beacons;


    private static class BeaconData{
        private String description;
        private String message;

        public BeaconData(String description, String message) {
            this.description = description;
            this.message = message;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


    public NavigationPresenter(NavigationContract.View view) {
        this.mView = view;

        this.mAccel = 0.00f;
        this.mAccelCurrent = SensorManager.GRAVITY_EARTH;
        this.mAccelLast = SensorManager.GRAVITY_EARTH;

        mSensorMan = (SensorManager) this.mView.getContext().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        beacons = new HashMap<>();
        requestAllBeacons();

    }

    @Override
    public void initScanBeacons() {
        mBeaconManager = new BeaconManager(mView.getContext());
        mBeaconManager.setForegroundScanPeriod(TimeUnit.SECONDS.toMillis(
                Constants.PERIOD_MILLIS_SCAN_RANGING), Constants.WAIT_TIME_MILLIS_SCAN_RANGING);

        mBeaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
//                requestInfoBeacons(beacons);
                if(!NavigationPresenter.this.beacons.isEmpty() && !beacons.isEmpty()) {
                    saveBeacon(beacons.get(0));
                    stopRanging();
                }
            }
        });
    }

//    @Override
//    public void requestInfoBeacons(final List<Beacon> beacons) {
//        if (beacons != null && beacons.size() > 1) {
//            BeaconsApi service = RestClient.getClient(mView.getContext());
//            mCall = service.getBeacon(beacons.get(0).getMacAddress());
//            mCall.enqueue(new BeaconApiCallback(beacons.get(0)));
//            mCall = null;
//        }
//    }

    @Override
    public void requestAllBeacons() {
        BeaconsApi service = RestClient.getClient(mView.getContext());
        mCall = service.listAllBeacons();
        mCall.enqueue(new BeaconApiCallback());
        mCall = null;
    }

    @Override
    public void putBeaconsInList(BeaconApi beaconApi) {
        for(int i = 0; i < beaconApi.getPayload().size(); i++) {
            beacons.put(beaconApi.getPayload().get(i).getMacAddress(),
                    new BeaconData(beaconApi.getPayload().get(i).getDescription(),beaconApi.getPayload().get(i).getMessage()));
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] mGravity = event.values.clone();
            double x = mGravity[0];
            double y = mGravity[1];
            double z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = Math.sqrt(x * x + y * y + z * z);
            double delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;


            if (mAccel > 1.0) {
                Log.i(TAG, String.valueOf("Scanning : " + mScanning));

                if (!mScanning) {
                    Log.i(TAG, String.valueOf("Scanning - start"));
                    mScanning = true;
                    connectToServiceScanBeacon();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

//    private class BeaconApiCallback implements Callback<BeaconApi> {
//        private Beacon beacon;
//
//        public BeaconApiCallback(Beacon beacon) {
//            this.beacon = beacon;
//        }
//
//        @Override
//        public void onResponse(Call<BeaconApi> call, Response<BeaconApi> response) {
//            BeaconApi beaconApi = response.body();
//
//            if (beaconApi != null && beaconApi.getPayload() != null) {
//                saveBeacon(beacon, beaconApi);
//            }
//
//            mScanning = false;
//            Log.i(TAG, String.valueOf("Scanning - FIM"));
//        }
//
//        @Override
//        public void onFailure(Call<BeaconApi> call, Throwable t) {
//            mScanning = false;
//        }
//    }

    private class BeaconApiCallback implements Callback<BeaconApi> {
        public BeaconApiCallback() {
        }

        @Override
        public void onResponse(Call<BeaconApi> call, Response<BeaconApi> response) {
            BeaconApi beaconApi = response.body();

            if (beaconApi != null && beaconApi.getPayload() != null) {
                putBeaconsInList(beaconApi);
            }
        }

        @Override
        public void onFailure(Call<BeaconApi> call, Throwable t) {
            mScanning = false;
        }
    }


    @Override
    public void saveBeacon(Beacon beacon) {
        Log.i(TAG, "messageBeacon=" + beacons.get(beacon.getMacAddress()));
//        Log.i(TAG, "descriptionBeacon=" + beaconApi.getPayload().getDescription());
        Log.i(TAG, "macAddress=" + beacon.getMacAddress());

        BeaconsNavigationModel beaconsNavigationModel = new BeaconsNavigationModel();
        if(beacons.containsKey(beacon.getMacAddress())) {
            Long returnSave = beaconsNavigationModel.save(beacon.getProximityUUID().toString(),
                    beacon.getName(),
                    beacon.getMacAddress(),
                    beacon.getMajor(),
                    beacon.getMinor(),
                    beacon.getMeasuredPower(),
                    beacon.getRssi(),
                    beacons.get(beacon.getMacAddress()).getDescription(),
                    beacons.get(beacon.getMacAddress()).getMessage());

            Log.i(TAG, "returnSave::" + returnSave);
            mScanning = false;
            this.mView.showMessage(beacons.get(beacon.getMacAddress()).getMessage());
        } else {
            mScanning = false;
        }
    }

    @Override
    public void connectToServiceScanBeacon() {
        if (mBeaconManager.hasBluetooth() && mBeaconManager.isBluetoothEnabled()) {
            mBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    try {
                        mBeaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                        NavigationPresenter.this.mView.showProgress();
                        Log.i(TAG, "startRanging");
                    } catch (RemoteException e) {
                        NavigationPresenter.this.mView.showMessageBeaconError();
                        Log.i(TAG, "Cannot start ranging", e);
                    }
                }
            });
        }
    }

    private void stopRanging() {
        try {
            mBeaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
            NavigationPresenter.this.mView.hideProgress();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        mBeaconManager.disconnect();
    }

    @Override
    public void onResume() {
        if (mCall != null) {
            mCall.cancel();
        }

        connectToServiceScanBeacon();
        mSensorMan.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        stopRanging();
        mSensorMan.unregisterListener(this);
    }

    @Override
    public void onStart() {
        if (!mBeaconManager.hasBluetooth()) {
            this.mView.showMessageNoBluetooth();
        } else if (!mBeaconManager.isBluetoothEnabled()) {
            this.mView.requestBluetooth();
        }
    }
}
