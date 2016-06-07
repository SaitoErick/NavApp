package com.ciandt.thegarage.navapp.view.activities.navigationall;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

import com.ciandt.thegarage.navapp.infrastructure.Constants;
import com.ciandt.thegarage.navapp.model.BeaconsNavigationModel;
import com.ciandt.thegarage.navapp.model.api.BeaconApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thales on 5/2/16.
 */
public class NavigationPresenter implements NavigationContract.UserActionsListener,
        SensorEventListener {

    private NavigationContract.View mView;
    private String TAG = NavigationFragment.class.getSimpleName();
    private boolean mScanning = true;
    private SensorManager mSensorMan;
    private Sensor mAccelerometer;
    private double mAccel;
    private double mAccelCurrent;
    private double mAccelLast;
    private Call<BeaconApi> mCall;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;

    public NavigationPresenter(NavigationContract.View view) {
        this.mView = view;

        this.mAccel = 0.00f;
        this.mAccelCurrent = SensorManager.GRAVITY_EARTH;
        this.mAccelLast = SensorManager.GRAVITY_EARTH;

        mSensorMan = (SensorManager) this.mView.getContext().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mHandler = new Handler();

        final BluetoothManager bluetoothManager =
                (BluetoothManager) view.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    Log.d(Constants.TAG, "name");
                }
            };

    @Override
    public void initScanBeacons(boolean enable) {
        if (enable) {
            NavigationPresenter.this.mView.showProgress();

            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    @Override
    public void requestInfoBeacons() {
       /* if (beacons != null && beacons.size() > 1) {
            BeaconsApi service = RestClient.getClient(mView.getContext());
            mCall = service.getBeacon(beacons.get(0).getMacAddress());
            mCall.enqueue(new BeaconApiCallback(beacons.get(0)));
            mCall = null;
        }*/
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

    private class BeaconApiCallback implements Callback<BeaconApi> {

        public BeaconApiCallback() {
        }

        @Override
        public void onResponse(Call<BeaconApi> call, Response<BeaconApi> response) {
            BeaconApi beaconApi = response.body();

            if (beaconApi != null && beaconApi.getPayload() != null) {
                //saveBeacon(beacon, beaconApi);
            }

            mScanning = false;
            Log.i(TAG, String.valueOf("Scanning - FIM"));
        }

        @Override
        public void onFailure(Call<BeaconApi> call, Throwable t) {
            mScanning = false;
        }
    }


    @Override
    public void saveBeacon(BeaconApi beaconApi) {
//        Log.i(TAG, "messageBeacon=" + beaconApi.getPayload().getMessage());
//        Log.i(TAG, "descriptionBeacon=" + beaconApi.getPayload().getDescription());
        //Log.i(TAG, "macAddress=" + beacon.getMacAddress());

        BeaconsNavigationModel beaconsNavigationModel = new BeaconsNavigationModel();
        /*Long returnSave = beaconsNavigationModel.save(beacon.getProximityUUID().toString(),
                beacon.getName(),
                beacon.getMacAddress(),
                beacon.getMajor(),
                beacon.getMinor(),
                beacon.getMeasuredPower(),
                beacon.getRssi(),
                beaconApi.getPayload().getDescription(),
                beaconApi.getPayload().getMessage());

        Log.i(TAG, "returnSave::" + returnSave);*/

//        this.mView.showMessage(beaconApi.getPayload().getMessage());
    }

    @Override
    public void connectToServiceScanBeacon() {
        initScanBeacons(true);
        /*if (mBeaconManager.hasBluetooth() && mBeaconManager.isBluetoothEnabled()) {
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
        }*/
    }

    private void stopRanging() {
        initScanBeacons(false);
        NavigationPresenter.this.mView.hideProgress();
    }

    @Override
    public void onDestroy() {
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
        if (!this.mView.getContext().getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            this.mView.showMessageNoBluetooth();
        } else if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            this.mView.requestBluetooth();
        }

        /*if (!mBeaconManager.hasBluetooth()) {
            this.mView.showMessageNoBluetooth();
        } else if (!mBeaconManager.isBluetoothEnabled()) {
            this.mView.requestBluetooth();
        }*/
    }
}
