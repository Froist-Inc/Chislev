package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

class ChislevNetworkManager
{
    ChislevNetworkManager( Context context )
    {
    }

    public byte[] GetData( String address ) throws IOException
    {
        URL url;
        HttpsURLConnection urlConnection = null;
        final int fortyFiveSeconds = 45000;

        try {
            url = new URL( address );
            urlConnection = ( HttpsURLConnection ) url.openConnection();
            urlConnection.setConnectTimeout( fortyFiveSeconds );
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
