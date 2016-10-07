package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChislevScoresFragment extends Fragment {
    private ArrayList<ChislevSubjectInformation> mInformationList;
    private ExpandableListView mExpandableListView;
    Map<String, ArrayList<ChislevScoresFormat>> mScoreMap =
            Collections.synchronizedMap( new HashMap<String, ArrayList<ChislevScoresFormat>>() );

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.scores_fragment, container, false );
        mExpandableListView = ( ExpandableListView ) view.findViewById( R.id.expandableScoreListView );
        getActivity().setTitle( R.string.scores );
        LoadConfigFile();
        return view;
    }

    private void LoadConfigFile() {
        final ArrayList<ChislevSubjectInformation> subjectInformation = ChislevSubjectsLaboratory.Get( getActivity() )
                .GetSubjects();
        if ( subjectInformation == null ) {
            final ChislevUtilities.ChislevLoadConfigFileTask fileTask = new ChislevUtilities.ChislevLoadConfigFileTask
                    ( getActivity() );
            fileTask.SetUiThreadOnPostExecuteListener(new ChislevUtilities.ChislevLoadConfigFileTask.MainUiThreadListener() {
                @Override
                public void UiThreadOnPostExecute(ArrayList<ChislevSubjectInformation> informationArrayList) {
                    mInformationList = informationArrayList;
                }
            });
            fileTask.execute();
        } else {
            mInformationList = subjectInformation;
        }

        if ( mInformationList.size() != 0 ) {
            for ( int i = 0; i != mInformationList.size(); ++i) {
                getLoaderManager().initLoader( i, null, new ScoresLoaderCallback() );
            }
        }
    }

    private class ScoresLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        private String[] tableColumns = new String[]{
                ChislevScoresFormat.LEVEL, ChislevScoresFormat.TIME_STARTED, ChislevScoresFormat.TIME_USED,
                ChislevScoresFormat.DAY_QUIZ_TAKEN, ChislevScoresFormat.TOTAL_SCORE, ChislevScoresFormat.QUESTION_ARITY
        };

        @Override
        public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
            final String dataId = mInformationList.get( id ).getSubjectCode();
            return new ChislevScoresDatabaseManager.ChislevScoresCursorLoader( getActivity(), tableColumns, dataId );
        }

        @Override
        public void onLoadFinished( final Loader<Cursor> loader, final Cursor data ) {
            ResultStoringThread storingThread = new ResultStoringThread( loader, data );
            storingThread.start();
        }

        @Override
        public void onLoaderReset( Loader<Cursor> loader ) {
            if ( mExpandableListView != null ) {
                // cast avoids ambiguous overload problem
                mExpandableListView.setAdapter(( ExpandableListAdapter ) null);
            }
        }
    }

    private class ResultStoringThread extends Thread
    {
        private ChislevScoresDatabaseManager.ScoresExtractingCursor mDataCursor;
        private final String mDataId;

        ResultStoringThread( final Loader<Cursor> loader, final Cursor data )
        {
            mDataCursor = ( ChislevScoresDatabaseManager.ScoresExtractingCursor ) data;
            mDataId = (( ChislevScoresDatabaseManager.ChislevScoresCursorLoader ) loader ).GetId();
        }

        @Override
        public void run()
        {
            ArrayList<ChislevScoresFormat> scores = new ArrayList<>();
            if ( mDataCursor != null ) {
                mDataCursor.moveToFirst();
                ChislevScoresFormat score = mDataCursor.GetScore();
                while ( score != null ) {
                    scores.add( score );
                    mDataCursor.moveToNext();
                    score = mDataCursor.GetScore();
                }
            }
            synchronized ( this ){
                mScoreMap.put( mDataId, scores );
            }

            if( mScoreMap.size() == mInformationList.size() ){
                getActivity().runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<String> header = new ArrayList<>();
                        for ( int i = 0; i != mInformationList.size(); ++i ) {
                            header.add( mInformationList.get( i ).getSubjectCode() );
                        }
                        mExpandableListView.setAdapter( new ExpandableScoreAdapter( getActivity(), header, mScoreMap ));
                    }
                });
            }
        }
    }

    private class ExpandableScoreAdapter extends BaseExpandableListAdapter
    {
        private ArrayList<String> mHeaders;
        private ArrayList<String> mDisplayHeaders;
        private Map<String, ArrayList<ChislevScoresFormat>> mData;
        private Context mContext;

        ExpandableScoreAdapter( Context context, ArrayList<String> headers,
                                Map<String, ArrayList<ChislevScoresFormat>> data)
        {
            mContext = context;
            mHeaders = headers;
            mData = data;
            mDisplayHeaders = new ArrayList<>();
            for ( ChislevSubjectInformation info : mInformationList ) {
                mDisplayHeaders.add( info.getSubjectName() );
            }
        }

        @Override
        public long getChildId( int groupPosition, int childPosition ) {
            return childPosition;
        }

        @Override
        public long getGroupId( int groupPosition ) {
            return groupPosition;
        }

        @Override
        public Object getChild( int groupPosition, int childPosition ) {
            return mData.get( mHeaders.get( groupPosition ) ).get( childPosition );
        }

        @Override
        public int getChildrenCount( int groupPosition ) {
            return mData.get( mHeaders.get( groupPosition ) ).size();
        }

        @Override
        public int getGroupCount() {
            return mHeaders.size();
        }

        @Override
        public Object getGroup( int groupPosition ) {
            return mHeaders.get( groupPosition );
        }

        @Override
        public View getChildView( int groupPosition, int childPosition, boolean isLastChild, View convertView,
                                  ViewGroup parent )
        {
            ChislevScoresFormat score = ( ChislevScoresFormat ) getChild( groupPosition, childPosition );
            if( convertView == null ) {
                convertView = GetInflatedView( R.layout.scores_child_view, parent );
            }

            final TextView dateTimeTextView = ( TextView ) convertView.findViewById( R.id.date_time_textView );
            final TextView levelTextView = ( TextView ) convertView.findViewById( R.id.difficulty_level_textView );
            final TextView timeUsedTextView = ( TextView ) convertView.findViewById( R.id.time_used_textView );
            final TextView scoreTextView = ( TextView ) convertView.findViewById( R.id.score_textView );

            dateTimeTextView.setText( getString( R.string.score_day_taken_text, score.getDayQuizTaken(),
                    score.getTimeStarted() ));
            levelTextView.setText( getString( R.string.score_level_text, score.getQuizLevelTaken() ));
            scoreTextView.setText( getString( R.string.score_total_text, score.getTotalScores(),
                    score.getQuestionTotal() ) );
            timeUsedTextView.setText( getString( R.string.score_time_used_text, score.getTimeUsed() ) );
            return convertView;
        }

        private View GetInflatedView( int layoutId, ViewGroup parent )
        {
            LayoutInflater inflater = ( LayoutInflater ) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            return inflater.inflate( layoutId, parent, false );
        }

        @Override
        public View getGroupView( int groupPosition, boolean isExpanded, View convertView, ViewGroup parent )
        {
            if( convertView == null ) {
                convertView = GetInflatedView( R.layout.scores_grouproot_view, parent );
            }
            TextView text = ( TextView ) convertView.findViewById( R.id.scores_group_root_textView );
            text.setText( mDisplayHeaders.get( groupPosition ) );
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
