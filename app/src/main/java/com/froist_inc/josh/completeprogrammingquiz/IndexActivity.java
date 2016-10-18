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

        NavigationView navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener(this);

        mPageIndex = savedInstanceState == null ? 0 : savedInstanceState.getInt( CURRENT_INDEX );
        selectItem( mPageIndex );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if ( drawer.isDrawerOpen( GravityCompat.START ) ) {
            drawer.closeDrawer(GravityCompat.START);
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
    protected void onSaveInstanceState( Bundle outState ) {
        outState.putInt( CURRENT_INDEX, mPageIndex );
        super.onSaveInstanceState( outState );
    }

    private void selectItem( int position )
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
            default:
                break;
        }
        if( fragmentToShow != null ) {
            getSupportFragmentManager().beginTransaction().replace( R.id.drawer_mainLayout, fragmentToShow ).commit();
        }
    }
}

/*
package com.froist_inc.josh.navpage;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

 */