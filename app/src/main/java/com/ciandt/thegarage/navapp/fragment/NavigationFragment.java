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
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationFragment extends Fragment {

    private BeaconManager mBeaconManager;
    private static final String TAG = NavigationFragment.class.getSimpleName();

    private static final int NOTIFICATION_ID = 123;

    // Y positions are relative to height of bg_distance image.
    private static final double RELATIVE_START_POS = 320.0 / 1110.0;
    private static final double RELATIVE_STOP_POS = 885.0 / 1110.0;

    private BeaconManager beaconManager;

    private Beacon beacon;
    private Region region;

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
        // Inflate the layout for this fragment
        //timerRun = new TimerRun(this);
        //repository = new Repository(this);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_navigation2, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        //beaconManager.setForegroundScanPeriod(TimeUnit.SECONDS.toMillis(Constants.PERIOD_MILLIS_SCAN_RANGING), Constants.WAIT_TIME_MILLIS_SCAN_RANGING);

        // CallBack Scan Beacons
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.i(TAG, "setRangingListener--onBeaconsDiscovered");
                        JSONObject jsonObj = new JSONObject();

                        try {

                            Log.i(TAG, "setRangingListener--onBeaconsDiscovered::" + beacons.toString());

                            if (beacons != null && beacons.size() > 0) {
                                Toast.makeText(getActivity().getApplicationContext(), "macAddress:" + beacons.get(0).getMacAddress().toString(), Toast.LENGTH_LONG).show();

                                jsonObj.put("macAddress", beacons.get(0).getMacAddress());
                                jsonObj.put("rssi", beacons.get(0).getRssi());

                                repository.put(Constants.BEACON_ANTERIOR_KEY, repository.get(Constants.BEACON_ATUAL_KEY));
                                repository.put(Constants.BEACON_ATUAL_KEY, jsonObj.toString());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


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
            //timerRun.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        /*try {
            //Stop busca de Beacon
            mBeaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {
            Log.d(TAG, "Error while stopping ranging", e);
        }*/
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBeaconManager.disconnect();
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
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                } catch (RemoteException e) {
                    Toast.makeText(getActivity().getApplicationContext(), "Cannot start ranging, something terrible happened", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }
}
