package com.froist_inc.josh.completeprogrammingquiz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChislevSolutionsFragment extends Fragment
{
    private int mCurrentSolutionDisplayed;
    private int mNumberOfQuestionsAnswered;

    static final String SOLUTION_DISPLAYED_INDEX = "SOLUTION_INDEX";
    static final String NUMBER_OF_QUESTIONS_ANSWERED = "ANSWERED_QUESTIONS";
    private ViewPager mViewPager;

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        ChislevQuestionDisplayActivity.GetSolutionList();
        mCurrentSolutionDisplayed = savedInstanceState == null ? 0 : savedInstanceState.getInt( SOLUTION_DISPLAYED_INDEX );
        mNumberOfQuestionsAnswered = getArguments().getInt( NUMBER_OF_QUESTIONS_ANSWERED );
    }

    @Override
    public void onResume()
    {
        super.onResume();
        InitViewPagerAdapter();
    }

    @Override
    public void onSaveInstanceState( Bundle outState )
    {
        outState.putInt( SOLUTION_DISPLAYED_INDEX, mCurrentSolutionDisplayed );
        super.onSaveInstanceState( outState );
    }

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.view_pager_root_fragment, container, false );
        mViewPager = ( ViewPager ) view.findViewById( R.id.solutionViewPager );
        InitViewPagerAdapter();
        return view;
    }

    private void InitViewPagerAdapter() {
        mViewPager.setAdapter( new FragmentStatePagerAdapter( getChildFragmentManager() ) {
            @Override
            public Fragment getItem( int position ) {
                mCurrentSolutionDisplayed = position;
                return ChislevViewSolutionFragment.newInstance( position );
            }

            @Override
            public int getCount() {
                return mNumberOfQuestionsAnswered;
            }
        });
        mViewPager.setCurrentItem( mCurrentSolutionDisplayed );
    }
}
