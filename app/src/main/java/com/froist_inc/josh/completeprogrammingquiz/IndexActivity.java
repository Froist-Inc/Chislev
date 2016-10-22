package com.froist_inc.josh.completeprogrammingquiz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class IndexActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private static final String CURRENT_INDEX = "IndexCode";
    int mPageIndex;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_index );
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar( toolbar );

        DrawerLayout drawerLayout = ( DrawerLayout ) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle( this, drawerLayout, toolbar,
                android.R.string.copy, android.R.string.paste );
        drawerLayout.addDrawerListener( mDrawerToggle );
        mDrawerToggle.syncState();

        NavigationView navigationView = ( NavigationView ) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener(this);

        mPageIndex = savedInstanceState == null ? 0 : savedInstanceState.getInt( CURRENT_INDEX );
        selectItem( mPageIndex );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = ( DrawerLayout ) findViewById( R.id.drawer_layout );
        if ( drawer.isDrawerOpen( GravityCompat.START ) ) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected( MenuItem item )
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch( id )
        {
            case R.id.new_quiz:
                selectItem( 0 );
                break;
            case R.id.scores:
                selectItem( 1 );
                break;
            case R.id.contribute:
                selectItem( 2 );
                break;
            case R.id.appreciation: default:
                selectItem( 3 );
                break;
        }
        DrawerLayout drawer = ( DrawerLayout ) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

    @Override
    protected void onSaveInstanceState( Bundle outState )
    {
        outState.putInt( CURRENT_INDEX, mPageIndex );
        super.onSaveInstanceState( outState );
    }

    private void selectItem( final int position )
    {
        Fragment fragmentToShow = null;

        mPageIndex = position;
        switch ( position ){
            case 0: // list of quiz
                fragmentToShow = new ChislevSubjectPresenterFragments();
                break;
            case 1: // scores
                fragmentToShow = new ChislevScoresFragment();
                break;
            case 2: // contribution
                fragmentToShow = new ChislevContributionFragment();
                break;
            case 3: // appreciation
                fragmentToShow = new ChislevAppreciationFragment();
                break;
            default:
                break;
        }
        if( fragmentToShow != null ) {
            getSupportFragmentManager().beginTransaction().replace( R.id.drawer_mainLayout, fragmentToShow ).commit();
        }
    }
}