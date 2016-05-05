package com.ciandt.thegarage.navapp.view.adapter;

/**
 * Displays basic information about beacon.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ciandt.thegarage.navapp.R;
import com.ciandt.thegarage.navapp.model.BeaconsNavigationModel;

import java.util.List;

/**
 * Created by thales on 6/13/15.
 * City Adapter
 */
public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.ViewHolder> {
    private RecyclerViewItemClickListener mListener;
    private List<BeaconsNavigationModel> mDataSet;

    /**
     * Interface definition for a callback to be invoked when a recycler view is clicked.
     */
    public interface RecyclerViewItemClickListener {

        /**
         * Called when a view has been clicked.
         *
         * @param item A object representing data's inputs.
         */
        void onItemClick(BeaconsNavigationModel item);
    }

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param dataSet  The dataSet
     * @param listener The callback that will run
     */
    public BeaconAdapter(List<BeaconsNavigationModel> dataSet,
                         RecyclerViewItemClickListener listener) {
        this.mDataSet = dataSet;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.populate(mDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * Set items in a dataSet and after update de recycler view
     *
     * @param dataSet A dataSet list
     */
    public void replaceData(List<BeaconsNavigationModel> dataSet) {
        setList(dataSet);
        notifyDataSetChanged();
    }

    /**
     * Set items in a dataSet
     *
     * @param dataSet A dataSet list
     */
    private void setList(List<BeaconsNavigationModel> dataSet) {
        mDataSet.clear();
        mDataSet.addAll(dataSet);
    }

    /**
     * Inner Class for a recycler view
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView mDescriptionView;
        private BeaconsNavigationModel mItem;

        /**
         * Simple constructor to use when creating a view from code.
         *
         * @param view Recycle view item
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDescriptionView = (TextView) view.findViewById(R.id.description_view);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null && mItem != null) {
                        mListener.onItemClick(mItem);
                    }
                }
            });
        }

        /**
         * Populate the data in a recycle view item
         *
         * @param data A object
         */
        public void populate(BeaconsNavigationModel data) {
            mItem = data;
            mDescriptionView.setText(String.format("Local: %s", mItem.getDescribeBeacon()));
        }
    }
}