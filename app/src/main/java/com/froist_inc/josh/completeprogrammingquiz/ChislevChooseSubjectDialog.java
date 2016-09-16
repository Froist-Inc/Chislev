package com.froist_inc.josh.completeprogrammingquiz;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class ChislevChooseSubjectDialog extends DialogFragment
{
    public static final String SUBJECT_POSITION = "SUBJECT_CHOSEN";
    GridView mGridView;

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);
        setRetainInstance( true );
    }

    @NonNull
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState )
    {
        mGridView = new GridView( getActivity() );
        mGridView.setId( R.id.subjectGridView );
        mGridView.setAdapter( new ChislevSubjectAdapter() );
        mGridView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View view, int position, long id )
            {
                SendResultToTarget( Activity.RESULT_OK, position );
            }
        });

        return new AlertDialog.Builder( getActivity() )
                .setTitle( R.string.app_name ).setView( mGridView )
                .setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SendResultToTarget( Activity.RESULT_CANCELED, -1 );
                    }
                }).create();
    }

    private void SendResultToTarget( int code, int position )
    {
        if( getTargetFragment() != null ){
            Intent intent = new Intent();
            intent.putExtra( SUBJECT_POSITION, position );
            getTargetFragment().onActivityResult( ChislevChooseSubjectFragment.POSITION_REQUEST_CODE, code, intent );
        }
    }

    public static ChislevChooseSubjectDialog NewInstance()
    {
        return new ChislevChooseSubjectDialog();
    }

    private class ChislevSubjectAdapter extends ArrayAdapter<ChislevSubjectInformation>
    {
        ChislevSubjectAdapter()
        {
            super( getActivity(), 0, ChislevSubjectsLaboratory.Get( getActivity() ).GetSubjects() );
        }

        @Override
        public View getView( int position, View convertView, ViewGroup parent )
        {
            ChislevSubjectInformation subjectInformation = ChislevSubjectsLaboratory.Get( getActivity() ).GetSubjectItem( position );
            if( convertView == null ){
                convertView = getActivity().getLayoutInflater().inflate( R.layout.element_layout_fragment, parent, false );
            }
            ImageView imageView = ( ImageView ) convertView.findViewById( R.id.subject_item_imageView );
            TextView textCaption = ( TextView ) convertView.findViewById( R.id.subject_item_captionTextView );
            textCaption.setText( subjectInformation.getSubjectName() );

            if( subjectInformation.getSubjectIconUrl() == null )
            {
                imageView.setImageResource( R.drawable.default_icon );
            } else {
                File path = new File( getActivity().getFilesDir(), subjectInformation.getSubjectCode() );
                File file = new File( path, subjectInformation.getIconFilename() );
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile( file.getCanonicalPath() );
                    imageView.setImageBitmap( bitmap );
                } catch ( IOException exception ){
                    imageView.setImageResource( R.drawable.default_icon );
                }
            }
            return convertView;
        }
    }
}
