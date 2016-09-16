package com.froist_inc.josh.completeprogrammingquiz;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.GridView;

public class ChislevChooseLevelDialog extends DialogFragment
{
    GridView mLevelGridView = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate( savedInstanceState );
        setRetainInstance( true );
    }

    // Todo -> Work-in-progress
    @NonNull
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState )
    {
        mLevelGridView = new GridView( getActivity() );
        mLevelGridView.setId( R.id.levelGridView );

        return super.onCreateDialog(savedInstanceState);
    }
}
