package com.froist_inc.josh.completeprogrammingquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;

public class ChislevScoresDatabaseManager extends ChislevAbstractDatabaseManager
{
    final static String TABLE_NAME = "scores";
    final static String TableCreationQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " +
            ChislevScoresFormat.LEVEL + " text, " + ChislevScoresFormat.TIME_STARTED + " text, " +
            ChislevScoresFormat.TIME_USED + " text, " + ChislevScoresFormat.DAY_QUIZ_TAKEN +
            " text, " + ChislevScoresFormat.TOTAL_SCORE + " INTEGER, "
            + ChislevScoresFormat.QUESTION_ARITY + " INTEGER )";

    ChislevScoresDatabaseManager( Context context, final String dbName )
    {
        super( context, dbName );
    }

    public static class ChislevScoresDatabaseHelper extends ChislevAbstractDatabaseHelper
    {
        ChislevScoresDatabaseHelper(){
            super( TABLE_NAME );
        }

        @Override
        protected ChislevAbstractDatabaseManager GetDataManager( Context context, String path )
        {
            return new ChislevScoresDatabaseManager( context, path );
        }

        @Override
        public Cursor GetCursor( String... dbColumnTitles ) {
            return GetScoresCursor( dbColumnTitles );
        }

        private Cursor GetScoresCursor( String... columnNames )
        {
            StringBuilder queryBuilder = new StringBuilder( "SELECT " );

            final int length = columnNames.length - 1;
            for( int i = 0; i != length; ++i )
            {
                queryBuilder.append( columnNames[i] );
                queryBuilder.append( ", " );
            }

            queryBuilder.append( columnNames[length] );
            queryBuilder.append( " FROM " + TABLE_NAME );

            super.GetWriteableDatabase().rawQuery( TableCreationQuery, null );
            return new ScoresExtractingCursor( super.GetReadableDatabase().rawQuery( queryBuilder.toString(), null ));
        }
    }

    public static class ScoresExtractingCursor extends CursorWrapper
    {
        ScoresExtractingCursor( Cursor cursor )
        {
            super( cursor );
        }

        ChislevScoresFormat GetScore()
        {
            if( isBeforeFirst() || isAfterLast() ) return null;

            final ChislevScoresFormat score = new ChislevScoresFormat();
            score.setQuizLevelTaken( getString( getColumnIndex( ChislevScoresFormat.LEVEL ) ) );
            score.setTotalScores( getInt( getColumnIndex( ChislevScoresFormat.TOTAL_SCORE )) );
            score.setQuestionTotal( getInt( getColumnIndex( ChislevScoresFormat.QUESTION_ARITY ) ) );
            score.setDayQuizTaken( getString( getColumnIndex( ChislevScoresFormat.DAY_QUIZ_TAKEN ) ) );
            score.setTimeStarted( getString( getColumnIndex( ChislevScoresFormat.TIME_STARTED )) );
            score.setTotalTimeUsed( getString( getColumnIndex( ChislevScoresFormat.TIME_USED )) );

            return score;
        }
    }

    public static class ChislevScoresCursorLoader extends ChislevAbstractCursorLoader
    {
        private String mId;

        ChislevScoresCursorLoader( Context context, String[] tableColumns, final String code )
        {
            super( context, tableColumns, code );
            mId = code;
        }

        public String GetId(){ return mId; }

        @Override
        public ChislevAbstractDatabaseHelper GetDatabaseHelper()
        {
            return new ChislevScoresDatabaseHelper();
        }
    }

    synchronized static
    public boolean InsertNewScore( Context context, final ChislevScoresFormat score, final String code )
    {
        ContentValues cv = new ContentValues();
        cv.put( ChislevScoresFormat.LEVEL, score.getQuizLevelTaken() );
        cv.put( ChislevScoresFormat.DAY_QUIZ_TAKEN, score.getDayQuizTaken() );
        cv.put( ChislevScoresFormat.TIME_STARTED, score.getTimeStarted() );
        cv.put( ChislevScoresFormat.TIME_USED, score.getTimeUsed() );
        cv.put( ChislevScoresFormat.TOTAL_SCORE, score.getTotalScores() );
        cv.put( ChislevScoresFormat.QUESTION_ARITY, score.getQuestionTotal() );

        ChislevScoresDatabaseHelper databaseHelper = new ChislevScoresDatabaseHelper();
        boolean success = databaseHelper.OpenDatabase( context, code );
        if( !success ){
            databaseHelper.close();
            return false;
        }

        try {
            databaseHelper.GetWriteableDatabase().execSQL( TableCreationQuery );
            return databaseHelper.GetWriteableDatabase().insert( TABLE_NAME, null, cv ) != -1;
        } finally {
            databaseHelper.close();
        }
    }
}
