package com.ciandt.thegarage.navapp.fragment;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ciandt.thegarage.navapp.Constants;
import com.ciandt.thegarage.navapp.R;
import com.ciandt.thegarage.navapp.adapter.BeaconListAdapter;
import com.ciandt.thegarage.navapp.helper.VolleySingleton;
import com.ciandt.thegarage.navapp.model.BeaconsNavigationModel;
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
public class NavigationFragment extends Fragment implements
        Response.Listener<JSONObject>, Response.ErrorListener, SensorEventListener {

    private String TAG = NavigationFragment.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    private BeaconManager mBeaconManager;
    private BeaconsNavigationModel mBeaconsNavigationModel;
    private Beacon mBeacon;
    private String mMessageBeacon;
    private String mDescriptionBeacon;

    private BeaconListAdapter adapter;
    private List<BeaconsNavigationModel> mListBeaconNavigationModel;
    private ListView mList;

    private TextView mTextMesage;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        mBeaconManager = new BeaconManager(getActivity().getApplicationContext());
        mBeaconManager.setForegroundScanPeriod(TimeUnit.SECONDS.toMillis(Constants.PERIOD_MILLIS_SCAN_RANGING), Constants.WAIT_TIME_MILLIS_SCAN_RANGING);
        callBackScannBeacons();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View layout = inflater.inflate(R.layout.fragment_navigation, container, false);

        BeaconsNavigationModel mBeaconsNavigationModel = new BeaconsNavigationModel();
        mListBeaconNavigationModel = mBeaconsNavigationModel.getAll();
        mListBeaconNavigationModel = mListBeaconNavigationModel == null ? new ArrayList<BeaconsNavigationModel>() : mListBeaconNavigationModel;

        adapter = new BeaconListAdapter(getActivity(), mListBeaconNavigationModel);
        mList = (ListView) layout.findViewById(R.id.device_list);
        mList.setAdapter(adapter);

        mProgressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
        mTextMesage = (TextView) layout.findViewById(android.R.id.empty);


        sensorMan = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

        //Verificando se o Device tem Bluetooth
        if (!mBeaconManager.hasBluetooth()) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.mensagem_celular_nao_tem_bluetooth, Toast.LENGTH_LONG).show();
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

        mBeaconsNavigationModel = new BeaconsNavigationModel();
        mListBeaconNavigationModel = mBeaconsNavigationModel.getAll();
        Log.i(TAG, "onResume::" + mListBeaconNavigationModel.toString());
        adapter.replaceWith(mListBeaconNavigationModel);

        sensorMan.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");

        sensorMan.unregisterListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        mBeaconManager.disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Verificando acesso a permissão
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode != getActivity().RESULT_OK) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.mensagem_app_nao_ira_funcionar_sem_permissao_ao_bluetooth, Toast.LENGTH_LONG).show();
                return;
            }

            // Aceito
            if (resultCode == getActivity().RESULT_OK) {
                connectToServiceScannBeacon();
            }
        }
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        try {
            JSONObject payload = jsonObject.getJSONObject("payload");
            mDescriptionBeacon = payload.getString("description");
            mMessageBeacon = payload.getString("message");

            //Toast.makeText(getActivity().getApplicationContext(), "MacAddress-" + mBeacon.getMacAddress() + "-mDescriptionBeacon-" + mDescriptionBeacon, Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity().getApplicationContext(), "Local: " + mDescriptionBeacon, Toast.LENGTH_LONG).show();

            Log.i(TAG, "mMessageBeacon=" + mMessageBeacon);
            Log.i(TAG, "mDescriptionBeacon=" + mDescriptionBeacon);
            Log.i(TAG, "getMacAddress=" + mBeacon.getMacAddress());

            mBeaconsNavigationModel = new BeaconsNavigationModel();
            //mListBeaconNavigationModel = mBeaconsNavigationModel.getAll();

            Long returnSave = mBeaconsNavigationModel.save(mBeacon.getProximityUUID().toString(),
                    mBeacon.getName(),
                    mBeacon.getMacAddress(),
                    mBeacon.getMajor(),
                    mBeacon.getMinor(),
                    mBeacon.getMeasuredPower(),
                    mBeacon.getRssi(),
                    mDescriptionBeacon,
                    mMessageBeacon);

            Log.i(TAG, "returnSave::" + returnSave);

            mListBeaconNavigationModel = mBeaconsNavigationModel.getAll();
            Log.i(TAG, "setRangingListener--onBeaconsDiscovered::" + mListBeaconNavigationModel.toString());
            adapter.replaceWith(mListBeaconNavigationModel);

            scanning = false;
            //connectToServiceScannBeacon();
            showProgress(false);
        } catch (JSONException e) {
            showProgress(false);

            scanning = false;
            //connectToServiceScannBeacon();
        }

        Log.i(TAG, String.valueOf("Scanning - FIM"));
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        showProgress(false);
        mTextMesage.setText(R.string.mensagem_falha_servico_beacon);
        mTextMesage.setText(R.string.mensagem_falha_servico_beacon);
        Toast.makeText(getActivity().getApplicationContext(), R.string.mensagem_falha_servico_beacon, Toast.LENGTH_LONG).show();
        //connectToServiceScannBeacon();
    }


    private boolean scanning = true;

    private void connectToServiceScannBeacon() {
        Collections.<Beacon>emptyList();

        mBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    mBeaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                    Log.i(TAG, "startRanging");
                } catch (RemoteException e) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.mensagem_falha_start_scann_beacon, Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Cannot start ranging", e);
                }
            }
        });
    }

    public void callBackScannBeacons() {
        mBeaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "setRangingListener--onBeaconsDiscovered:: beacons::" + beacons.toString());
                        try {
                            if (beacons != null && beacons.size() > 0) {
                                //Toast.makeText(getActivity().getApplicationContext(), R.string.mensagem_beacons_encontrados + beacons.get(0).getMacAddress(), Toast.LENGTH_LONG).show();
                                Log.i(TAG, R.string.mensagem_beacons_encontrados + beacons.get(0).getMacAddress());

                                BeaconsNavigationModel mBeaconsNavigationModel = new BeaconsNavigationModel();

                                // Verificar se o Beacon scaneado é o ultimo já cadastrado
                                //if(mBeaconsNavigationModel.beaconScannIsLastSave(beacons.get(0).getMacAddress().toString()) < 1){
                                requestInfosBeacon(beacons.get(0));
                                mBeaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
                                //} else {
                                //    Log.i(TAG, beacons.get(0).getMacAddress().toString() + " Já existe");
                                //}

                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), R.string.mensagem_beacons_nao_encontrados, Toast.LENGTH_LONG).show();
                                Log.i(TAG, String.valueOf(R.string.mensagem_beacons_nao_encontrados));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void requestInfosBeacon(Beacon beacon) {

        showProgress(true);
        mBeacon = beacon;

        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        String urlRequest = Constants.BEACONS_URL_WEBSERVICE + beacon.getMacAddress();
        JsonObjectRequest request =
                new JsonObjectRequest(
                        Request.Method.GET, // Requisição via HTTP_GET
                        urlRequest, // url da requisição
                        null, // JSONObject a ser enviado via POST
                        this, // Response.Listener
                        this // Response.ErrorListener
                );
        queue.add(request);
    }

    private void showProgress(boolean show) {

        if (show == true) {
            mTextMesage.setText(R.string.mensagem_baixando_infos_beacon);
        }

        mTextMesage.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private SensorManager sensorMan;
    private Sensor accelerometer;

    private float[] mGravity;
    private double mAccel;
    private double mAccelCurrent;
    private double mAccelLast;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values.clone();
            // Shake detection
            double x = mGravity[0];
            double y = mGravity[1];
            double z = mGravity[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = Math.sqrt(x * x + y * y + z * z);
            ;
            double delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect


            if (mAccel > 1.5) {

                Log.i(TAG, String.valueOf("Scanning : " + scanning));

                if (!scanning) {
                    Log.i(TAG, String.valueOf("Scanning - Inicio"));
                    scanning = true;
                    connectToServiceScannBeacon();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
