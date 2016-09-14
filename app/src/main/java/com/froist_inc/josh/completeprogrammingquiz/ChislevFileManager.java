package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

/**
 * Created by Josh on 12-Sep-16.
 */
public class ChislevFileManager
{
    private Context mContext;
    private static final String TAG = "ChislevFileManager";

    ChislevFileManager( Context context )
    {
        mContext = context;
    }

    boolean FileExists( String filename )
    {
        return new File( filename ).exists();
    }

    boolean FileExists( String filename, String parentDirectory, boolean createIfNotExists )
            throws IOException
    {
        File parentDirectoryFile = new File( parentDirectory );
        if( !parentDirectoryFile.exists() ){
            if( createIfNotExists ){
                if( !CreateDirectory( parentDirectory ) ) return false;
            }
        }
        File file = new File( parentDirectoryFile.getCanonicalPath() + "/" + filename );
        return file.exists();
    }

    boolean CreateDirectory( String directoryName )
    {
        File directory = mContext.getDir( directoryName, Context.MODE_PRIVATE );
        return directory != null;
    }

    public String ReadDataFromFile( String filename ) throws IOException
    {
        BufferedReader reader = null;
        try {
            FileInputStream fileInputStream = mContext.openFileInput( filename );
            reader = new BufferedReader( new InputStreamReader( fileInputStream ) );
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;
            while( ( line = reader.readLine() ) != null ){
                stringBuilder.append( line );
            }
            return stringBuilder.toString();
        } finally {
            if( reader != null ) reader.close();
        }
    }

    public void SaveDataToFile( byte [] data, String filename, String parentDirectory ) throws IOException
    {
        Writer writer = null;
        File newFile = null;
        try {
            if( parentDirectory != null ) {
                File parentPath = mContext.getDir( parentDirectory, Context.MODE_PRIVATE );
                newFile = new File( parentPath, filename );
            } else {
                newFile = new File( filename );
            }
            if( !newFile.exists() ){
                newFile.createNewFile();
                Log.d( TAG, "New file created" );
            }
            writer = new FileWriter( newFile );
            Log.d( TAG, "Writing data to file" );
            writer.write( ChislevUtilities.ByteArrayToString( data ), 0, data.length );
            Log.d( TAG, "Data successfully written to file. Balling out..." );
        } finally {
            Log.d( TAG, "Closing file" );
            if( writer != null ) writer.close();
        }
    }
}
