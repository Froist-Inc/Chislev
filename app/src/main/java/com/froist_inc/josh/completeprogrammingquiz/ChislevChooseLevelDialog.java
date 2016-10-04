package com.froist_inc.josh.completeprogrammingquiz;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class ChislevChooseLevelDialog extends DialogFragment
{
    public static final String DIFFICULTY_LEVEL = "DIFFICULTY_LEVEL";
    private static final int LEVEL_SIZE = 4; // Beginner, Intermediate, Advanced, Random Level -- in that particular order.
    public static final String TAG = "ChislevChooseLevelDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState )
    {
        GridView levelGridView = new GridView( getActivity() );
        levelGridView.setGravity( Gravity.CENTER );
        levelGridView.setId( R.id.levelGridView );
        levelGridView.setNumColumns( GridView.AUTO_FIT );
        levelGridView.setStretchMode( GridView.STRETCH_COLUMN_WIDTH );

        ArrayList<Integer> dummyList = new ArrayList<>();
        for ( int i = 0; i != LEVEL_SIZE; i++ ) {
            dummyList.add( i );
        }
        levelGridView.setAdapter( new ChislevLevelAdapter( dummyList ) );
        levelGridView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
                SendTargetResponse( Activity.RESULT_OK, position );
            }
        });
        return new AlertDialog.Builder( getActivity() )
                .setTitle( R.string.difficulty_level_title )
                .setView( levelGridView )
                .setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SendTargetResponse( Activity.RESULT_CANCELED, -1 );
                    }
                }).create();
    }

    private void SendTargetResponse( int result, int position )
    {
        Fragment targetFragment = getTargetFragment();
        if( targetFragment != null )
        {
            Intent resultIntent = new Intent();
            resultIntent.putExtra( DIFFICULTY_LEVEL, position );
            targetFragment.onActivityResult( ChislevSubjectPresenterFragments.LEVEL_REQUEST_CODE, result, resultIntent );
            this.dismiss();
        }
    }

    private class ChislevLevelAdapter extends ArrayAdapter<Integer>
    {
        ChislevLevelAdapter( ArrayList<Integer> list )
        {
            super( getActivity(), 0, list );
        }

        @Override
        public View getView( int position, View convertView, ViewGroup parent ) {
            if( convertView == null ) {
                convertView = getActivity().getLayoutInflater().inflate( R.layout.subject_level_fragment, parent, false );
            }

            ImageView imageView = ( ImageView ) convertView.findViewById( R.id.subject_level_imageView );
            switch( position ){
                case 0:
                    imageView.setImageResource( R.drawable.beginner_icon );
                    break;
                case 1:
                    imageView.setImageResource( R.drawable.intermediate_icon );
                    break;
                case 2:
                    imageView.setImageResource( R.drawable.advanced_icon );
                    break;
                case 3: default:
                    imageView.setImageResource( R.drawable.random_icon );
                    break;
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return LEVEL_SIZE;
        }
    }
}
