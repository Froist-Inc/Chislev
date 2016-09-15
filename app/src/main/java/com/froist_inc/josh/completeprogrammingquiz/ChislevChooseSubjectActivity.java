package com.froist_inc.josh.completeprogrammingquiz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class ChislevChooseSubjectActivity extends FragmentActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.subject_chooser_activity);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById( R.id.subject_choose_layoutContainer );
        if( fragment == null ){
            fragment = CreateFragment();
            fragmentManager.beginTransaction().add( R.id.subject_choose_layoutContainer, fragment ).commit();
        }
    }

    Fragment CreateFragment()
    {
        return ChislevChooseSubjectFragment.NewInstance();
    }
}
