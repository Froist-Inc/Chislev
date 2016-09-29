package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.AsyncTaskLoader;

import java.io.File;
import java.io.IOException;

class ChislevDatabaseManager extends SQLiteOpenHelper
{
    private SQLiteDatabase mDatabase;

    private ChislevDatabaseManager(Context context, String dbName ){
        super( context, dbName, null, 1 );
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
    }

    @Override
    public void onCreate( SQLiteDatabase db ) {

    }

    private boolean OpenDatabase( final String fullPath ) {
        try {
            mDatabase = SQLiteDatabase.openDatabase( fullPath, null, SQLiteDatabase.OPEN_READWRITE );
        } catch ( SQLiteException exception ){
            return false;
        }
        boolean mDatabaseIsNull = mDatabase == null;
        return !mDatabaseIsNull;
    }

    @Override
    public synchronized void close() {
        if( mDatabase != null ){
            mDatabase.close();
        }
        super.close();
    }

    public static class ChislevDatabaseHelper
    {
        private SQLiteDatabase mDatabase;
        private ChislevDatabaseManager mDatabaseManager;
        public static final String DB_NAME = ChislevSubjectInformation.SOLUTION_FILENAME;
        public static final String TABLE_NAME = "answers";

        ChislevDatabaseHelper(){

        }

        private boolean OpenDatabase( Context context, final String path )
        {
            File parentPath = new File( context.getFilesDir(), path );
            String fullPath;
            try {
                File temp = new File( parentPath, DB_NAME );
                fullPath = temp.getCanonicalPath();
            } catch ( IOException exception ) {
                return false;
            }

            mDatabaseManager = new ChislevDatabaseManager( context, fullPath );
            boolean success = mDatabaseManager.OpenDatabase( fullPath );
            if( !success ) return false;

            try {
                mDatabaseManager.close();
                mDatabase = mDatabaseManager.getReadableDatabase();
                return true;
            } catch ( SQLiteException exception ){
                return false;
            }
        }

        public Cursor GetSolutionCursor( String [] referenceIdList )
        {
            final String query = "SELECT reference_id, option, answer_text FROM " + TABLE_NAME + " WHERE reference_id IN "
                    + "( ";
            StringBuilder sqlInExpression = new StringBuilder( query );
            final int listSize = referenceIdList.length - 1;
            for( int i = 0; i != listSize; ++i ) {
                sqlInExpression.append( referenceIdList[i] );
                sqlInExpression.append( ", " );
            }
            sqlInExpression.append( referenceIdList[listSize] );
            sqlInExpression.append( " )" );

            return new ChislevSolutionsCursor( mDatabase.rawQuery( sqlInExpression.toString(), null ) );
        }

        void close()
        {
            if( mDatabase != null && mDatabase.isOpen() ){
                mDatabase.close();
                mDatabase = null;
            }
            mDatabaseManager.close();
        }
    }

    public static class ChislevSolutionsCursor extends CursorWrapper
    {
        ChislevSolutionsCursor( Cursor cursor )
        {
            super( cursor );
        }

        ChislevQuestion.ChislevSolutionFormat GetSolution()
        {
            if( isBeforeFirst() || isAfterLast() ) return null;

            final long referenceId = getLong( getColumnIndex( "reference_id" ) ),
                    correctOption = getLong( getColumnIndex( "option" ) );
            final String correctText = getString( getColumnIndex( "answer_text" ) );
            return new ChislevQuestion.ChislevSolutionFormat( referenceId, correctOption, correctText );
        }
    }

    public static class ChislevCursorLoader extends AsyncTaskLoader<Cursor>
    {
        private final Context mContext;
        private final String[] mList;
        private final String mCode;

        private Cursor mCursor;

        public ChislevCursorLoader( Context context, String[] list, final String code )
        {
            super( context );
            mContext = context;
            mList = list;
            mCode = code;
        }

        @Override
        public Cursor loadInBackground() {
            ChislevDatabaseHelper dbHelper = new ChislevDatabaseHelper();
            boolean success = dbHelper.OpenDatabase( mContext, mCode );
            if( !success ){
                dbHelper.close();
                return null;
            }
            mCursor = dbHelper.GetSolutionCursor( mList );
            if( mCursor != null ){
                mCursor.getCount();
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
        protected void onStopLoading() {
            cancelLoad();
        }

        @Override
        protected void onReset() {
            super.onReset();
            onStopLoading();
            if( mCursor != null && !mCursor.isClosed() ) mCursor.close();
            mCursor = null;
        }

        @Override
        public void deliverResult( Cursor data ){
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
        public void onCanceled( Cursor data ){
            if( mCursor != null && !mCursor.isClosed() ){
                mCursor.close();
            }
            super.onCanceled( data );
        }
    }
}
