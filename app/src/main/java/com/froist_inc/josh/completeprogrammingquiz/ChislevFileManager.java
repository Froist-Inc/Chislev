package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

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

            String line;
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
        File newFile;
        try {
            if( parentDirectory != null ) {
                File parentPath = mContext.getDir( parentDirectory, Context.MODE_PRIVATE );
                if( !parentPath.exists() ){
                    if( !parentPath.mkdirs() ){
                        throw new IOException( "Unable to create parent directory." );
                    }
                }
                newFile = new File( parentPath, filename );
            } else {
                newFile = new File( mContext.getFilesDir(), filename );
            }
            if( !newFile.exists() ){
                if( !newFile.createNewFile() ){
                    throw new IOException( "Unable to create new file." );
                }
            }
            writer = new FileWriter( newFile );
            writer.write( ChislevUtilities.ByteArrayToString( data ), 0, data.length );
        } finally {
            if( writer != null ) writer.close();
        }
    }
}
