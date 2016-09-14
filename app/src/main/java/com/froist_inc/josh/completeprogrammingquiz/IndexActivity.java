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
    private static String TAG = "IndexActivity";

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_index );
        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        mViewLoading = findViewById( R.id.index_activity_layoutMain );
        mViewLoading.setVisibility( View.VISIBLE );
        mTextView = ( TextView ) findViewById( R.id.index_activity_loadingTextView );

        mStartQuizButton = ( Button ) findViewById( R.id.index_activity_new_quizButton );
        mStartQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v)
            {
                //
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
        getMenuInflater().inflate(R.menu.menu_index, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        int id = item.getItemId();
        // ToDo
        if ( id == R.id.action_settings ) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void LoadStartupConfigFile()
    {
        Log.d( TAG, "Loading startup configuration file" );
        new ChislevLoadConfigFileTask().execute();
    }

    private void UpdateMainThreadInformation()
    {
        if( ChislevSubjectsLaboratory.Get( this ).GetSubjects().size() == 0 )
        {
            Toast.makeText( IndexActivity.this, "Please make sure you're connected to the internet.", Toast.LENGTH_LONG ).show();
            mStartQuizButton.setEnabled( false );
        } else {
            Toast.makeText( IndexActivity.this, "Sending the stuff to HandlerThread", Toast.LENGTH_LONG ).show();
            mTextView.setText( R.string.sending_stuff );
            for ( int i = 0; i < ChislevSubjectsLaboratory.Get( this ).GetSubjects().size(); ++i ) {
                mHandlerThread.Prepare( ChislevSubjectsLaboratory.Get( this ).GetSubjectItem( i ) );
            }
        }
        mViewLoading.setVisibility( View.INVISIBLE );
    }

    private class ChislevLoadConfigFileTask extends AsyncTask<Void, Void, ArrayList<ChislevSubjectInformation>>
    {
        static final String CONFIG_URL = "https://raw.githubusercontent.com/Froist/Chislev/master/Data/config.xml";
        static final String CONFIG_FILENAME = "config.xml";
        static final String TAG = "LoadConfigFileTask";
        @Override
        protected ArrayList<ChislevSubjectInformation> doInBackground( Void... params )
        {
            File file = new File( CONFIG_FILENAME );
            String data = null;
            ChislevFileManager fileManager = new ChislevFileManager( IndexActivity.this );
            try {
                if( !file.exists() ){
                    byte[] result = new ChislevNetworkManager( IndexActivity.this ).GetData( CONFIG_URL );
                    if( result == null || result.length == 0 ) return null;

                    Log.d( TAG, "Saving file." );
                    fileManager.SaveDataToFile( result, CONFIG_FILENAME, null );
                    data = ChislevUtilities.ByteArrayToString( result );
                    Log.d( TAG, "Displaying data obtained: " + data );
                } else {
                    data = fileManager.ReadDataFromFile( CONFIG_FILENAME );
                }
            } catch ( IOException exception ) {
                Log.d( TAG, exception.getLocalizedMessage(), exception );
                return null;
            }
            ChislevXMLSerializer xmlSerializer = new ChislevXMLSerializer( IndexActivity.this );
            ArrayList<ChislevSubjectInformation> informationList = null;
            try {
                informationList = xmlSerializer.ParseConfigData( data );
            } catch ( XmlPullParserException exception ) {
                Log.d( TAG, "Error parsing the result sent from the network. Contact your app admin: "
                        + exception.getLocalizedMessage(), exception );
            } catch ( IOException exception ) {
                Log.d( TAG, "Error parsing the result sent from the network. Contact your app admin: "
                        + exception.getLocalizedMessage(), exception );
            }
            return informationList;
        }

        @Override
        protected void onPostExecute( ArrayList<ChislevSubjectInformation> informationList )
        {
            ChislevSubjectsLaboratory.Get( IndexActivity.this ).SetSubjects( informationList );
            UpdateMainThreadInformation();
        }
    }
}
