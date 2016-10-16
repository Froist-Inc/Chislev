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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IndexActivity extends AppCompatActivity
{
    private DrawerLayout mDrawLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private static final String CURRENT_INDEX = "IndexCode";
    int mPageIndex;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_index );
        mDrawLayout = ( DrawerLayout ) findViewById( R.id.drawer_layout );
        mDrawerList = ( ListView ) findViewById( R.id.drawer_listView );

        mDrawerList.setAdapter( new DrawerInternalLayoutAdapter() );
        mDrawerList.setOnItemClickListener( new DrawerItemClickListener() );

        Toolbar toolbar = ( Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeButtonEnabled( true );

        mDrawerToggle = new ActionBarDrawerToggle( this, mDrawLayout, toolbar, android.R.string.copy,
                android.R.string.cancel ){
            @Override
            public void onDrawerClosed( View drawerView ) {
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened( View drawerView )
            {
                invalidateOptionsMenu();
            }
        };
        mDrawLayout.addDrawerListener( mDrawerToggle );

        mPageIndex = savedInstanceState == null ? 0 : savedInstanceState.getInt( CURRENT_INDEX );
        selectItem( mPageIndex );
    }

    @Override
    protected void onSaveInstanceState( Bundle outState ) {
        outState.putInt( CURRENT_INDEX, mPageIndex );
        super.onSaveInstanceState( outState );
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
    public boolean onCreateOptionsMenu( Menu menu ) {
        return super.onCreateOptionsMenu(menu);
    }

    private class DrawerInternalLayoutAdapter extends ArrayAdapter<String>
    {
        final int[] mItemCaptionList = new int[]{ R.string.take_quiz, R.string.scores, R.string.contribute, R.string.attributes };
        private final int ELEMENT_COUNT = mItemCaptionList.length;

        DrawerInternalLayoutAdapter()
        {
            super( IndexActivity.this, 0 );
        }

        @Override
        public int getCount() {
            return ELEMENT_COUNT;
        }

        @Override
        public View getView( final int position, View convertView, ViewGroup parent ) {
            if( convertView == null ){
                convertView = getLayoutInflater().inflate( R.layout.drawer_internal_fragment, parent, false );
            }
            TextView textView = ( TextView ) convertView.findViewById( R.id.internal_textView );
            textView.setText( getString( mItemCaptionList[ position ]));

            if( position == 0 ){
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT );
                layoutParams.setMargins( 10, 70, 10, 10 );
                textView.setLayoutParams( layoutParams );
            }

            return convertView;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu( Menu menu )
    {
        boolean drawerOpen = mDrawLayout.isDrawerOpen( mDrawerList );
        MenuItem menuItem = menu.findItem( R.id.action_settings );
        if( menuItem != null ) {
            menuItem.setVisible(!drawerOpen);
        }
        return super.onPrepareOptionsMenu( menu );
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
        mDrawerList.setItemChecked( position, true );
        mDrawLayout.closeDrawer( mDrawerList );
    }

    @Override
    protected void onPostCreate( @Nullable Bundle savedInstanceState ) {
        super.onPostCreate( savedInstanceState );
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged( Configuration newConfig )
    {
        super.onConfigurationChanged( newConfig );
        mDrawerToggle.onConfigurationChanged( newConfig );
    }
}
