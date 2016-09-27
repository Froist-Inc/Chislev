package com.froist_inc.josh.completeprogrammingquiz;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class IndexActivity extends AppCompatActivity
{
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mIndexTitles;
    private DrawerLayout mDrawLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_index );
        mTitle = mDrawerTitle = getTitle();

        mIndexTitles = getResources().getStringArray( R.array.index_menu_overlay_item );
        mDrawLayout = ( DrawerLayout ) findViewById( R.id.drawer_layout );
        mDrawerList = ( ListView ) findViewById( R.id.drawer_listView );

        mDrawerList.setAdapter( new ArrayAdapter<>( this, R.layout.drawer_list_layout, mIndexTitles ));
        mDrawerList.setOnItemClickListener( new DrawerItemClickListener() );

        Toolbar toolbar = ( Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeButtonEnabled( true );

        mDrawerToggle = new ActionBarDrawerToggle( this, mDrawLayout, toolbar, android.R.string.copy,
                android.R.string.cancel ){
            @Override
            public void onDrawerClosed( View drawerView) {
                getSupportActionBar().setTitle( R.string.app_name );
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle( R.string.menu_item );
                invalidateOptionsMenu();
            }
        };
        mDrawLayout.addDrawerListener( mDrawerToggle );
        if( savedInstanceState == null ){
            selectItem( 0 );
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ){
        getMenuInflater().inflate( R.menu.menu_index, menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        return mDrawerToggle.onOptionsItemSelected( item ) || super.onOptionsItemSelected( item );
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick( AdapterView<?> parent, View view, int position, long id ){
            selectItem( position );
        }
    }

    @Override
    public boolean onPrepareOptionsMenu( Menu menu )
    {
        boolean drawerOpen = mDrawLayout.isDrawerOpen( mDrawerList );
        menu.findItem( R.id.action_settings ).setVisible( !drawerOpen );
        return super.onPrepareOptionsMenu(menu);
    }

    // Todo
    private void selectItem( int position )
    {
        Fragment fragmentToShow = null;
        switch ( position ){
            case 0: // lists of quiz
                fragmentToShow = new ChislevSubjectPresenterFragments();
                break;
            // Todo
            case 1: default:
                break;
        }
        getSupportFragmentManager().beginTransaction().replace( R.id.drawer_mainLayout, fragmentToShow ).commit();
        mDrawerList.setItemChecked( position, true );
        mDrawLayout.closeDrawer( mDrawerList );
    }

    @Override
    protected void onPostCreate( @Nullable Bundle savedInstanceState ) {
        super.onPostCreate( savedInstanceState );
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged( newConfig );
        mDrawerToggle.onConfigurationChanged( newConfig );
    }
}
