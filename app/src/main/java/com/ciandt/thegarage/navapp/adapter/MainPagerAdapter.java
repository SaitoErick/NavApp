package com.ciandt.thegarage.navapp.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentManager;

import com.ciandt.thegarage.navapp.R;
import com.ciandt.thegarage.navapp.fragment.DiscoveryFragment;
import com.ciandt.thegarage.navapp.fragment.HelpUsersFragment;
import com.ciandt.thegarage.navapp.fragment.HistoricFragment;
import com.ciandt.thegarage.navapp.fragment.NavigationFragment;

/**
 * Created by carlosfm on 9/22/15.
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

    private final String[] mSliddingTabHeaderArray;
    private Context mContext;

    /**
     * Constructor, create an instance of InpatientPagerAdapter.
     *
     * @param fragmentManager - FragmentManager
     * @param context         - Context
     */
    public MainPagerAdapter(final FragmentManager fragmentManager, final Context context) {
        super(fragmentManager);
        mContext = context;
        this.mSliddingTabHeaderArray = context.getResources().getStringArray(R.array.main_sliding_tab_headers);
    }

    @Override
    public int getCount() {
        return mSliddingTabHeaderArray.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mSliddingTabHeaderArray[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                NavigationFragment navigationFragment = new NavigationFragment();
                return navigationFragment;
            case 1:
                HistoricFragment historicFragment = new HistoricFragment();
                return historicFragment;
            case 2:
                HelpUsersFragment helpFragment = new HelpUsersFragment();
                return helpFragment;
            default:
                return new Fragment();
        }
    }

    /**
     * Usado para que o notifydatasetchanged chame novamente o getItem
     */
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
