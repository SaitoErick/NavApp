package com.ciandt.thegarage.navapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.ciandt.thegarage.navapp.R;
import com.ciandt.thegarage.navapp.adapter.BeaconListAdapter;
import com.ciandt.thegarage.navapp.model.BeaconsNavigationModel;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoricFragment extends Fragment {

    private String TAG = HistoricFragment.class.getSimpleName();

    private BeaconsNavigationModel mBeaconsNavigationModel;

    private BeaconListAdapter adapter;
    private List<BeaconsNavigationModel> mListBeaconNavigationModel;
    private ListView mList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View layout = inflater.inflate(R.layout.fragment_historic, container, false);

        BeaconsNavigationModel mBeaconsNavigationModel = new BeaconsNavigationModel();
        mListBeaconNavigationModel = mBeaconsNavigationModel.getAll();
        mListBeaconNavigationModel = mListBeaconNavigationModel == null ? new ArrayList<BeaconsNavigationModel>() : mListBeaconNavigationModel;

        mListBeaconNavigationModel = mBeaconsNavigationModel.getAll();
        mListBeaconNavigationModel = mListBeaconNavigationModel == null ? new ArrayList<BeaconsNavigationModel>() : mListBeaconNavigationModel;

        adapter = new BeaconListAdapter(getActivity(), mListBeaconNavigationModel);
        mList = (ListView) layout.findViewById(R.id.device_list);
        mList.setAdapter(adapter);

        adapter = new BeaconListAdapter(getActivity(), mListBeaconNavigationModel);
        mList = (ListView) layout.findViewById(R.id.device_list);
        mList.setAdapter(adapter);

        // Disable the touch to prevent the wrong speech
        layout.setEnabled(false);

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");

        mBeaconsNavigationModel = new BeaconsNavigationModel();
        mListBeaconNavigationModel = mBeaconsNavigationModel.getAll();
        Log.i(TAG, "onResume::" + mListBeaconNavigationModel.toString());
        adapter.replaceWith(mListBeaconNavigationModel);

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
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
    }


}
