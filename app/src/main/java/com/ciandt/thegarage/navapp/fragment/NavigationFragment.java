package com.ciandt.thegarage.navapp.fragment;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ciandt.thegarage.navapp.Constants;
import com.ciandt.thegarage.navapp.MainActivity;
import com.ciandt.thegarage.navapp.R;
import com.ciandt.thegarage.navapp.Repository;
import com.ciandt.thegarage.navapp.TimerRun;
import com.ciandt.thegarage.navapp.adapter.MainPagerAdapter;
import com.ciandt.thegarage.navapp.model.BeaconsNavigationModel;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationFragment extends Fragment {

    private BeaconManager mBeaconManager;
    private String TAG = NavigationFragment.class.getSimpleName();

    private static final int NOTIFICATION_ID = 123;

    // Y positions are relative to height of bg_distance image.
    private static final double RELATIVE_START_POS = 320.0 / 1110.0;
    private static final double RELATIVE_STOP_POS = 885.0 / 1110.0;

    private BeaconManager beaconManager;

    private Beacon beacon;
    private Region region;
    private List<BeaconsNavigationModel> mListBeaconsNavigationModel;

    private int startY = -1;
    private int segmentLength = -1;

    private int beaconsSize = 0;
    private ArrayList<Beacon> beacons;

    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
    private Repository repository;
    private TimerRun timerRun;

    public NavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        mBeaconManager = new BeaconManager(getActivity().getApplicationContext());
        mBeaconManager.setForegroundScanPeriod(TimeUnit.SECONDS.toMillis(Constants.PERIOD_MILLIS_SCAN_RANGING), Constants.WAIT_TIME_MILLIS_SCAN_RANGING);
        callBackScannBeacons();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_navigation2, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

        //Verificando se o Device tem Bluetooth
        if (!mBeaconManager.hasBluetooth()) {
            Toast.makeText(getActivity().getApplicationContext(), "O aplicativo não irá funcionar, pois seu Celular não tem Bluetooth.", Toast.LENGTH_LONG).show();
            return;
        }

        //Verificando se o Bluetooth está habilitando
        if (!mBeaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToServiceScannBeacon();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        try {
            //Stop busca de Beacon
            mBeaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {
            Log.d(TAG, "Error while stopping ranging", e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBeaconManager.disconnect();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Verificando acesso a permissão
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode != getActivity().RESULT_OK) {
                Toast.makeText(getActivity().getApplicationContext(), "O aplicativo não irá funcionar, pois não foi dada a permissão de habilitar o Bluetooth.", Toast.LENGTH_LONG).show();
                return;
            }

            // Aceito
            if (resultCode == getActivity().RESULT_OK) {
                connectToServiceScannBeacon();
                //timerRun.start();
            }
        }
    }

    private void connectToServiceScannBeacon() {
        Collections.<Beacon>emptyList();
        mBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    mBeaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                    Log.i(TAG, "startRanging");
                } catch (RemoteException e) {
                    Toast.makeText(getActivity().getApplicationContext(), "Cannot start ranging, something terrible happened", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Cannot start ranging", e);
                }
            }
        });
    }

    public void callBackScannBeacons(){

        mBeaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.i(TAG, "setRangingListener--onBeaconsDiscovered:: beacons::" + beacons.toString());
                        BeaconsNavigationModel mBeaconsNavigationModel = new BeaconsNavigationModel();

                        try {

                            if (beacons != null && beacons.size() > 0) {
                                Toast.makeText(getActivity().getApplicationContext(), "macAddress:" + beacons.get(0).getMacAddress().toString(), Toast.LENGTH_LONG).show();

                                if (mBeaconsNavigationModel.beaconExistByMacAddress(beacons.get(0).getMacAddress().toString()) <= 0) {
                                    Long returnSave = mBeaconsNavigationModel.save(beacons.get(0).getMacAddress().toString(), beacons.get(0).getRssi());
                                    Log.i(TAG, "returnSave::" + returnSave);
                                } else {
                                    Log.i(TAG, "Beacon " + beacons.get(0).getMacAddress().toString() + " exist not save in DataBase Local");
                                    Toast.makeText(getActivity().getApplicationContext(), "Beacon " + beacons.get(0).getMacAddress().toString() + " exist not save in DataBase Local", Toast.LENGTH_LONG).show();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //mListBeaconsNavigationModel = mBeaconsNavigationModel.getAll();
                        //Log.i(TAG, "setRangingListener--onBeaconsDiscovered::" + beacons.toString());
                    }
                });
            }
        });
    }
}
