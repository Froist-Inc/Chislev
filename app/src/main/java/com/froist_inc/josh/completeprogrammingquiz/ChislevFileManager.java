package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by Josh on 12-Sep-16.
 */
public class ChislevFileManager
{
    private Context mContext;

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

    public String ReadDataFromFile( String filename )
            throws IOException
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

    public void SaveDataToFile( byte [] data, String filename, String parentDirectory )
            throws IOException
    {
        Writer writer = null;
        try {
            final String pathToFile = ( parentDirectory == null ? filename : parentDirectory + "/" + filename );
            FileOutputStream outputStream = mContext.openFileOutput( pathToFile, Context.MODE_PRIVATE );
            writer = new OutputStreamWriter( outputStream );
            writer.write( new String( data ), 0, data.length );
        } finally {
            if( writer != null ) writer.close();
        }
    }
}
