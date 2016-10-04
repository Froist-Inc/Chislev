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

class ChislevHandlerThread extends HandlerThread
{
    private Handler mDefaultHandler;
    private final Handler mMainUIHandler;
    private Listener mListener;
    private final ChislevFileManager mFileManager;
    private final ChislevNetworkManager mNetworkManager;
    private final Context mContext;

    public interface Listener {
        void OnSubjectCodeDataObtained( ChislevSubjectInformation subjectInformation );
    }

    private static final String TAG = "ChislevHandlerThread";
    private static final int DATA_INITIALIZE = 0;

    private final Map<ChislevSubjectInformation, String> mData =
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
            GrabData( subject.getSubjectDataUrl(), subject.getSubjectCode(), subject.getSubjectFilename() );
            GrabData( subject.getSubjectSolutionDBUrl(), subject.getSubjectCode(), ChislevSubjectInformation.SOLUTION_FILENAME );
            GrabData( subject.getSubjectDetailsCheckSums(), subject.getSubjectCode(), ChislevSubjectInformation.CHECKSUM_FILENAME );
            GrabData( subject.getSubjectIconUrl(), subject.getSubjectCode(), ChislevSubjectInformation.ICON_FILENAME );

            subject.setIsAllSet( true );
            /* In case the handler itself or the looper associated with the handler is destroyed, don't do nothing. */
            if( mDefaultHandler == null || mDefaultHandler.getLooper() == null ){
                return;
            }
            mMainUIHandler.post( new Runnable() {
                @Override
                public void run() {
                    mData.remove( subject );
                    mListener.OnSubjectCodeDataObtained( subject );
                }
            });
        } catch ( IOException exception ){
            Log.d( TAG, exception.getLocalizedMessage(), exception );
        } finally {
            mData.remove( subject );
        }
    }

    public void Prepare( final ChislevSubjectInformation subjectInformation )
    {
        if( mDefaultHandler.getLooper() != null ) {
            mData.put( subjectInformation, subjectInformation.getSubjectName() );
            mDefaultHandler.obtainMessage( ChislevHandlerThread.DATA_INITIALIZE, subjectInformation ).sendToTarget();
        }
    }

    private void GrabData( final String url, final String directory, final String filename )
            throws IOException
    {
        if( url != null ){
            if( !mFileManager.FileExists( filename, directory ) ){
                byte[] data = mNetworkManager.GetData( url );
                mFileManager.SaveDataToFile( data, filename, directory );
            }
        }
    }

    @Override
    public boolean quit() {
        mData.clear();
        this.interrupt();
        return super.quit();
    }
}
