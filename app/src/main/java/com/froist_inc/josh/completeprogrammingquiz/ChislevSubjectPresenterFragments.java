package com.froist_inc.josh.completeprogrammingquiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ChislevSubjectPresenterFragments extends Fragment
{
    private static final String CONFIG_FILENAME = "config.xml";
    private static final String CONFIG_URL = "https://raw.githubusercontent.com/Froist/Chislev/master/Data/config.xml";

    private ChislevFileManager mFileManager = null;
    private View mViewLoading;
    private Button mGoOnlineButton;
    private GridView mGridView;
    private ChislevHandlerThread mHandlerThread = null;

    static public final int LEVEL_REQUEST_CODE = 1;
    static private final String TAG = "SubjectPresenterFragments";

    private int mSubjectIndex = -1;
    private int mSubjectDifficultyLevel = -1;

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        getActivity().setTitle( R.string.available_subjects );

        mFileManager = new ChislevFileManager( getActivity() );
        InitializeHandler();
    }

    private void InitializeHandler()
    {
        if( mHandlerThread == null ){
            mHandlerThread = new ChislevHandlerThread( getActivity(), new Handler() );
            mHandlerThread.setListener( new ChislevHandlerThread.Listener() {
                @Override
                public void OnSubjectCodeDataObtained( ChislevSubjectInformation subjectInformation )
                {
                    if( mViewLoading != null ){
                        mViewLoading.setVisibility( View.INVISIBLE );
                    }
                    if( subjectInformation.isAllSet() ){
                        mGridView.setAdapter( new ChislevSubjectAdapter( ChislevSubjectsLaboratory.Get( getActivity() ).GetSubjects() ));
                    }
                }
            });

            mHandlerThread.start();
            mHandlerThread.getLooper();
        } else {
            if( !mHandlerThread.isAlive() ){
                mHandlerThread.start();
                mHandlerThread.getLooper();
            }
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if( mHandlerThread != null ){
            mHandlerThread.quit();
            mHandlerThread = null;
        }
    }

    private void LoadStartupConfigFile()
    {
        new ChislevLoadConfigFileTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState )
    {
        View layoutView = inflater.inflate( R.layout.subject_chooser_fragment, container, false );

        mGridView = ( GridView ) layoutView.findViewById( R.id.subject_choose_gridView );
        View mEmptyView = layoutView.findViewById( R.id.empty );
        mGridView.setEmptyView( mEmptyView );
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSubjectIndex = position;
                ChislevChooseLevelDialog levelDialog = new ChislevChooseLevelDialog();
                levelDialog.setTargetFragment( ChislevSubjectPresenterFragments.this, LEVEL_REQUEST_CODE );
                levelDialog.show( getActivity().getSupportFragmentManager(), ChislevChooseLevelDialog.TAG );
            }
        });

        mViewLoading = layoutView.findViewById( R.id.subject_chooser_layoutMain );
        mViewLoading.setVisibility( View.VISIBLE );
        mGoOnlineButton = ( Button ) layoutView.findViewById( R.id.go_onlineButton );
        mGoOnlineButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                mViewLoading.setVisibility( View.VISIBLE );
                mGoOnlineButton.setEnabled( false );
                final ChislevLoadConfigFileTask asyncTask = new ChislevLoadConfigFileTask();
                asyncTask.execute();
            }
        });
        LoadStartupConfigFile();
        return layoutView;
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        super.onActivityResult( requestCode, resultCode, data );
        if( resultCode == Activity.RESULT_OK ) {
            if ( requestCode == LEVEL_REQUEST_CODE ){
                mSubjectDifficultyLevel = data.getIntExtra( ChislevChooseLevelDialog.DIFFICULTY_LEVEL, -1 );
                Fragment dialogFragment = getActivity().getSupportFragmentManager()
                        .findFragmentByTag( ChislevChooseLevelDialog.TAG );
                if( dialogFragment != null ){
                    ChislevChooseLevelDialog dialog = ( ChislevChooseLevelDialog ) dialogFragment;
                    dialog.dismiss();
                }
                DisplayQuestionPage();
            }
        }
    }

    private void DisplayQuestionPage()
    {
        final Intent questionIntent = ChislevQuestionDisplayFragment.GetQuestionDisplayIntent( getActivity(),
                mSubjectIndex, mSubjectDifficultyLevel );
        startActivity( questionIntent );
    }

    private class ChislevSubjectAdapter extends ArrayAdapter<ChislevSubjectInformation>
    {
        final ArrayList<ChislevSubjectInformation> mList;
        ChislevSubjectAdapter( ArrayList<ChislevSubjectInformation> informationList )
        {
            super( getActivity(), 0, informationList );
            mList = informationList;
            mViewLoading.setVisibility( View.INVISIBLE );
            mGoOnlineButton.setEnabled( true );
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public View getView( int position, View convertView, ViewGroup parent )
        {
            ChislevSubjectInformation subjectInformation = mList.get( position );
            if( convertView == null ){
                convertView = getActivity().getLayoutInflater().inflate( R.layout.element_layout_fragment, parent, false );
            }
            ImageView imageView = ( ImageView ) convertView.findViewById( R.id.subject_item_imageView );
            TextView textCaption = ( TextView ) convertView.findViewById( R.id.subject_item_captionTextView );
            textCaption.setText( subjectInformation.getSubjectName() );

            if( subjectInformation.getSubjectIconUrl() == null )
            {
                imageView.setImageResource( R.drawable.default_icon );
            } else {
                File path = new File( getActivity().getFilesDir(), subjectInformation.getSubjectCode() );
                File file = new File( path, subjectInformation.getIconFilename() );
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile( file.getCanonicalPath() );
                    imageView.setImageBitmap( bitmap );
                } catch ( IOException exception ){
                    imageView.setImageResource( R.drawable.default_icon );
                }
            }
            return convertView;
        }
    }

    private class ChislevLoadConfigFileTask extends AsyncTask<Void, String, ArrayList<ChislevSubjectInformation>>
    {
        static final String TAG = "LoadConfigFileTask";
        @Override
        protected ArrayList<ChislevSubjectInformation> doInBackground( Void... params )
        {
            File file = new File( getActivity().getFilesDir(), CONFIG_FILENAME );
            String data;
            ChislevFileManager fileManager = new ChislevFileManager( getActivity() );
            ArrayList<ChislevSubjectInformation> informationList = new ArrayList<>();

            try {
                if( !file.exists() ){
                    byte[] result = new ChislevNetworkManager( getActivity() ).GetData( CONFIG_URL );
                    if( result == null || result.length == 0 ) return null;

                    fileManager.SaveDataToFile( result, CONFIG_FILENAME, null );
                    data = ChislevUtilities.ByteArrayToString( result );
                } else {
                    data = fileManager.ReadDataFromFile( file.getCanonicalPath() );
                }
            } catch ( IOException exception ) {
                return informationList;
            }
            ChislevXMLSerializer xmlSerializer = new ChislevXMLSerializer( getActivity() );
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
        protected void onPostExecute( ArrayList<ChislevSubjectInformation> informationList )
        {
            ChislevSubjectsLaboratory.Get( getActivity() ).SetSubjects( informationList );
            UpdateMainThreadInformation();
        }
    }

    private void UpdateMainThreadInformation()
    {
        if( ChislevSubjectsLaboratory.Get( getActivity() ).GetSubjects().size() == 0 ) {
            mGridView.setAdapter( new ChislevSubjectAdapter( new ArrayList<ChislevSubjectInformation>() ) );
        } else {
            for ( int i = 0; i < ChislevSubjectsLaboratory.Get( getActivity() ).GetSubjects().size(); ++i ) {
                mHandlerThread.Prepare( ChislevSubjectsLaboratory.Get( getActivity() ).GetSubjectItem( i ) );
            }
        }
    }
}
