package com.froist_inc.josh.completeprogrammingquiz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChislevHandlerThread extends HandlerThread
{
    private Handler mDefaultHandler;
    private Handler mMainUIHandler;
    Listener mListener;
    ChislevFileManager mFileManager;
    ChislevNetworkManager mNetworkManager;
    Context mContext;

    public interface Listener {
        void OnSubjectCodeDataObtained( ChislevSubjectInformation subjectInformation );
    }

    public static final String TAG = "ChislevHandlerThread";
    private static final String ANSWERS_FILENAME = "answer.sqlite";
    private static final String ICON_FILENAME = "icon.png";
    static final int DATA_INITIALIZE = 0;

    Map<ChislevSubjectInformation, String> mData =
            Collections.synchronizedMap( new HashMap< ChislevSubjectInformation, String>() );

    public ChislevHandlerThread( Context context, Handler mainUIHandler )
    {
        super( TAG );
        mMainUIHandler = mainUIHandler;
        mContext = context;
        mFileManager = new ChislevFileManager( mContext );
        mNetworkManager = new ChislevNetworkManager( mContext );
    }

    public void setListener( Listener listener )
    {
        mListener = listener;
    }

    @SuppressLint({"", "HandlerLeak"})
    @Override
    protected void onLooperPrepared() {
        mDefaultHandler = new Handler(){
            @Override
            public void handleMessage( Message msg )
            {
                if( msg.what == ChislevHandlerThread.DATA_INITIALIZE ){
                    @SuppressWarnings( "unchecked" )
                    ChislevSubjectInformation info = ( ChislevSubjectInformation ) msg.obj;
                    HandleMessage( info );
                }
            }
        };
    }

    private void HandleMessage( final ChislevSubjectInformation subject )
    {
        try {
            boolean subjectDirectoryExists = mFileManager.FileExists( subject.getSubjectCode() );
            if( !subjectDirectoryExists ){
                mFileManager.CreateDirectory( subject.getSubjectCode() );
            }
            boolean subjectQuestionExists = mFileManager.FileExists( subject.getSubjectFilename(),
                    subject.getSubjectCode(), false );
            if( !subjectQuestionExists ){
                byte[] questions = mNetworkManager.GetData( subject.getSubjectDataUrl() );
                mFileManager.SaveDataToFile( questions, subject.getSubjectFilename(), subject.getSubjectCode() );
            }

            if( subject.getSubjectAnswerUrl() != null ){
                boolean answersDatabaseExists = mFileManager.FileExists( ANSWERS_FILENAME, subject.getSubjectCode(), false );
                if( !answersDatabaseExists ){
                    byte[] answersData = mNetworkManager.GetData( subject.getSubjectAnswerUrl() );
                    mFileManager.SaveDataToFile( answersData, ANSWERS_FILENAME, subject.getSubjectCode() );
                }
            }

            if( subject.getSubjectIconUrl() != null ){
                boolean iconExists = mFileManager.FileExists( ICON_FILENAME, subject.getSubjectCode(), false );
                if( !iconExists ){
                    byte[] iconData = mNetworkManager.GetData( subject.getSubjectIconUrl() );
                    mFileManager.SaveDataToFile( iconData, ICON_FILENAME, subject.getSubjectCode() );
                }
            }
            subject.setIsAllSet( true );
            Object lock = new Object();
            synchronized( lock ) {
                mMainUIHandler.post( new Runnable() {
                    @Override
                    public void run() {
                        mData.remove( subject );
                        Log.d( TAG, "Handling message." );
                        mListener.OnSubjectCodeDataObtained( subject );
                    }
                });
                lock.notifyAll();
            }
            Log.d( TAG, "Outside" );
        } catch ( IOException exception ){
            Log.d( TAG, exception.getLocalizedMessage(), exception );
        } finally {
            mData.remove( subject );
        }
    }

    public void Prepare( ChislevSubjectInformation subjectInformation )
    {
        mData.put( subjectInformation, subjectInformation.getSubjectName() );
        mDefaultHandler.obtainMessage( ChislevHandlerThread.DATA_INITIALIZE, subjectInformation ).sendToTarget();
    }
}
