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
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ChislevQuestionDisplayFragment extends Fragment
{
    private static final String EXTRA_INDEX = "EXTRA_INDEX", EXTRA_LEVEL = "EXTRA_LEVEL", EXTRA = "EXTRA_DATA";
    private CountDownTimer mCountDownTimer;

    private final int QUESTION_SIZE = 5;

    private ArrayList<ChislevQuestion> mQuestionList;
    int mCurrentQuestionIndex = 0;
    int mSubjectIndex, mLevel;

    WebView mCodeView;
    ImageButton mNextQuestionButton, mPrevQuestionButton;
    TextView mContributorTextView, mTimerTextView, mQuestionTextView;
    RadioButton mFirstOptionRadio, mSecondOptionRadio, mThirdOptionRadio, mFourthOptionRadio;
    View /* coverView = null, */ view = null;

    private void UpdateQuestionView( int index )
    {
        if( view == null || mQuestionList == null ) return;
        ChislevQuestion currentQuestion = mQuestionList.get( index );

        mCodeView.loadData( currentQuestion.getCode(), "text/html", "UTF-8" );
        if( index == 0 ){
            mPrevQuestionButton.setEnabled( false );
        } else if( index == ( mQuestionList.size() - 1 ) ){
            mNextQuestionButton.setEnabled( false );
        }

        if( currentQuestion.getOwner() == null ){
            mContributorTextView.setText( getString( R.string.question_contributor, "The C++ Community" ) );
        } else {
            mContributorTextView.setText( getString( R.string.question_contributor, currentQuestion.getOwner() ));
        }
        mQuestionTextView.setText( currentQuestion.getQuestion().trim() );
        mFirstOptionRadio.setText( currentQuestion.getAvailableOptions().get( 0 ));
        mSecondOptionRadio.setText( currentQuestion.getAvailableOptions().get( 1 ));
        mThirdOptionRadio.setText( currentQuestion.getAvailableOptions().get( 2 ));
        mFourthOptionRadio.setText( currentQuestion.getAvailableOptions().get( 3 ));
    }

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState )
    {
        view = inflater.inflate( R.layout.question_page_fragment, container, false );
//        coverView = view.findViewById( R.id.question_page_coverLayout );
//        coverView.setVisibility( View.VISIBLE );

        mPrevQuestionButton = ( ImageButton ) view.findViewById( R.id.prevButton );
        mNextQuestionButton = ( ImageButton ) view.findViewById( R.id.nextButton );

        mPrevQuestionButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                if( mCurrentQuestionIndex == 0 ) return;
                mCurrentQuestionIndex = ( mCurrentQuestionIndex - 1 ) % mQuestionList.size();
                UpdateQuestionView( mCurrentQuestionIndex );
            }
        });
        mNextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentQuestionIndex = ( mCurrentQuestionIndex + 1 ) % mQuestionList.size();
                UpdateQuestionView( mCurrentQuestionIndex );
            }
        });
        mCodeView = ( WebView ) view.findViewById( R.id.question_codeWebView );
        mCodeView.setClickable( true );
        mTimerTextView = ( TextView ) view.findViewById( R.id.timer_textView );
        mContributorTextView = ( TextView ) view.findViewById( R.id.contributor_textView );
        mQuestionTextView = ( TextView ) view.findViewById( R.id.question_textView );
        mFirstOptionRadio = ( RadioButton ) view.findViewById( R.id.question_option_oneRadioButton );
        mSecondOptionRadio = ( RadioButton ) view.findViewById( R.id.question_option_twoRadioButton );
        mThirdOptionRadio = ( RadioButton ) view.findViewById( R.id.question_option_threeRadioButton );
        mFourthOptionRadio = ( RadioButton ) view.findViewById( R.id.question_option_fourRadioButton );
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
                long x = millisUntilFinished / 1000;
                long seconds = x % 60;
                x /= 60;
                int minutes = ( int ) x % 60;
                x /= 60;
                int hours = ( int ) x % 24;
                x /= 24;
                int days = ( int ) x;
                String text = "" + ( days > 0 ? ( days + " day(s), " ) : "" ) +
                        ( hours > 0 ? ( hours + "h" ) : "" ) +
                        ( minutes > 0 ? ( minutes + "m" ) : "" ) +
                        seconds + "s";
                mTimerTextView.setText( text );
            }

            @Override
            public void onFinish()
            {

            }
        }.start();
//        coverView.setVisibility( View.INVISIBLE );
        UpdateQuestionView( mCurrentQuestionIndex );
    }

    // Todo -> save array index and some other housekeeping information
    @Override
    public void onSaveInstanceState( Bundle outState )
    {
        super.onSaveInstanceState( outState );
    }

    private class ChislevSubjectProcessingTask extends AsyncTask<Void, Void, ArrayList<ChislevQuestion> >
    {
        private ArrayList<ChislevQuestion> FilterQuestions( final Map<String, ArrayList<ChislevQuestion> > questionMap,
                                                            final String filterCriteria )
        {
            boolean notRandom = filterCriteria.compareToIgnoreCase( "Beginner" ) == 0 ||
                    filterCriteria.compareToIgnoreCase( "Intermediate" ) == 0 ||
                    filterCriteria.compareToIgnoreCase( "Advanced" ) == 0;
            ArrayList<ChislevQuestion> questions = null;
            if( !notRandom ){
                questions = questionMap.get( filterCriteria );
            } else {
                // TODO: 24-Sep-16
                questions = questionMap.get( "Intermediate" );
            }
            if ( questions != null ){
                Collections.shuffle( questions );
            }
            return questions;
        }

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
                return FilterQuestions( questionMap, filteringLevel );
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
