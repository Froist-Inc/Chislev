package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Context;
import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;
import net.sqlcipher.database.SQLiteOpenHelper;

import android.support.v4.content.AsyncTaskLoader;

import java.io.File;
import java.io.IOException;

public class ChislevAbstractDatabaseManager extends SQLiteOpenHelper
{
    protected SQLiteDatabase mDatabase;

    ChislevAbstractDatabaseManager( Context context, final String dbName )
    {
        super( context, dbName, null, 1 );
        SQLiteDatabase.loadLibs( context );
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
    {
    }

    @Override
    public void onCreate( SQLiteDatabase db )
    {
    }

    protected boolean OpenDatabase( final String fullPath, final String password )
    {
        try {
            mDatabase = SQLiteDatabase.openDatabase( fullPath, password, null, SQLiteDatabase.OPEN_READWRITE );
        } catch ( SQLiteException exception ){
            return false;
        }
        return true;
    }

    @Override
    public synchronized void close()
    {
        if( mDatabase != null ){
            mDatabase.close();
        }
        super.close();
    }

    public static abstract class ChislevAbstractDatabaseHelper
    {
        private final String DB_NAME = ChislevSubjectInformation.SOLUTION_FILENAME;
        private SQLiteDatabase mDatabase;
        private final String mTableName;
        private ChislevAbstractDatabaseManager mDatabaseManager;

        ChislevAbstractDatabaseHelper( String tableName )
        {
            mTableName = tableName;
        }

        String GetTableName(){ return mTableName; }
        public String GetFullDbPath( Context context, final String path )
        {
            File parentPath = new File( context.getFilesDir(), path );
            try {
                File temp = new File( parentPath, DB_NAME );
                return temp.getCanonicalPath();
            } catch ( IOException exception ) {
                return null;
            }
        }

        protected boolean OpenDatabase( Context context, final String path, final String password )
        {
            String fullPath = GetFullDbPath( context, path );
            if( fullPath == null ){
                return false;
            }

            mDatabaseManager = GetDataManager( context, fullPath );
            boolean success = mDatabaseManager.OpenDatabase( fullPath, password );
            if( !success ) return false;

            try {
                mDatabaseManager.close();
                mDatabase = mDatabaseManager.getReadableDatabase( password );
                return true;
            } catch ( SQLiteException exception ){
                return false;
            }
        }

        protected SQLiteDatabase GetReadableDatabase(){ return mDatabase; }
        protected SQLiteDatabase GetWriteableDatabase(){ return mDatabase; }
        protected abstract ChislevAbstractDatabaseManager GetDataManager( Context context, String path );
        public abstract Cursor GetCursor( String... dbColumnTitles );

        public void close()
        {
            if( mDatabase != null && mDatabase.isOpen() ){
                mDatabase.close();
                mDatabase = null;
            }
        }
    }

    public static abstract class ChislevAbstractCursorLoader extends AsyncTaskLoader<Cursor>
    {
        private final Context mContext;
        private final String[] mList;
        private final String mCode;
        private final String mChecksum;

        private Cursor mCursor;

        public ChislevAbstractCursorLoader( Context context, String[] list, final String code, final String checksum )
        {
            super( context );
            mContext = context;
            mList = list;
            mCode = code;
            mChecksum = checksum;
        }

        public abstract ChislevAbstractDatabaseHelper GetDatabaseHelper();

        @Override
        public Cursor loadInBackground()
        {
            ChislevAbstractDatabaseHelper dbHelper = GetDatabaseHelper();
            boolean success = dbHelper.OpenDatabase( mContext, mCode, mChecksum );
            if( !success ){
                dbHelper.close();
                return null;
            }
            try {
                mCursor = dbHelper.GetCursor( mList );
                if ( mCursor != null ) {
                    mCursor.getCount();
                }
            } catch ( Exception except ){
                return null;
            }
            return mCursor;
        }

        @Override
        protected void onStartLoading()
        {
            if( mCursor != null ){
                deliverResult( mCursor );
            }
            if( takeContentChanged() || mCursor == null ){
                forceLoad();
            }
        }

        @Override
        protected void onStopLoading()
        {
            cancelLoad();
        }

        @Override
        protected void onReset()
        {
            super.onReset();
            onStopLoading();
            if( mCursor != null && !mCursor.isClosed() ) mCursor.close();
            mCursor = null;
        }

        @Override
        public void deliverResult( Cursor data )
        {
            Cursor oldCursor = mCursor;
            mCursor = data;
            if( isStarted() ){
                super.deliverResult( data );
            }
            if( oldCursor != null && oldCursor != mCursor && !oldCursor.isClosed() ){
                oldCursor.close();
            }
        }

        @Override
        public void onCanceled( Cursor data )
        {
            if( mCursor != null && !mCursor.isClosed() ){
                mCursor.close();
            }
            super.onCanceled( data );
        }
    }
}
