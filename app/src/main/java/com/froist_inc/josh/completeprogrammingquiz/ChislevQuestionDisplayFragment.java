package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class ChislevQuestionDisplayFragment extends Fragment
{
    private static final String EXTRA_INDEX = "EXTRA_INDEX", EXTRA_LEVEL = "EXTRA_LEVEL", EXTRA = "EXTRA_DATA";
    private CountDownTimer mCountDownTimer;

    private ArrayList<ChislevQuestion> mQuestionList;
    int mSubjectIndex, mLevel;

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.question_page_fragment, container, false );
        WebView webView = ( WebView ) view.findViewById( R.id.question_codeWebView );
        webView.setClickable( true );
        webView.loadData( getString( R.string.index_activity_new_quiz ), "text/html", "UTF-8" );
        return view;
    }

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setRetainInstance( true );
        if( savedInstanceState != null ){
            mSubjectIndex = savedInstanceState.getInt( EXTRA_INDEX );
            mLevel = savedInstanceState.getInt( EXTRA_LEVEL );
        }
        new ChislevSubjectProcessingTask().execute();
    }

    private void UpdateMainUIView()
    {
        if( mQuestionList == null || mQuestionList.size() < 1 ){
            Toast.makeText( getActivity(), "Could not process questions, please update app.", Toast.LENGTH_LONG ).show();
            getActivity().finish();
            return;
        }
        final int anHour = 3600000, oneSecond = 1000;
        mCountDownTimer = new CountDownTimer( anHour, oneSecond ) {
            @Override
            public void onTick( long millisUntilFinished )
            {

            }

            @Override
            public void onFinish()
            {

            }
        }.start();
    }

    // Todo -> save array index and some other housekeeping information
    @Override
    public void onSaveInstanceState( Bundle outState )
    {
        super.onSaveInstanceState( outState );
    }

    private class ChislevSubjectProcessingTask extends AsyncTask<Void, Void, ArrayList<ChislevQuestion> >
    {
        private String LevelToString( int level )
        {
            switch( level ){
                case 0:
                    return "Beginner";
                case 1:
                    return "Intermediate";
                case 2:
                    return "Advanced";
                default:
                    return "Random";
            }
        }

        @Override
        protected ArrayList<ChislevQuestion> doInBackground( Void... params )
        {
            if( mSubjectIndex == -1 || mLevel == -1 ) return null;
            ChislevXMLSerializer xmlSerializer = new ChislevXMLSerializer( getActivity() );
            ChislevSubjectInformation subjectInformation = ChislevSubjectsLaboratory.Get( getActivity() )
                    .GetSubjectItem( mSubjectIndex );
            try {
                Map<String, ArrayList<ChislevQuestion> > questionMap =
                        xmlSerializer.ParseQuestions( subjectInformation.getSubjectCode(), subjectInformation.getSubjectFilename() );
                final String filteringLevel = LevelToString( mLevel );
                ArrayList<ChislevQuestion> questions = questionMap.get( filteringLevel );
                if( questions != null ) {
                    Collections.shuffle(questions);
                }
                return questions;
            } catch( IOException except ) {
//                Todo
//                Log.d();
            } catch ( XmlPullParserException exception ){
//                Todo
            }
            return null;
        }

        @Override
        protected void onPostExecute( ArrayList<ChislevQuestion> questionArrayList )
        {
            mQuestionList = questionArrayList;
            UpdateMainUIView();
        }
    }

    public static Intent GetQuestionDisplayIntent( Context context, int subjectIndex, int difficultyLevel )
    {
        Bundle extraData = new Bundle();
        extraData.putInt( EXTRA_INDEX, subjectIndex );
        extraData.putInt( EXTRA_LEVEL, difficultyLevel );

        Intent questionIntent = new Intent( context, ChislevQuestionDisplayActivity.class );
        questionIntent.putExtra( EXTRA, extraData );
        return questionIntent;
    }
}
