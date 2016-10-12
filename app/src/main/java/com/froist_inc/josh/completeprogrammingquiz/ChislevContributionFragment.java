package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChislevContributionFragment extends Fragment
{
    private String mSubjectName, mQuestionText, mUsersName, mSourceCodeText;
    private String mOptionOneText, mOptionTwoText, mOptionThreeText, mOptionFourText;
    private String mDifficultyLevelText, mInputText, mExplanationText, mHintText;

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState )
    {
        setRetainInstance( true );
        View view = inflater.inflate( R.layout.contribution_fragment, container, false );
        EditText subjectNameEditText = ( EditText ) view.findViewById( R.id.contribution_subject_name );
        subjectNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSubjectName = s.toString();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        EditText questionEditText = ( EditText ) view.findViewById( R.id.contribution_question_text );
        questionEditText.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                mQuestionText = s.toString();
            }
            @Override
            public void afterTextChanged( Editable s ) {}
        });
        ( ( EditText ) view.findViewById( R.id.contribution_source_code )).addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSourceCodeText = s.toString();
            }
            @Override
            public void afterTextChanged( Editable s ) {}
        });
        ( ( EditText ) view.findViewById( R.id.contribution_name_text )).addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUsersName = s.toString();
            }
            @Override
            public void afterTextChanged( Editable s ) {}
        });

        ( ( EditText ) view.findViewById( R.id.contribution_difficulty_text )).addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDifficultyLevelText = s.toString();
            }
            @Override
            public void afterTextChanged( Editable s ) {}
        });
        ( ( EditText ) view.findViewById( R.id.contribution_input_text )).addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mInputText = s.toString();
            }
            @Override
            public void afterTextChanged( Editable s ) {}
        });
        ( ( EditText ) view.findViewById( R.id.contribution_explanation_text )).addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mExplanationText = s.toString();
            }
            @Override
            public void afterTextChanged( Editable s ) {}
        });
        ( ( EditText ) view.findViewById( R.id.contribution_hint_text )).addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mHintText = s.toString();
            }
            @Override
            public void afterTextChanged( Editable s ) {}
        });

        ( ( EditText ) view.findViewById( R.id.contribution_option_one_text )).addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mOptionOneText = s.toString();
            }
            @Override
            public void afterTextChanged( Editable s ) {}
        });

        ( ( EditText ) view.findViewById( R.id.contribution_option_two_text )).addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mOptionTwoText = s.toString();
            }
            @Override
            public void afterTextChanged( Editable s ) {}
        });

        ( ( EditText ) view.findViewById( R.id.contribution_option_three_text )).addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mOptionThreeText = s.toString();
            }
            @Override
            public void afterTextChanged( Editable s ) {}
        });

        ( ( EditText ) view.findViewById( R.id.contribution_option_four_text )).addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mOptionFourText = s.toString();
            }
            @Override
            public void afterTextChanged( Editable s ) {}
        });

        Button sendButton = ( Button ) view.findViewById( R.id.contributor_sendButton );
        sendButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                SendEmail();
            }
        });
        return view;
    }

    private void SendEmail()
    {
        boolean anyInputIsEmpty = mQuestionText == null || mSourceCodeText == null || mDifficultyLevelText == null
                || mOptionOneText == null || mOptionTwoText == null || mOptionThreeText == null
                || mOptionFourText == null || mHintText == null || mExplanationText == null
                || mSubjectName == null;

        if( anyInputIsEmpty ){
            Toast.makeText( getActivity(), getString( R.string.fill_empty_blanks ), Toast.LENGTH_SHORT ).show();
            return;
        }

        final String body = "%%\n" + "$$ Question\n" + mQuestionText + "\n\n"
                + "$$ Code\n" + mSourceCodeText + "\n\n"
                + "$$ Level\n" + mDifficultyLevelText + "\n\n"
                + "$$ Options\n" + mOptionOneText + "\n" + mOptionTwoText + "\n" + mOptionThreeText
                + "\n" + mOptionFourText + "\n\n" + "$$ Hint\n" + mHintText + "\n\n"
                + "$$ Explanation\n" + mExplanationText + "\n\n"
                + "$$ Output\n" + mInputText + "\n";
        final String subject = mSubjectName + " contribution" + ( mUsersName == null ? "." : " from " + mUsersName );

        Log.d( "Subject Text", subject );
        Log.d( "Body Text", body );
        Intent emailIntent = new Intent( Intent.ACTION_SENDTO, Uri.fromParts( "mailto", "froist_inc@yahoo.com", null ));
        emailIntent.putExtra( Intent.EXTRA_SUBJECT, subject );
        emailIntent.putExtra( Intent.EXTRA_TEXT, body );
        startActivity( Intent.createChooser( emailIntent, "Send email" ) );
    }
}
