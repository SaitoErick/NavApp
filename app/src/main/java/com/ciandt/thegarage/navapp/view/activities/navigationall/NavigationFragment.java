package com.ciandt.thegarage.navapp.view.activities.navigationall;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ciandt.thegarage.navapp.R;
import com.ciandt.thegarage.navapp.infrastructure.Constants;


/**
 * Created by esaito on 3/31/16.
 */
public class NavigationFragment extends Fragment implements NavigationContract.View {
    private static final String TAG = NavigationFragment.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 6684;

    private ProgressBar mProgressBar;

    private NavigationContract.UserActionsListener mPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View layout = inflater.inflate(R.layout.fragment_navigation, container, false);
        mProgressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
        mPresenter = new NavigationPresenter(this);
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(Constants.TAG, "NavigationFragment: onStart");
        mPresenter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(Constants.TAG, "NavigationFragment: onResume");
        mPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(Constants.TAG, "NavigationFragment: onPause");
        mPresenter.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode != Activity.RESULT_OK) {
                showMessage(R.string.mensagem_app_nao_ira_funcionar_sem_permissao_ao_bluetooth);
            } else {
                mPresenter.connectToServiceScanBeacon();
            }
        }
    }


    @Override
    public void showMessageBeaconFound() {
        showMessage(R.string.mensagem_beacons_nao_encontrados);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this.getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showMessage(int idMessage) {
        Toast.makeText(this.getContext(), idMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showMessageBeaconError() {
        showMessage(R.string.mensagem_falha_servico_beacon);
    }

    @Override
    public void showMessageNoBluetooth() {
        showMessage(R.string.mensagem_celular_nao_tem_bluetooth);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void requestBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }
}
