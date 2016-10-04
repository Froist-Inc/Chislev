package com.froist_inc.josh.completeprogrammingquiz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class ChislevQuestionDisplayActivity extends AppCompatActivity
{
    private static final String TAG = "QuestionDisplayActivity";

    private static ArrayList<ChislevQuestion> questionList;
    private static ArrayList<ChislevQuestion.ChislevSolutionFormat> solutionList;
    private static final String FRAG = "CurrentFragment";
    private int currentFragment = 0;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( GetLayoutID() );

        final int containerID = GetContainerID();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById( containerID );

        if( fragment == null ){
            if( savedInstanceState != null ){
                currentFragment = savedInstanceState.getInt( FRAG );
            }

            fragment = GetFragment();
            fragmentManager.beginTransaction().add( containerID, fragment ).commit();
        }

        Toolbar toolbar = ( Toolbar ) findViewById( R.id.question_toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeButtonEnabled( true );
    }

    @Override
    protected void onSaveInstanceState( Bundle outState ) {
        outState.putInt( FRAG, currentFragment );
        super.onSaveInstanceState( outState );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
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

    public Fragment GetFragment()
    {
        if( currentFragment == 0 ) {
            return new ChislevQuestionDisplayFragment();
        } else {
            return new ChislevSolutionsFragment();
        }
    }

    public int GetLayoutID() {
        return R.layout.question_page_activity;
    }

    public int GetContainerID() {
        return R.id.subject_choose_layoutContainer;
    }

    public void FragmentWorkCompleted()
    {
        currentFragment = 1;
        FragmentManager fragmentManager = getSupportFragmentManager();
        if( fragmentManager.findFragmentById( GetContainerID() ) == null ){
            Toast.makeText( ChislevQuestionDisplayActivity.this, "A very critical error occurred",
                    Toast.LENGTH_SHORT ).show();
            this.finish();
        }
        ChislevSolutionsFragment solutionsFragment = new ChislevSolutionsFragment();
        fragmentManager.beginTransaction().replace( GetContainerID(), solutionsFragment ).commit();
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
}
