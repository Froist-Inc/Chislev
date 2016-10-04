package com.froist_inc.josh.completeprogrammingquiz;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class ChislevQuestionDisplayFragment extends Fragment
{
    private static final String TAG = "QuestionDisplayFragment";
    public static final int HINT_CHECK = 1;
    private static final String EXTRA_INDEX = "EXTRA_INDEX", EXTRA_LEVEL = "EXTRA_LEVEL", EXTRA = "EXTRA_DATA";

    private int mCurrentQuestionIndex = 0;
    private int mSubjectIndex;
    private int mLevel;
    private Thread solutionsLoaderThread;

    private WebView mCodeView;
    private TextView mContributorTextView;
    private TextView mTimerTextView;
    private TextView mQuestionTextView;
    private TextView mQuestionIndexTextView;

    private RadioButton mFirstOptionRadio;
    private RadioButton mSecondOptionRadio;
    private RadioButton mThirdOptionRadio;
    private RadioButton mFourthOptionRadio;
    private EditText mAnswerText;
    private CheckBox mUsingStringCheckbox;
    private CountDownTimer mCountDownTimer;

    private View view = null;

    private void UpdateQuestionView( int index )
    {
        if( view == null || ChislevQuestionDisplayActivity.GetQuestionList() == null ) return;
        ChislevQuestion currentQuestion = ChislevQuestionDisplayActivity.GetQuestionList().get( index );

        mCodeView.loadData( currentQuestion.getCode(), "text/html", "UTF-8" );
        mCodeView.reload();
        if( currentQuestion.getOwner() == null ){
            mContributorTextView.setText( getString( R.string.question_contributor, "The " +
                    ChislevSubjectsLaboratory.Get( getActivity() ).GetSubjectItem( mSubjectIndex ).getSubjectName() +
            " Community" ) );
        } else {
            mContributorTextView.setText( getString( R.string.question_contributor, currentQuestion.getOwner() ));
        }
        mQuestionIndexTextView.setText( getString( R.string.question_index, ( index + 1 ),
                ChislevQuestionDisplayActivity.GetQuestionList().size() ) );
        mQuestionTextView.setText( currentQuestion.getQuestion().trim() );
        mFirstOptionRadio.setText( currentQuestion.getAvailableOptions().get( 0 ));
        mSecondOptionRadio.setText( currentQuestion.getAvailableOptions().get( 1 ));
        mThirdOptionRadio.setText( currentQuestion.getAvailableOptions().get( 2 ));
        mFourthOptionRadio.setText( currentQuestion.getAvailableOptions().get( 3 ));
        mAnswerText.setText( currentQuestion.getAnswer() != null ? currentQuestion.getAnswer() : "" );
        mUsingStringCheckbox.setChecked( false );
        mAnswerText.setVisibility( View.INVISIBLE );
    }

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState )
    {
        view = inflater.inflate( R.layout.question_page_fragment, container, false );
        new ChislevSubjectProcessingTask().execute();
        return view;
    }

    private void InitializeView()
    {
        Button hintButton = ( Button ) view.findViewById( R.id.hintButton );
        hintButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                ChislevHintDisplayDialog hintDialog = new ChislevHintDisplayDialog();
                hintDialog.setTargetFragment( ChislevQuestionDisplayFragment.this, HINT_CHECK );
                hintDialog.show( getActivity().getSupportFragmentManager(), ChislevHintDisplayDialog.TAG );
            }
        });
        mAnswerText = ( EditText ) view.findViewById( R.id.chosen_option_editText );

        mUsingStringCheckbox = ( CheckBox ) view.findViewById( R.id.use_input_checkBox );
        mUsingStringCheckbox.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
                if( isChecked ){
                    mAnswerText.setVisibility( View.VISIBLE );
                } else {
                    mAnswerText.setVisibility( View.INVISIBLE );
                    ChislevQuestionDisplayActivity.GetQuestionList().get( mCurrentQuestionIndex ).setAnswer( "" );
                }

                mAnswerText.setText( "" );
                mUsingStringCheckbox.setChecked( isChecked );
                ChislevQuestionDisplayActivity.GetQuestionList().get( mCurrentQuestionIndex ).setUsingInputbox( isChecked );
            }
        });
        mAnswerText.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence s, int start, int count, int after ) {}

            @Override
            public void onTextChanged( CharSequence s, int start, int before, int count ) {
                if ( mUsingStringCheckbox.isChecked() ) {
                    ChislevQuestionDisplayActivity.GetQuestionList().get( mCurrentQuestionIndex ).setAnswer( s.toString() );
                }
            }

            @Override
            public void afterTextChanged( Editable s ) {}
        });

        mQuestionIndexTextView = ( TextView ) view.findViewById( R.id.questionIndex );

        final Button nextQuestionButton = ( Button ) view.findViewById( R.id.nextButton );
        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                if( mCurrentQuestionIndex == ChislevQuestionDisplayActivity.GetQuestionList().size() - 1 ){
                    DoFinalSubmission();
                } else {
                    ++mCurrentQuestionIndex;
                    if ( mCurrentQuestionIndex == ChislevQuestionDisplayActivity.GetQuestionList().size() - 1) {
                        nextQuestionButton.setText(getString(R.string.submit_text));
                    }
                    UpdateQuestionView(mCurrentQuestionIndex);
                }
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

        RadioGroup collectiveOptions = ( RadioGroup ) view.findViewById( R.id.radioGroup );
        collectiveOptions.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged( RadioGroup group, int checkedId ) {
                switch( checkedId ){
                    case R.id.question_option_oneRadioButton:
                        ChislevQuestionDisplayActivity.GetQuestionList().get( mCurrentQuestionIndex ).setChosenOption( 1 );
                        break;
                    case R.id.question_option_twoRadioButton:
                        ChislevQuestionDisplayActivity.GetQuestionList().get( mCurrentQuestionIndex ).setChosenOption( 2 );
                        break;
                    case R.id.question_option_threeRadioButton:
                        ChislevQuestionDisplayActivity.GetQuestionList().get( mCurrentQuestionIndex ).setChosenOption( 3 );
                        break;
                    case R.id.question_option_fourRadioButton:
                        ChislevQuestionDisplayActivity.GetQuestionList().get( mCurrentQuestionIndex ).setChosenOption( 4 );
                        break;
                    default:
                        ChislevQuestionDisplayActivity.GetQuestionList().get( mCurrentQuestionIndex ).setChosenOption( -1 );
                        break;
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater ) {
        inflater.inflate( R.menu.menu_index, menu );
    }

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setHasOptionsMenu( true );
        setRetainInstance( true );
        mSubjectIndex = getArguments().getInt( EXTRA_INDEX );
        mLevel = getArguments().getInt( EXTRA_LEVEL );
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        super.onActivityResult( requestCode, resultCode, data );
        if( requestCode == ChislevQuestionDisplayFragment.HINT_CHECK ) {
            if ( resultCode == Activity.RESULT_OK ) {
                ChislevQuestionDisplayActivity.GetQuestionList().get( mCurrentQuestionIndex ).setHintUsed();
                new AlertDialog.Builder( getActivity() ).setTitle( R.string.hint )
                        .setMessage( ChislevQuestionDisplayActivity.GetQuestionList().get( mCurrentQuestionIndex ).getHint() )
                        .setPositiveButton( android.R.string.ok, null ).show();
            }
            Fragment hintDialogFragment = getActivity().getSupportFragmentManager()
                    .findFragmentByTag( ChislevHintDisplayDialog.TAG );
            if( hintDialogFragment != null ){
                (( ChislevHintDisplayDialog ) hintDialogFragment ).dismiss();
            }
        }
    }

    private void UpdateMainUIView()
    {
        final ArrayList<ChislevQuestion> questionList = ChislevQuestionDisplayActivity.GetQuestionList();
        if( questionList == null || questionList.size() < 1 ){
            Toast.makeText( getActivity(), "Could not process questions, please update app.", Toast.LENGTH_LONG ).show();
            getActivity().finish();
            return;
        }

        /* at least we have one or more questions presented to the user, now let's secretly get their solutions */
        getLoaderManager().initLoader( 0, null, new LoaderCallbacks() );
        InitializeView();

        final int thirtySeconds = 30000, timeElapsed = thirtySeconds * questionList.size(), oneSecond = 1000;
        if( mCountDownTimer == null ) {
            mCountDownTimer = new CountDownTimer( timeElapsed, oneSecond ) {
                @Override
                public void onTick( long millisUntilFinished ) {
                    long x = millisUntilFinished / 1000;
                    long seconds = x % 60;
                    x /= 60;
                    int minutes = ( int ) x % 60;
                    x /= 60;
                    int hours = ( int ) x % 24;
                    String text = "" + ( hours > 0 ? (hours + "h") : "" ) +
                            ( minutes > 0 ? ( minutes + "m" ) : "" ) +
                            seconds + "s";
                    mTimerTextView.setText(text);
                }

                @Override
                public void onFinish() {
                    DoFinalSubmission();
                }
            };
            mCountDownTimer.start();
        }
        UpdateQuestionView( mCurrentQuestionIndex );
    }

    private void DoFinalSubmission()
    {
        mCountDownTimer.cancel();
        final ArrayList<ChislevQuestion> questionList = ChislevQuestionDisplayActivity.GetQuestionList();
        final ArrayList<ChislevQuestion.ChislevSolutionFormat> solutionList = ChislevQuestionDisplayActivity.GetSolutionList();
        for( int i = 0; i != questionList.size(); ++i ){
            ChislevQuestion currentQuestion = questionList.get( i );
            ChislevQuestion.ChislevSolutionFormat currentGottenSolution = solutionList.get( i );

            final String answer = currentQuestion.getAnswer() == null ? "" : currentQuestion.getAnswer();
            if( currentQuestion.getChosenOption() == currentGottenSolution.getCorrectOption() ){
                if( currentGottenSolution.getCorrectText().equals( answer ) ){
                    currentGottenSolution.setIsCorrect();
                }
            }
        }

        new AlertDialog.Builder( getActivity() ).setMessage( R.string.display_solution_message )
                .setTitle( R.string.display_solution_title ).setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {
                        dialog.dismiss();
                    }
                }).show();
        RecordScores();
        ( ( ChislevQuestionDisplayActivity ) getActivity() ).FragmentWorkCompleted();
    }

    private void RecordScores()
    {
        // Todo
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /* Could be null if an error occurred before it's initialized in the main UI thread */
        if ( solutionsLoaderThread != null ) {
            try {
                solutionsLoaderThread.interrupt();
                solutionsLoaderThread.join();
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
            solutionsLoaderThread = null;
        }
    }

    private class ChislevSubjectProcessingTask extends AsyncTask<Void, Void, ArrayList<ChislevQuestion> >
    {
        private ArrayList<ChislevQuestion> FilterQuestions( final Map<String, ArrayList<ChislevQuestion> > questionMap,
                                                            final String filterCriteria )
        {
            boolean isBasicFilter = filterCriteria.compareToIgnoreCase( "Beginner" ) == 0 ||
                    filterCriteria.compareToIgnoreCase( "Intermediate" ) == 0 ||
                    filterCriteria.compareToIgnoreCase( "Hard" ) == 0;
            ArrayList<ChislevQuestion> questions;
            if( isBasicFilter ){
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
                    return "Hard";
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
                Log.d( TAG, except.getLocalizedMessage(), except );
            } catch ( XmlPullParserException exception ){
                Log.d( TAG, "XML could not be parsed.", exception );
            }
            return null;
        }

        @Override
        protected void onPostExecute( ArrayList<ChislevQuestion> questionArrayList )
        {
            ChislevQuestionDisplayActivity.SetQuestionList( questionArrayList );
            UpdateMainUIView();
        }
    }

    private class LoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor>
    {
        @SuppressWarnings( "unchecked" )
        @Override
        public Loader onCreateLoader( int id, Bundle args ) {
            final String subjectCode = ChislevSubjectsLaboratory.Get(getActivity()).GetSubjectItem(mSubjectIndex)
                    .getSubjectCode();
            final ArrayList<ChislevQuestion> questionList = ChislevQuestionDisplayActivity.GetQuestionList();
            String[] referenceIds = new String[questionList.size()];
            for (int i = 0; i != questionList.size(); ++i) {
                referenceIds[i] = questionList.get( i ).getReferenceID();
            }
            return new ChislevDatabaseManager.ChislevCursorLoader(getActivity(), referenceIds, subjectCode);
        }

        @SuppressWarnings( "unchecked" )
        @Override
        public void onLoadFinished( Loader loader, final Cursor data ) {
            solutionsLoaderThread = new Thread( new Runnable() {
                @Override
                public void run() {
                    ChislevDatabaseManager.ChislevSolutionsCursor solutionsCursor = ( ChislevDatabaseManager.ChislevSolutionsCursor) data;
                    if (solutionsCursor != null) {
                        ArrayList<ChislevQuestion.ChislevSolutionFormat> solutions = new ArrayList<>();
                        solutionsCursor.moveToFirst();
                        ChislevQuestion.ChislevSolutionFormat solutionFormat = solutionsCursor.GetSolution();
                        while (solutionFormat != null) {
                            solutions.add(solutionFormat);
                            solutionsCursor.moveToNext();
                            solutionFormat = solutionsCursor.GetSolution();
                        }
                        ChislevQuestionDisplayActivity.SetAnswerList( solutions );
                    } else {
                        Log.d( TAG, "WTF is the solutions NULL?!" );
                    }
                }
            });
            solutionsLoaderThread.start();
        }

        @Override
        public void onLoaderReset( Loader loader ) {

        }
    }

    public static Fragment newInstance( int subjectIndex, int subjectLevel )
    {
        Bundle bundle = new Bundle();
        bundle.putInt( EXTRA_INDEX, subjectIndex );
        bundle.putInt( EXTRA_LEVEL, subjectLevel );
        ChislevQuestionDisplayFragment displayFragment = new ChislevQuestionDisplayFragment();
        displayFragment.setArguments( bundle );
        return displayFragment;
    }
}
