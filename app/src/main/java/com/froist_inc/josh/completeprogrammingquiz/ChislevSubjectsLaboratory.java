package com.froist_inc.josh.completeprogrammingquiz;

import android.content.Context;

import java.util.ArrayList;

public class ChislevSubjectsLaboratory
{
    private static ChislevSubjectsLaboratory subjectsLaboratoryInstance;
    private ArrayList<ChislevSubjectInformation> mItems;
    private Context mContext;

    public static ChislevSubjectsLaboratory Get( Context context )
    {
        if( subjectsLaboratoryInstance == null ){
            subjectsLaboratoryInstance = new ChislevSubjectsLaboratory( context.getApplicationContext() );
        }
        return subjectsLaboratoryInstance;
    }

    public ArrayList<ChislevSubjectInformation> GetSubjects()
    {
        return mItems;
    }

    public ChislevSubjectInformation GetSubjectItem( int index )
    {
        return mItems.get( index );
    }

    public void SetSubjects( ArrayList<ChislevSubjectInformation> subjects )
    {
        mItems = subjects;
    }

    public void AddSubjects( ChislevSubjectInformation subjectInformation )
    {
        mItems.add( subjectInformation );
    }

    private ChislevSubjectsLaboratory( Context context )
    {
        mContext = context;
        mItems = new ArrayList<>();
    }
}
