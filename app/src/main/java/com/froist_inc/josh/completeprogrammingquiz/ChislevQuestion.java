package com.froist_inc.josh.completeprogrammingquiz;

import java.util.ArrayList;

public class ChislevQuestion
{
    private String mExplanation;
    private String mHint;
    private String mOwner;
    private String mQuestion;
    private ArrayList<String> mAvailableOptions;
    private String mCode;
    private String mDifficultyLevel;
    private String mReferenceID;

    private int mChosenOption;
    private String mAnswer;
    private boolean mHintUsed = false;

    public void setHintUsed()
    {
        mHintUsed = true;
    }

    public int getChosenOption() {
        return mChosenOption;
    }

    public void setChosenOption( int mChosenOption ) {
        this.mChosenOption = mChosenOption;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public void setAnswer(String mAnswer) {
        this.mAnswer = mAnswer;
    }

    public ChislevQuestion() {}

    public String getDifficultyLevel() {
        return mDifficultyLevel;
    }

    public void setDifficultyLevel(String mDifficultyLevel) {
        this.mDifficultyLevel = mDifficultyLevel;
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

    public String getExplanation() {
        return mExplanation;
    }

    public void setExplanation( final String explanation ) {
        mExplanation = explanation;
    }

    public String getReferenceID() {
        return mReferenceID;
    }

    public void setReferenceID( String referenceID ) {
        this.mReferenceID = referenceID;
    }
}
