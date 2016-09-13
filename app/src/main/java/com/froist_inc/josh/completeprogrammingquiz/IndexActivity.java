package com.froist_inc.josh.completeprogrammingquiz;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 https://raw.githubusercontent.com/Froist/Chislev/master/Data/config.xml
 */

public class IndexActivity extends AppCompatActivity
{
    View mViewLoading = null;
    Button mStartQuizButton = null;
    ArrayList<ChislevSubjectInformation> mChislevSubjectInformationList = null;
    ChislevHandlerThread mHandlerThread = null;
    int subjectsAvailable = 0;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_index );
        Toolbar toolbar = ( Toolbar ) findViewById(R.id.toolbar);
        setSupportActionBar( toolbar );

        mViewLoading = findViewById( R.id.index_activity_layoutMain );
        mViewLoading.setVisibility( View.VISIBLE );
        mStartQuizButton = ( Button ) findViewById( R.id.index_activity_new_quizButton );
        mStartQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Todo
                Using all the information gotten, start an activity hosting a gridView populated fragment
                and display all the available subjects.
                 */
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
        new ChislevLoadConfigFileTask().execute();
    }

    private void UpdateMainThreadInformation()
    {
        if( mChislevSubjectInformationList == null || mChislevSubjectInformationList.size() == 0 )
        {
            // Todo : Display dialog prompting user to allow internet use
            // such that if error persists, contact admin
            mStartQuizButton.setEnabled( false );
        } else {
            for (int i = 0; i < mChislevSubjectInformationList.size(); ++i ) {
                mHandlerThread.Prepare( mChislevSubjectInformationList.get( i ) );
            }
        }
        mViewLoading.setVisibility( View.INVISIBLE );
    }

    private class ChislevLoadConfigFileTask extends AsyncTask<Void, Void, ArrayList<ChislevSubjectInformation>>
    {
        static final String CONFIG_URL = "https://raw.githubusercontent.com/Froist/Chislev/master/Data/config.xml";
        static final String CONFIG_FILENAME = "config.xml";

        @Override
        protected ArrayList<ChislevSubjectInformation> doInBackground(Void... params)
        {
            File file = new File( CONFIG_FILENAME );
            String data = null;
            ChislevFileManager fileManager = new ChislevFileManager( IndexActivity.this );
            try {
                if( !file.exists() ){
                    byte[] result = new ChislevNetworkManager( IndexActivity.this ).GetData( CONFIG_URL );
                    if( result == null || result.length == 0 ) return null;

                    fileManager.SaveDataToFile( result, CONFIG_FILENAME, null );
                    data = ChislevUtilities.ByteArrayToString( result );
                } else {
                    data = fileManager.ReadDataFromFile( CONFIG_FILENAME );
                }
            } catch ( IOException exception ) {
                // Todo
            }
            ChislevXMLSerializer xmlSerializer = new ChislevXMLSerializer( IndexActivity.this );
            ArrayList<ChislevSubjectInformation> informationList = null;
            try {
                informationList = xmlSerializer.ParseConfigData( data );
            } catch ( XmlPullParserException exception ) {
                Toast.makeText( IndexActivity.this, "Error parsing the result sent from the network. Contact your app admin: "
                        + exception.getLocalizedMessage(), Toast.LENGTH_LONG );
            } catch ( IOException exception ) {
                Toast.makeText( IndexActivity.this, "Error parsing the result sent from the network. Contact your app admin: "
                        + exception.getLocalizedMessage(), Toast.LENGTH_LONG );
            }
            return informationList;
        }

        @Override
        protected void onPostExecute( ArrayList<ChislevSubjectInformation> informationList )
        {
            mChislevSubjectInformationList = informationList;
            UpdateMainThreadInformation();
        }
    }
}
