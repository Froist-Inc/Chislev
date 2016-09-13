package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Josh on 11-Sep-16.
 */

public class ChislevNetworkManager
{
    Context mContext;
    static final String TAG = "ChislevNetworkManager";

    ChislevNetworkManager( Context context )
    {
        mContext = context;
    }

    public byte[] GetData( String address ) throws IOException
    {
        URL url = null;
        HttpsURLConnection urlConnection = null;

        try {
            url = new URL( address );
            urlConnection = ( HttpsURLConnection ) url.openConnection();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputStream in = urlConnection.getInputStream();

            if( urlConnection.getResponseCode() != HttpsURLConnection.HTTP_OK ){
                return null;
            }
            int bytesRead = 0;
            byte [] buffer = new byte[1024];
            while( ( bytesRead = in.read( buffer ) ) > 0 ) {
                byteArrayOutputStream.write( buffer, 0, bytesRead );
            }
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } finally {
            if( urlConnection != null )
            {
                urlConnection.disconnect();
            }
        }
    }
}
