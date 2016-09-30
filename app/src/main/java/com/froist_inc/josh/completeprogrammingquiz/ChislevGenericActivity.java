package com.froist_inc.josh.completeprogrammingquiz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public abstract class ChislevGenericActivity extends AppCompatActivity
{
    protected abstract Fragment GetFragment();
    protected abstract int GetLayoutID();
    protected abstract int GetContainerID();

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( GetLayoutID() );

        final int containerID = GetContainerID();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById( containerID );

        if( fragment == null ){
            fragment = GetFragment();
            fragmentManager.beginTransaction().add( containerID, fragment ).commit();
        }
    }
}
