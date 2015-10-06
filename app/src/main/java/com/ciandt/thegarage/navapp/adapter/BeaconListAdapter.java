package com.ciandt.thegarage.navapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ciandt.thegarage.navapp.R;
import com.ciandt.thegarage.navapp.model.BeaconNavigation;
import com.ciandt.thegarage.navapp.model.BeaconsNavigationModel;
import com.estimote.sdk.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Displays basic information about beacon.
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class BeaconListAdapter extends BaseAdapter {

    private List<BeaconsNavigationModel> beacons;
    private LayoutInflater inflater;

    public BeaconListAdapter(Context context, List<BeaconsNavigationModel> BeaconsNavigationModel) {
        this.inflater = LayoutInflater.from(context);
        this.beacons = BeaconsNavigationModel;
    }

    public void replaceWith(Collection<BeaconsNavigationModel> newBeacons) {
        this.beacons.clear();
        this.beacons.addAll(newBeacons);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return beacons.size();
    }

    @Override
    public BeaconsNavigationModel getItem(int position) {
        return beacons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflateIfRequired(view, position, parent);
        bind(getItem(position), view);
        return view;
    }

    private void bind(BeaconsNavigationModel beacon, View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.beaconTextView.setText(String.format("Local: %s", beacon.getDescribeBeacon()));
    /*holder.macTextView.setText(String.format("MAC: %s (%.2fm)", beacon.getMacAddress(), Utils.computeAccuracy(beacon.getRssi(), beacon.getMeasuredPower())));
    holder.majorTextView.setText("Major: " + beacon.getMajor());
    holder.minorTextView.setText("Minor: " + beacon.getMinor());
    holder.measuredPowerTextView.setText("MPower: " + beacon.getMeasuredPower());
    holder.rssiTextView.setText("RSSI: " + beacon.getDescribeBeacon());*/
    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.beacon_item, null);
            view.setTag(new ViewHolder(view));
        }
        return view;
    }

    static class ViewHolder {
        final TextView beaconTextView;
    /*final TextView macTextView;
    final TextView majorTextView;
    final TextView minorTextView;
    final TextView measuredPowerTextView;
    final TextView rssiTextView;*/

        ViewHolder(View view) {

            beaconTextView = (TextView) view.findViewWithTag("beacon");

      /*macTextView = (TextView) view.findViewWithTag("mac");
      majorTextView = (TextView) view.findViewWithTag("major");
      minorTextView = (TextView) view.findViewWithTag("minor");
      measuredPowerTextView = (TextView) view.findViewWithTag("mpower");
      rssiTextView = (TextView) view.findViewWithTag("rssi");*/
        }
    }
}
