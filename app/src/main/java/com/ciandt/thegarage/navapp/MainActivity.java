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


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    // Y positions are relative to height of bg_distance image.
    private static final double RELATIVE_START_POS = 320.0 / 1110.0;
    private static final double RELATIVE_STOP_POS = 885.0 / 1110.0;

    private BeaconManager beaconManager;

    private Beacon beacon;
    private Region region;

    private int startY = -1;
    private int segmentLength = -1;

    private int beaconsSize = 0;
    private TextView restJson;
    private ArrayList<Beacon> beacons;

    public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
    public static final String EXTRAS_BEACON = "extrasBeacon";
    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
    private static final String UrlService = "http://citbeacons.appspot.com/ws/beacon/findByMacAddress/macAddress=";
    String beaconMacAddress = "";
    String ultimoBeaconMacAddress ="";
    String proximoBeaconNaArea = "";
    String beaconsAntecessores = "";
    String mensagemBeacon = "";
    String mensagemFinalAoUsuario = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        restJson = (TextView) findViewById(R.id.textView);
        beaconManager = new BeaconManager(this);

        final ArrayList<String> beaconsAntecessoresLista= new ArrayList<>();
        final LinkedHashMap<String,String> mensagensBeaconsAntecessoresChaveValor = new LinkedHashMap<>();

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (beacons.size() > 0) {
                            beaconMacAddress = beacons.get(0).getMacAddress();

                            /*if (beaconMacAddress.equals("D7:A5:E8:49:E2:17")) {
                                beaconsAntecessores = "F7:C1:D2:76:F4:B6";
                                mensagemBeacon = "CaminhoFeliz:Bem vindo a o escritorio, vire a esquerda e desça as escadas|CaminhoAlternativo:Obrigado por ter vindo.";
                            } else {
                                beaconsAntecessores = "";
                                mensagemBeacon = "CaminhoFeliz:Bem vindo a recepção";
                            }*/

                            if (!beaconMacAddress.equals(ultimoBeaconMacAddress)) {
                                JsonObjectRequest request = new JsonObjectRequest(UrlService + beaconMacAddress, null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONObject jsonResult = response.getJSONObject("payload");
                                                    beaconsAntecessores = jsonResult.getString("description");
                                                    mensagemBeacon = jsonResult.getString("message");

                                                    String splitTemp[] = beaconsAntecessores.split("\\;");
                                                    for (int i = 0; i < splitTemp.length; i++) {
                                                        beaconsAntecessoresLista.add(splitTemp[i]);
                                                    }

                                                    splitTemp = mensagemBeacon.split("\\|");
                                                    for (int i = 0; i < splitTemp.length; i++) {
                                                        String splitTemp2[] = splitTemp[i].split(":");
                                                        mensagensBeaconsAntecessoresChaveValor.put(splitTemp2[0], splitTemp2[1]);
                                                    }

                                                    restJson.setText("Beacon Atual: " + beaconMacAddress + "\n\nBeacon Anterior: " + ultimoBeaconMacAddress
                                                            +"\n\n Mensagem: \n "  );

                                                    if(!beaconsAntecessoresLista.contains(ultimoBeaconMacAddress)) {
                                                        mensagemFinalAoUsuario = mensagensBeaconsAntecessoresChaveValor.get("CaminhoEntrada");
                                                    } else {
                                                        mensagemFinalAoUsuario = mensagensBeaconsAntecessoresChaveValor.get("CaminhoSaida");
                                                    }

                                                    restJson.setText("Beacon Atual: " + beaconMacAddress + "\n\nBeacon Anterior: " + ultimoBeaconMacAddress
                                                            + "\n\n Mensagem: \n " + mensagemFinalAoUsuario);

                                                    Toast.makeText(getApplicationContext(), mensagemFinalAoUsuario, Toast.LENGTH_LONG).show();

                                                    if (!beaconMacAddress.equals(ultimoBeaconMacAddress)) {
                                                        ultimoBeaconMacAddress = beaconMacAddress;
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },

                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                restJson.setText(error.toString());
                                            }
                                        }


                                );

                                VolleyApplication.getInstance().getRequestQueue().add(request);

                            }


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
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
            return;
        }

        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToService();
        }
    }

    @Override protected void onStop() {
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {
            Log.d(TAG, "Error while stopping ranging", e);
        }
        super.onStop();
    }

    private void connectToService() {
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
