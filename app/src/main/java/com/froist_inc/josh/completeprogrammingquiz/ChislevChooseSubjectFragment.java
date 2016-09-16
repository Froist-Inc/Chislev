package com.froist_inc.josh.completeprogrammingquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Josh on 16-Sep-16.
 */
public class ChislevChooseSubjectFragment extends Fragment
{
    static private final String TAG = "ChooseSubjectFragment";
    static public final int POSITION_REQUEST_CODE = 1;
    static public final int LEVEL_REQUEST_CODE = 2;

    private int mSubjectIndex = -1;

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
    }

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.subject_chooser_fragment, container );
        ChislevChooseSubjectDialog dialogFragment = ChislevChooseSubjectDialog.NewInstance();
        dialogFragment.setTargetFragment( ChislevChooseSubjectFragment.this, POSITION_REQUEST_CODE );
        dialogFragment.show( getActivity().getSupportFragmentManager(), TAG );
        return view;
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        super.onActivityResult( requestCode, resultCode, data );
        if( requestCode == Activity.RESULT_CANCELED ) {
            getActivity().finish();
        } else {
            if( requestCode == POSITION_REQUEST_CODE ){
                mSubjectIndex = data.getIntExtra( ChislevChooseSubjectDialog.SUBJECT_POSITION, -1 );
                getActivity().finish();
//                ShowNextDialog();
//            } else if ( requestCode == LEVEL_REQUEST_CODE ){
//                mSubjectDifficultyLevel = data.getIntArrayExtra( ChislevChooseSubjectDialog.DIFFICULTY_LEVEL, -1 );
            }
        }
    }
}
