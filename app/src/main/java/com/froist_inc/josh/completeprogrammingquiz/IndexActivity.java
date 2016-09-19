package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class IndexActivity extends AppCompatActivity
{
    View mViewLoading = null;
    Button mStartQuizButton = null;
    ChislevHandlerThread mHandlerThread = null;
    int subjectsAvailable = 0;
    TextView mTextView = null;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_index );
        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        /*
        MobileAds.initialize( getApplicationContext() );
        AdView mAdView = ( AdView ) findViewById( R.id.footnote_adView );
        mAdView.setAdSize( AdSize.BANNER );

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice( AdRequest.DEVICE_ID_EMULATOR )
                .addTestDevice( "abcdefghijklmno" )
                .build();
        mAdView.loadAd( adRequest );
        */
        mViewLoading = findViewById( R.id.index_activity_layoutMain );
        assert mViewLoading != null;
        mViewLoading.setVisibility( View.VISIBLE );
        mTextView = ( TextView ) findViewById( R.id.index_activity_loadingTextView );

        mStartQuizButton = ( Button ) findViewById( R.id.index_activity_new_quizButton );
        mStartQuizButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v )
            {
                Intent startQuizIntent = new Intent( IndexActivity.this, ChislevChooseSubjectActivity.class );
                startActivity( startQuizIntent );
            }
        });
        LoadStartupConfigFile();
        mHandlerThread = new ChislevHandlerThread( this, new Handler() );
        mHandlerThread.setListener( new ChislevHandlerThread.Listener() {
            @Override
            public void OnSubjectCodeDataObtained( ChislevSubjectInformation subjectInformation )
            {
                if( subjectInformation.isAllSet() ){
                    ++subjectsAvailable;
                    Toast.makeText( IndexActivity.this, subjectInformation.getSubjectName() + " is available", 
						Toast.LENGTH_LONG ).show();
                }
                if( subjectsAvailable > 0 && !mStartQuizButton.isEnabled() ){
                    mStartQuizButton.setEnabled( true );
                }
            }
        });

        mHandlerThread.start();
        mHandlerThread.getLooper();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( mHandlerThread != null ){
            mHandlerThread.quit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.menu_index, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // ToDo
        switch ( item.getItemId() ) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void LoadStartupConfigFile()
    {
        new ChislevLoadConfigFileTask().execute();
    }

    private void UpdateMainThreadInformation()
    {
        if( ChislevSubjectsLaboratory.Get( this ).GetSubjects().size() == 0 )
        {
            Toast.makeText( IndexActivity.this, "Please make sure you're connected to the internet.", Toast.LENGTH_LONG ).show();
            mStartQuizButton.setEnabled( false );
        } else {
            for ( int i = 0; i < ChislevSubjectsLaboratory.Get( this ).GetSubjects().size(); ++i ) {
                mHandlerThread.Prepare( ChislevSubjectsLaboratory.Get( this ).GetSubjectItem( i ) );
            }
        }
        mViewLoading.setVisibility( View.INVISIBLE );
    }

    private class ChislevLoadConfigFileTask extends AsyncTask<Void, String, ArrayList<ChislevSubjectInformation>>
    {
        static final String CONFIG_URL = "https://raw.githubusercontent.com/Froist/Chislev/master/Data/config.xml";
        static final String CONFIG_FILENAME = "config.xml";
        static final String TAG = "LoadConfigFileTask";
        @Override
        protected ArrayList<ChislevSubjectInformation> doInBackground( Void... params )
        {
            File file = new File( getApplicationContext().getFilesDir(), CONFIG_FILENAME );
            String data;
            ChislevFileManager fileManager = new ChislevFileManager( IndexActivity.this );
            try {
                if( !file.exists() ){
                    publishProgress( "Please wait, trying to fetch configuration information from the web." );
                    byte[] result = new ChislevNetworkManager( IndexActivity.this ).GetData( CONFIG_URL );
                    if( result == null || result.length == 0 ) return null;

                    publishProgress( "Please wait, saving configuration file for further processing." );
                    fileManager.SaveDataToFile( result, CONFIG_FILENAME, null );
                    data = ChislevUtilities.ByteArrayToString( result );
                } else {
                    data = fileManager.ReadDataFromFile( file.getCanonicalPath() );
                }
            } catch ( IOException exception ) {
                return null;
            }
            ChislevXMLSerializer xmlSerializer = new ChislevXMLSerializer( IndexActivity.this );
            ArrayList<ChislevSubjectInformation> informationList = null;
            try {
                informationList = xmlSerializer.ParseConfigData( data );
            } catch ( XmlPullParserException exception ) {
                Log.d( TAG, "Error parsing the result sent from the network, contact your app admin.\nDetails: "
                        + exception.getLocalizedMessage(), exception );
            } catch ( IOException exception ) {
                Log.d( TAG, "Input/Output error occurred, please contact your app admin.\nDetails: "
                        + exception.getLocalizedMessage(), exception );
            }
            return informationList;
        }

        @Override
        protected void onProgressUpdate( String... values )
        {
            mTextView.setText( values[0] );
        }

        @Override
        protected void onPostExecute( ArrayList<ChislevSubjectInformation> informationList )
        {
            if( informationList != null ){
                ChislevSubjectsLaboratory.Get( IndexActivity.this ).SetSubjects( informationList );
            }
            UpdateMainThreadInformation();
        }
    }
}
