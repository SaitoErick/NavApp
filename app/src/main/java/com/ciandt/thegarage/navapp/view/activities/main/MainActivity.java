package com.ciandt.thegarage.navapp.view.activities.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.ciandt.thegarage.navapp.infrastructure.Constants;
import com.ciandt.thegarage.navapp.R;
import com.ciandt.thegarage.navapp.view.activities.help.HelpUsersFragment;
import com.ciandt.thegarage.navapp.view.activities.navigation.NavigationFragment;
import com.ciandt.thegarage.navapp.view.activities.historic.HistoricFragment;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private Fragment[] mfragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        setFindViewById();
        setViewProperties();
        mfragments = new Fragment[2];

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_ACCESS_COARSE_LOCATION);
            }
        }
    }

    /**
     * Initialize Toolbar
     */
    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Initialize views by id
     */
    private void setFindViewById() {
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
    }

    /**
     * View properties
     */
    private void setViewProperties() {
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.title_navigation));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.title_historic));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.title_help));

        mViewPager.setAdapter(new SectionsAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position < 2 && mfragments[position] != null) {
                    mfragments[position].onResume();
                }

                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(Constants.TAG, "permission was granted: PERMISSIONS_ACCESS_COARSE_LOCATION");
                } else {
                    Log.d(Constants.TAG, "permission denied: PERMISSIONS_ACCESS_COARSE_LOCATION");
                }
            }
        }
    }

    /**
     * Sections of the view pager
     */
    private class SectionsAdapter extends FragmentPagerAdapter {
        public SectionsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;

            switch (position) {
                case 0:
                    fragment = new NavigationFragment();
                    mfragments[0] = fragment;
                    break;
                case 1:
                    fragment = new HistoricFragment();
                    mfragments[1] = fragment;
                    break;
                default:
                    fragment = new HelpUsersFragment();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
