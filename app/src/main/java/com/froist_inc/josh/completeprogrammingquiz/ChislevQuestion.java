package com.froist_inc.josh.completeprogrammingquiz;

import java.util.ArrayList;

public class ChislevQuestion
{
    private final String mOwner;
    private final String mQuestion;
    private final String mCode;
    private final ArrayList<String> mAvailableOptions;
    private final String mHint;
    private final String mCorrectAnswer;

    boolean mIsQuestionAnswered = false;
    int mAssociatedScore = 0;

    public ChislevQuestion( final String questionOwner, final String question,
                            final String questionCode, final String hintToQuestion,
                            final String correctAnswer, ArrayList<String> availableOptions )
    {
        mOwner = questionOwner;
        mQuestion = question;
        mCode = questionCode;
        mHint = hintToQuestion;
        mCorrectAnswer = correctAnswer;
        mAvailableOptions = availableOptions;
    }

    public boolean isQuestionAnswered()
    {
        return mIsQuestionAnswered;
    }

    public void setIsQuestionAnswered( boolean questionAnswered )
    {
        mIsQuestionAnswered = questionAnswered;
    }
}
