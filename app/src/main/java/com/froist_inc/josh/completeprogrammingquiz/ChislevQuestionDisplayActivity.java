package com.froist_inc.josh.completeprogrammingquiz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class ChislevQuestionDisplayActivity extends ChislevGenericActivity
{
    private static final String TAG = "QuestionDisplayActivity";

    private static ArrayList<ChislevQuestion> questionList;
    private static ArrayList<ChislevQuestion.ChislevSolutionFormat> solutionList;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        Toolbar toolbar = ( Toolbar ) findViewById( R.id.question_toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeButtonEnabled( true );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate( R.menu.question_page_menu, menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch ( item.getItemId() ){
            case R.id.menu_question_page_forfeit:
                Log.d( TAG, "Forfeiting" );
                return true;
            case android.R.id.home:
                if( NavUtils.getParentActivityName( this ) != null ){
                    NavUtils.navigateUpFromSameTask( this );
                }
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }

    @Override
    public Fragment GetFragment()
    {
        return new ChislevQuestionDisplayFragment();
    }

    @Override
    public int GetLayoutID() {
        return R.layout.question_page_activity;
    }

    @Override
    public int GetContainerID() {
        return R.id.subject_choose_layoutContainer;
    }

    public static ArrayList<ChislevQuestion.ChislevSolutionFormat> GetSolutionList()
    {
        return solutionList;
    }
    public static ArrayList<ChislevQuestion> GetQuestionList()
    {
        return questionList;
    }

    public static void SetQuestionList( ArrayList<ChislevQuestion> chislevQuestions )
    {
        questionList = chislevQuestions;
    }

    public static void SetAnswerList( ArrayList<ChislevQuestion.ChislevSolutionFormat> chislevSolutionFormats )
    {
        solutionList = chislevSolutionFormats;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
