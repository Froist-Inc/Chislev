package com.froist_inc.josh.completeprogrammingquiz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class ChislevChooseSubjectActivity extends FragmentActivity
{
    private static final String TAG = "ChooseSubjectActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.subject_chooser_activity );

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById( R.id.subject_choose_layoutContainer );
        if( fragment == null ){
            fragment = new ChislevChooseSubjectFragment();
            fragmentManager.beginTransaction().add( fragment, TAG ).commit();
        }
    }
}
