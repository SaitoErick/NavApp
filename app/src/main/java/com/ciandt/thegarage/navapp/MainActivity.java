package com.ciandt.thegarage.navapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beaconManager = new BeaconManager(this);

        timerRun = new TimerRun(this);
        repository = new Repository(this);

        beaconManager.setForegroundScanPeriod(TimeUnit.SECONDS.toMillis(Constants.PERIOD_MILLIS_SCAN_RANGING), Constants.WAIT_TIME_MILLIS_SCAN_RANGING);

        // CallBack Scan Beacons
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.i(TAG, "setRangingListener--onBeaconsDiscovered");
                        JSONObject jsonObj = new JSONObject();

                        try {

                            Log.i(TAG, "setRangingListener--onBeaconsDiscovered::" + beacons.toString());

                            if(beacons != null && beacons.size() > 0){
                                Toast.makeText(getApplicationContext(), "macAddress:" + beacons.get(0).getMacAddress().toString(), Toast.LENGTH_LONG).show();

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override protected void onDestroy() {
        beaconManager.disconnect();
        super.onDestroy();
    }

    @Override protected void onStart() {
        super.onStart();

        //Verificando se o Device tem Bluetooth
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(this, "O aplicativo não irá funcionar, pois seu Celular não tem Bluetooth.", Toast.LENGTH_LONG).show();
            return;
        }

        //Verificando se o Bluetooth está habilitando
        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToServiceScannBeacon();
            //timerRun.start();
        }
    }

    @Override protected void onResume(){
        super.onResume();
    }

    @Override protected void onStop() {
        /*try {
            //Stop busca de Beacon
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {
            Log.d(TAG, "Error while stopping ranging", e);
        }*/
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Verificando acesso a permissão
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(MainActivity.this, "O aplicativo não irá funcionar, pois não foi dada a permissão de habilitar o Bluetooth.", Toast.LENGTH_LONG).show();
                return;
            }

            // Aceito
            if (resultCode == RESULT_OK) {
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
                    Toast.makeText(MainActivity.this, "Cannot start ranging, something terrible happened", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }
}
