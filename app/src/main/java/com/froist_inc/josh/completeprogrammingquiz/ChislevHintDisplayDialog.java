package com.froist_inc.josh.completeprogrammingquiz;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

public class ChislevHintDisplayDialog extends android.support.v4.app.DialogFragment
{
    public static final String TAG = "ChislevHintDisplayDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {
        TextView textView = new TextView( getActivity() );
        textView.setText( getString( R.string.show_hint_confirmation ) );
        return new AlertDialog.Builder( getActivity() ).setTitle( getString( R.string.hint ) )
                .setView( textView )
                .setPositiveButton( android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {
                        getTargetFragment().onActivityResult( ChislevQuestionDisplayFragment.HINT_CHECK,
                                Activity.RESULT_OK, null );
                    }
                })
                .setNegativeButton( android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getTargetFragment().onActivityResult( ChislevQuestionDisplayFragment.HINT_CHECK,
                                Activity.RESULT_CANCELED, null );
                    }
                })
                .create();
    }
}
