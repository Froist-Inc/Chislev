package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

@SuppressWarnings("ALL")
class ChislevFileManager
{
    private final Context mContext;
    private static final String TAG = "ChislevFileManager";

    ChislevFileManager( Context context )
    {
        mContext = context;
    }

    boolean FileExists( String filename )
    {
        return new File( mContext.getFilesDir(), filename ).exists();
    }

    boolean FileExists( String filename, String parentDirectory )
            throws IOException
    {
        File parentDirectoryFile = new File( mContext.getFilesDir(), parentDirectory );
        File file = new File( parentDirectoryFile.getCanonicalPath(), filename );
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
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream( filename );
            reader = new BufferedReader( new InputStreamReader( fileInputStream ) );
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while( ( line = reader.readLine() ) != null ){
                stringBuilder.append( line );
            }
            return stringBuilder.toString();
        } finally {
            if( reader != null ) reader.close();
            if( fileInputStream != null ) fileInputStream.close();
        }
    }

    public void SaveDataToFile( byte [] data, String filename, String parentDirectory ) throws IOException
    {
        FileOutputStream stream = null;
        File newFile;
        try {
            File filesDir = mContext.getFilesDir();
            if( parentDirectory != null ) {
                File parentPath = new File( filesDir, parentDirectory );
                if( !parentPath.exists() ){
                    if( !parentPath.mkdirs() ){
                        throw new IOException( "Unable to create parent directory." );
                    }
                }
                newFile = new File( parentPath, filename );
            } else {
                newFile = new File( filesDir, filename );
            }
            if( !newFile.exists() ){
                if( !newFile.createNewFile() ){
                    throw new IOException( "Unable to create new file." );
                }
            }
            stream = new FileOutputStream( newFile );
            stream.write( data );
        } finally {
            if( stream != null ) stream.close();
        }
    }
}
