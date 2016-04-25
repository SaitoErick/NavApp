package com.ciandt.thegarage.navapp;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.ciandt.thegarage.navapp.adapter.BeaconListAdapter;
import com.ciandt.thegarage.navapp.adapter.MainPagerAdapter;
import com.ciandt.thegarage.navapp.model.BeaconsNavigationModel;
import com.ciandt.thegarage.navapp.view.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private MainPagerAdapter mMainPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.view_pager_main);
        mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        mViewPager.setAdapter(mMainPagerAdapter);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs_home);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.color_accent);
            }

            @Override
            public int getDividerColor(int position) {
                return getResources().getColor(R.color.white);
            }
        });

        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BeaconListAdapter adapter;
                ListView mList;
                List<BeaconsNavigationModel> mListBeaconNavigationModel;

                BeaconsNavigationModel mBeaconsNavigationModel = new BeaconsNavigationModel();
                mListBeaconNavigationModel = mBeaconsNavigationModel.getAll();
                mListBeaconNavigationModel = mListBeaconNavigationModel == null ? new ArrayList<BeaconsNavigationModel>() : mListBeaconNavigationModel;

                mListBeaconNavigationModel = mBeaconsNavigationModel.getAll();
                mListBeaconNavigationModel = mListBeaconNavigationModel == null ? new ArrayList<BeaconsNavigationModel>() : mListBeaconNavigationModel;

                adapter = new BeaconListAdapter(MainActivity.this, mListBeaconNavigationModel);
                mList = (ListView) MainActivity.this.findViewById(R.id.device_list);
                mList.setAdapter(adapter);

                adapter = new BeaconListAdapter(MainActivity.this, mListBeaconNavigationModel);
                mList = (ListView) MainActivity.this.findViewById(R.id.device_list);
                mList.setAdapter(adapter);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        slidingTabLayout.setViewPager(mViewPager);
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
        super.onDestroy();
    }

    @Override protected void onStart() {
        super.onStart();
    }

    @Override protected void onResume(){
        super.onResume();
        this.mViewPager.setCurrentItem(0);
    }

    @Override protected void onStop() {
        super.onStop();
    }
}
