package com.ciandt.thegarage.navapp.view.activities.historic;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ciandt.thegarage.navapp.R;
import com.ciandt.thegarage.navapp.view.adapter.BeaconAdapter;
import com.ciandt.thegarage.navapp.model.BeaconsNavigationModel;
import com.ciandt.thegarage.navapp.view.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoricFragment extends Fragment
        implements BeaconAdapter.RecyclerViewItemClickListener {

    private String TAG = HistoricFragment.class.getSimpleName();

    private BeaconAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_historic, container, false);

        initRecyclerView(view);

        return view;
    }

    private void initRecyclerView(View view) {
        this.mAdapter = new BeaconAdapter(new ArrayList<BeaconsNavigationModel>(), this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL_LIST,
                        DividerItemDecoration.DividerStyle.Default));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");

        BeaconsNavigationModel beaconsNavigationModel = new BeaconsNavigationModel();
        List<BeaconsNavigationModel> models = beaconsNavigationModel.getAll();

        if (models != null) {
            Log.i(TAG, "onResume::" + models.toString());
            this.mAdapter.replaceData(models);
        }
    }

    @Override
    public void onItemClick(BeaconsNavigationModel item) {

    }
}
