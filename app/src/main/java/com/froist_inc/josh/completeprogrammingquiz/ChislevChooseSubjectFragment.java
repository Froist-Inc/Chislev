package com.froist_inc.josh.completeprogrammingquiz;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Josh on 14-Sep-16.
 */

public class ChislevChooseSubjectFragment extends DialogFragment
{
    GridView mGridView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance( true );
    }

    @NonNull
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState )
    {
        mGridView = new GridView( getActivity() );
        mGridView.setAdapter( new ChislevSubjectAdapter() );
        return new AlertDialog.Builder( getActivity() )
                .setTitle( R.string.app_name ).setView( mGridView )
                .setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Todo --> bundle data back to target
                        getActivity().finish();
                    }
                }).create();
    }

    public static ChislevChooseSubjectFragment NewInstance()
    {
        // Todo --> will add extra data in Bundle, then attach bundle to ChislevChooseSubjectFragment
        return new ChislevChooseSubjectFragment();
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
            ChislevSubjectInformation subjectInformation = ChislevSubjectsLaboratory.Get( getActivity() )
                    .GetSubjectItem( position );
            if( convertView == null ){
                convertView = getActivity().getLayoutInflater().inflate( R.layout.element_layout_fragment,parent,
                        false );
            }
            ImageView imageView = ( ImageView ) convertView.findViewById( R.id.subject_item_imageView );
            TextView textCaption = ( TextView ) convertView.findViewById( R.id.subject_item_captionTextView );
            textCaption.setText( subjectInformation.getSubjectName() );

            // Todo --> Verify correctness.
            if( subjectInformation.getSubjectIconUrl() == null )
            {
                imageView.setImageResource( R.drawable.default_icon );
            } else {
                Bitmap bitmap = BitmapFactory.decodeFile( subjectInformation.getSubjectIconUrl() );
                imageView.setImageBitmap( bitmap );
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    // Todo --> Process onClicks
                }
            });
            return convertView;
        }
    }
}
