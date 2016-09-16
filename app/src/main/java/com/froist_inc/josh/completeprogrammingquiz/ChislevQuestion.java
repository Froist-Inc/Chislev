package com.froist_inc.josh.completeprogrammingquiz;

import java.util.ArrayList;

public class ChislevQuestion
{
    private String mOwner;
    private String mQuestion;
    private String mCode;
    private ArrayList<String> mAvailableOptions;
    private String mHint;
    private String mCorrectAnswer;

    boolean mIsQuestionAnswered = false;
    int mAssociatedScore = 0;

    public ChislevQuestion() {}

    public boolean isQuestionAnswered()
    {
        return mIsQuestionAnswered;
    }

    public void setIsQuestionAnswered( boolean questionAnswered )
    {
        mIsQuestionAnswered = questionAnswered;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner( String mOwner ) {
        this.mOwner = mOwner;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public void setQuestion( String mQuestion ) {
        this.mQuestion = mQuestion;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode( String mCode ) {
        this.mCode = mCode;
    }

    public ArrayList<String> getAvailableOptions() {
        return mAvailableOptions;
    }

    public void setAvailableOptions( ArrayList<String> mAvailableOptions ) {
        this.mAvailableOptions = mAvailableOptions;
    }

    public String getHint() {
        return mHint;
    }

    public void setHint( String mHint ) {
        this.mHint = mHint;
    }

    public String getCorrectAnswer() {
        return mCorrectAnswer;
    }

    public void setCorrectAnswer( String mCorrectAnswer ) {
        this.mCorrectAnswer = mCorrectAnswer;
    }
}
