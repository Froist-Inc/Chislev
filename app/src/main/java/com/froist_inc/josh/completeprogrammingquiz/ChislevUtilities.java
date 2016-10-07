package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ChislevUtilities
{
    private static final String CONFIG_FILENAME = "config.xml";
    private static final String CONFIG_URL = "https://raw.githubusercontent.com/Froist/Chislev/master/Data/config.xml";

    public static String ByteArrayToString( byte [] data ) {
        assert data != null;
        return new String(data);
    }
    public static class ChislevLoadConfigFileTask extends AsyncTask<Void, String, ArrayList<ChislevSubjectInformation>>
    {
        private Context mContext = null;
        private MainUiThreadListener mUiListener = null;

        public interface MainUiThreadListener {
            void UiThreadOnPostExecute( ArrayList<ChislevSubjectInformation> informationArrayList );
        }

        ChislevLoadConfigFileTask( Context context ){
            mContext = context;
        }

        public void SetUiThreadOnPostExecuteListener( MainUiThreadListener listener ){
            mUiListener = listener;
        }

        static final String TAG = "LoadConfigFileTask";
        @Override
        protected ArrayList<ChislevSubjectInformation> doInBackground( Void... params )
        {
            File file = new File( mContext.getFilesDir(), CONFIG_FILENAME );
            String data;
            ChislevFileManager fileManager = new ChislevFileManager( mContext );
            ArrayList<ChislevSubjectInformation> informationList = new ArrayList<>();

            try {
                if( !file.exists() ){
                    byte[] result = new ChislevNetworkManager( mContext ).GetData( CONFIG_URL );
                    if( result == null || result.length == 0 ) return null;

                    fileManager.SaveDataToFile( result, CONFIG_FILENAME, null );
                    data = ChislevUtilities.ByteArrayToString( result );
                } else {
                    data = fileManager.ReadDataFromFile( file.getCanonicalPath() );
                }
            } catch ( IOException exception ) {
                return informationList;
            }
            ChislevXMLSerializer xmlSerializer = new ChislevXMLSerializer( mContext );
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
            if( mUiListener != null ){
                mUiListener.UiThreadOnPostExecute( informationList );
            }
        }
    }
}
