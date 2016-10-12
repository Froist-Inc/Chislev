package com.froist_inc.josh.completeprogrammingquiz;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ChislevHintDisplayDialog extends android.support.v4.app.DialogFragment
{
    public static final String TAG = "ChislevHintDisplayDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        final Dialog dialog = new Dialog( getActivity() );
        dialog.setTitle( R.string.hint );
        dialog.setCancelable( false );
        dialog.setContentView( R.layout.hint_display_fragment );
        Button yesButton = ( Button ) dialog.findViewById( R.id.yesButton );
        yesButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                if( getTargetFragment() != null ) {
                    getTargetFragment().onActivityResult( ChislevQuestionDisplayFragment.HINT_CHECK, Activity.RESULT_OK,
                            null );
                }
                dialog.dismiss();
            }
        });
        TextView promptMessage = ( TextView ) dialog.findViewById( R.id.text );
        promptMessage.setText( R.string.show_hint_confirmation );
        Button noButton = ( Button ) dialog.findViewById( R.id.noButton );
        noButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                if( getTargetFragment() != null ) {
                    getTargetFragment().onActivityResult( ChislevQuestionDisplayFragment.HINT_CHECK,
                            Activity.RESULT_CANCELED, null );
                }
                dialog.dismiss();
            }
        });
        return dialog;
    }
}
