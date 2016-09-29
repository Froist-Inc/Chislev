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

        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        final int containerID = GetContainerID();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById( containerID );

        if( fragment == null ){
            fragment = GetFragment();
            fragmentManager.beginTransaction().add( containerID, fragment ).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.menu_index, menu );
        return true;
    }

    // Todo
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch( item.getItemId() ){
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }
}
