package com.froist_inc.josh.completeprogrammingquiz;

public class ChislevScoresFormat
{
    public static final String LEVEL = "level";
    public static final String TIME_STARTED = "time_started";
    public static final String TIME_USED = "time_used";
    public static final String TOTAL_SCORE = "total_score";
    public static final String QUESTION_ARITY = "question_arity";
    public static final String DAY_QUIZ_TAKEN = "day";

    private String mQuizLevelTaken;
    private String mDayQuizTaken;
    private String mTimeStarted;
    private String mTotalTimeUsed;
    private int    mTotalScores;
    private int    mQuestionTotal;

    public String getQuizLevelTaken() {
        return mQuizLevelTaken;
    }

    public void setQuizLevelTaken( String quizLevelTaken ) {
        mQuizLevelTaken = quizLevelTaken;
    }

    public String getTimeStarted() {
        return mTimeStarted;
    }

    public void setTimeStarted( String timeStarted ) {
        mTimeStarted = timeStarted;
    }

    public String getTimeUsed() {
        return mTotalTimeUsed;
    }

    public void setTotalTimeUsed( String timeElapsed ) {
        mTotalTimeUsed = timeElapsed;
    }

    public int getTotalScores() {
        return mTotalScores;
    }

    public void setTotalScores( int totalScores ) {
        mTotalScores = totalScores;
    }

    public int getQuestionTotal() {
        return mQuestionTotal;
    }

    public void setQuestionTotal( int questionTotal ) {
        mQuestionTotal = questionTotal;
    }

    public String getDayQuizTaken() {
        return mDayQuizTaken;
    }

    public void setDayQuizTaken( String dayQuizTaken ) {
        mDayQuizTaken = dayQuizTaken;
    }
}
