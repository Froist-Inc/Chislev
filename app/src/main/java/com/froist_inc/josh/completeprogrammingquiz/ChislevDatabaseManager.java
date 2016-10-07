package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;

class ChislevDatabaseManager extends ChislevAbstractDatabaseManager
{
    private ChislevDatabaseManager( Context context, String dbName ){
        super( context, dbName );
    }

    public static class ChislevDatabaseHelper extends ChislevAbstractDatabaseHelper
    {
        ChislevDatabaseHelper()
        {
            super( "answers" ); // super takes a table name
        }

        @Override
        public Cursor GetCursor( String... dbColumnTitles ) {
            return GetSolutionCursor( dbColumnTitles );
        }

        @Override
        protected ChislevAbstractDatabaseManager GetDataManager( Context context, String fullPath ) {
            return new ChislevDatabaseManager( context, fullPath );
        }

        private Cursor GetSolutionCursor( String[] referenceIdList )
        {
            final String query = "SELECT option, answer_text FROM " + super.GetTableName() + " WHERE reference_id IN ( ";
            StringBuilder sqlInExpression = new StringBuilder( query );
            final int listSize = referenceIdList.length - 1;
            for( int i = 0; i != listSize; ++i ) {
                sqlInExpression.append( referenceIdList[i] );
                sqlInExpression.append( ", " );
            }
            sqlInExpression.append( referenceIdList[listSize] );
            sqlInExpression.append( " )" );

            return new ChislevSolutionsCursor( super.GetReadableDatabase().rawQuery( sqlInExpression.toString(), null ) );
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

            final long correctOption = getLong( getColumnIndex( "option" ) );
            final String correctText = getString( getColumnIndex( "answer_text" ) );
            return new ChislevQuestion.ChislevSolutionFormat( correctOption, correctText );
        }
    }

    public static class ChislevCursorLoader extends ChislevAbstractCursorLoader
    {
        ChislevCursorLoader( Context context, String [] list, final String code )
        {
            super( context, list, code );
        }

        @Override
        public ChislevAbstractDatabaseHelper GetDatabaseHelper()
        {
            return new ChislevDatabaseHelper();
        }
    }
}
