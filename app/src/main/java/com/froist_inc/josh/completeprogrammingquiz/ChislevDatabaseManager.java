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
        protected ChislevAbstractDatabaseManager GetDataManager( Context context, String fullPath )
        {
            return new ChislevDatabaseManager( context, fullPath );
        }

        private Cursor GetSolutionCursor( String[] referenceIdList )
        {
            final String query = "SELECT reference_id, option, answer_text, hint, explanation FROM "
                    + super.GetTableName() + " WHERE reference_id IN ( ";
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

            final long referenceId = getLong( getColumnIndex( "reference_id" ) ),
                    correctOption = getLong( getColumnIndex( "option" ) );
            final String correctText = getString( getColumnIndex( "answer_text" ) ),
                    hintText = getString( getColumnIndex( "hint" ) ),
                    explanationText = getString( getColumnIndex( "explanation" ) );
            final ChislevQuestion.ChislevSolutionFormat solution =
                    new ChislevQuestion.ChislevSolutionFormat( referenceId, correctOption, correctText );
            solution.setExplanation( explanationText );
            solution.setHint( hintText );
            return solution;
        }
    }

    public static class ChislevCursorLoader extends ChislevAbstractCursorLoader
    {
        ChislevCursorLoader( Context context, String [] list, final String code, final String checksum )
        {
            super( context, list, code, checksum );
        }

        @Override
        public ChislevAbstractDatabaseHelper GetDatabaseHelper()
        {
            return new ChislevDatabaseHelper();
        }
    }
}
