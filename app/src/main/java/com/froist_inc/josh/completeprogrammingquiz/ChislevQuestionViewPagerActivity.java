package com.froist_inc.josh.completeprogrammingquiz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

public class ChislevQuestionViewPagerActivity extends FragmentActivity
{
    private ArrayList<ChislevQuestion> mQuestionList;

    // Todo --> Make sure you retain instance state on rotation, also grab the questions and more
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        ViewPager viewPager = new ViewPager( this );
        viewPager.setId( R.id.viewPager );
        setContentView( viewPager );

        viewPager.setAdapter(new FragmentStatePagerAdapter( getSupportFragmentManager() ) {
            @Override
            public Fragment getItem(int position) {
                return null;
            }

            @Override
            public int getCount() {
                return 0;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position)
            {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        /* Todo
        * Set correct current page
        */
    }

    // Todo --> save instance state
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState( outState );
    }
}
