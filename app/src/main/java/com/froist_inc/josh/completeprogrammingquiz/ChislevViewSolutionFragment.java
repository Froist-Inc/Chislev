package com.froist_inc.josh.completeprogrammingquiz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

public class ChislevViewSolutionFragment extends Fragment
{
    private int mSolutionIndex;
    private ChislevQuestion.ChislevSolutionFormat mSolution;
    private ChislevQuestion mQuestion;

    private static final String SOLUTION_INDEX = "INDEX";

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        getActivity().setTitle( R.string.display_solution_title );

        mSolutionIndex = getArguments().getInt( SOLUTION_INDEX );
        mSolution = ChislevQuestionDisplayActivity.GetSolutionList().get( mSolutionIndex );
        mQuestion = ChislevQuestionDisplayActivity.GetQuestionList().get( mSolutionIndex );
    }

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.solutions_fragment, container, false );
        TextView questionNumberTextView = ( TextView ) view.findViewById( R.id.question_numberTextView );
        questionNumberTextView.setText( getString( R.string.question_number, mSolutionIndex + 1 ) );

        TextView rightWrongTextView = ( TextView ) view.findViewById( R.id.right_wrong_textView );
        if( mSolution.IsCorrect() ){
            rightWrongTextView.setText( R.string.correct );
            rightWrongTextView.setBackgroundResource( R.color.green );
        } else {
            rightWrongTextView.setText( R.string.wrong );
            rightWrongTextView.setBackgroundResource( R.color.red );
        }
        TextView questionTextView = ( TextView ) view.findViewById( R.id.solutions_questionTextView );
        questionTextView.setText( mQuestion.getQuestion() );

        WebView codeView = ( WebView ) view.findViewById( R.id.code_display_webView );
        codeView.loadData( mQuestion.getCode(), "text/html", "UTF-8" );
        codeView.reload();

        /* The options started from index 1, that's always one integer past the array */
        int index = ( int ) mSolution.getCorrectOption() - 1;
        String answerString = mQuestion.getAvailableOptions().get( index ) + " " + mSolution.getCorrectText();
        TextView correctAnswerTextView = ( TextView ) view.findViewById( R.id.correct_answer_textView );
        correctAnswerTextView.setText( answerString );

        WebView reasonShowWebView = ( WebView ) view.findViewById( R.id.reason_show_webView );
        reasonShowWebView.loadData( mSolution.getExplanation(), "text/html", "UTF-8" );
        reasonShowWebView.reload();
        return view;
    }

    public static Fragment newInstance( int solutionIndex )
    {
        Bundle bundle = new Bundle();
        bundle.putInt( SOLUTION_INDEX, solutionIndex );
        ChislevViewSolutionFragment solutionFragment = new ChislevViewSolutionFragment();
        solutionFragment.setArguments( bundle );
        return solutionFragment;
    }
}
