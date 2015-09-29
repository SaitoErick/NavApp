package com.ciandt.thegarage.navapp.fragment;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
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
import com.ciandt.thegarage.navapp.Repository;
import com.ciandt.thegarage.navapp.TimerRun;
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
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationFragment extends Fragment implements
        Response.Listener<JSONObject>, Response.ErrorListener {

    private BeaconManager mBeaconManager;
    private String TAG = NavigationFragment.class.getSimpleName();

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
    private BeaconListAdapter adapter;
    List<BeaconsNavigationModel> mBeaconNavigation;
    ListView mList;
    BeaconsNavigationModel mBeaconsNavigationModel;

    TextView mTextMesage;
    ProgressBar mProgressBar;
    boolean mIsRunning;
    public String mMacAdressBeacon = null;

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

        mBeaconsNavigationModel = new BeaconsNavigationModel();
        mBeaconNavigation = mBeaconsNavigationModel.getAll();
        mBeaconNavigation = mBeaconNavigation == null ? new ArrayList<BeaconsNavigationModel>() : mBeaconNavigation;
        adapter = new BeaconListAdapter(getActivity(), mBeaconNavigation);
        mList = (ListView) layout.findViewById(R.id.device_list);
        mList.setAdapter(adapter);

        mProgressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
        mTextMesage = (TextView) layout.findViewById(android.R.id.empty);

        Log.i(TAG, "setRangingListener--onBeaconsDiscovered::" + mBeaconNavigation.toString());
        //list.setOnItemClickListener(createOnItemClickListener());

        return layout;
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

    public void callBackScannBeacons() {

        mBeaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {

                mMacAdressBeacon = null;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.i(TAG, "setRangingListener--onBeaconsDiscovered:: beacons::" + beacons.toString());

                        try {

                            if (beacons != null && beacons.size() > 0) {
                                if (mBeaconsNavigationModel.beaconExistByMacAddress(beacons.get(0).getMacAddress().toString()) <= 0) {

                                    //mMacAdressBeacon = beacons.get(0).getMacAddress().toString();
                                    //mBeaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
                                    //mBeaconManager.disconnect();

                                    //BEGIN TO DO REFACTOR

                                    Long returnSave = mBeaconsNavigationModel.save(beacons.get(0).getProximityUUID().toString(),
                                            beacons.get(0).getName(),
                                            beacons.get(0).getMacAddress(),
                                            beacons.get(0).getMajor(),
                                            beacons.get(0).getMinor(),
                                            beacons.get(0).getMeasuredPower(),
                                            beacons.get(0).getRssi(),
                                            "Descricao Beacon"
                                    );

                                    Toast.makeText(getActivity().getApplicationContext(), "Beacon " + beacons.get(0).getMacAddress().toString() + " salvo.", Toast.LENGTH_LONG).show();
                                    Log.i(TAG, "returnSave::" + returnSave);

                                    // Atulizar a listagem toda vez que houver um Beacon Novo
                                    // Buscar os ultimos Beacons scaneados e salvos no BD Local
                                    mBeaconNavigation = mBeaconsNavigationModel.getAll();
                                    Log.i(TAG, "setRangingListener--onBeaconsDiscovered::" + mBeaconNavigation.toString());
                                    adapter.replaceWith(mBeaconNavigation);


                                    //END TO DO REFACTOR

                                } else {
                                    Log.i(TAG, "Beacon " + beacons.get(0).getMacAddress().toString() + " já esta salvo.");
                                    Toast.makeText(getActivity().getApplicationContext(), "Beacon " + beacons.get(0).getMacAddress().toString() + " já existe no banco.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Não foi encontrado nenhum Beacon próximo.", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mBeaconNavigation = mBeaconsNavigationModel.getAll();
                        Log.i(TAG, "setRangingListener--onBeaconsDiscovered::" + mBeaconNavigation.toString());
                    }
                });

                /*if (mMacAdressBeacon != null) {
                    startRequestInfosBeacon(mMacAdressBeacon);
                }*/
            }
        });
    }

    public void startRequestInfosBeacon(String macAddress) {

        mIsRunning = true;
        showProgress(true);

        RequestQueue queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        String urlRequest = Constants.BEACONS_URL_WEBSERVICE + macAddress;
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

    @Override
    public void onResponse(JSONObject jsonObject) {
        mIsRunning = false;
        // showProgress(false);

        // Salvar os dados do Beacons no bd
        // e Reload na Listagem
        try {

            /*Long returnSave = mBeaconsNavigationModel.save(beacons.get(0).getProximityUUID().toString(),
                    beacons.get(0).getName(),
                    beacons.get(0).getMacAddress(),
                    beacons.get(0).getMajor(),
                    beacons.get(0).getMinor(),
                    beacons.get(0).getMeasuredPower(),
                    beacons.get(0).getRssi(),
                    "Descricao"
            );

            Toast.makeText(getActivity().getApplicationContext(), "Beacon " + beacons.get(0).getMacAddress().toString() + " salvo.", Toast.LENGTH_LONG).show();
            Log.i(TAG, "returnSave::" + returnSave);

            // Atulizar a listagem toda vez que houver um Beacon Novo
            // Buscar os ultimos Beacons scaneados e salvos no BD Local
            mBeaconNavigation = mBeaconsNavigationModel.getAll();
            Log.i(TAG, "setRangingListener--onBeaconsDiscovered::" + mBeaconNavigation.toString());
            adapter.replaceWith(mBeaconNavigation);*/

            connectToServiceScannBeacon();
            showProgress(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        mIsRunning = false;
        showProgress(false);
        mTextMesage.setText("Falha ao obter os dados do Beacon");
    }

    private void showProgress(boolean show) {

        if (show == true) {
            mTextMesage.setText("Baixando informações do Beacon...");
        }

        mTextMesage.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
