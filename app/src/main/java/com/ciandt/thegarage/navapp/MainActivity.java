package com.ciandt.thegarage.navapp;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.ciandt.thegarage.navapp.adapter.MainPagerAdapter;
import com.ciandt.thegarage.navapp.view.SlidingTabLayout;

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
