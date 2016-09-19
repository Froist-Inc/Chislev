package com.froist_inc.josh.completeprogrammingquiz;

import android.support.v4.app.Fragment;

public class ChislevQuestionDisplayActivity extends ChislevGenericActivity
{
    @Override
    public Fragment GetFragment()
    {
        return new ChislevQuestionDisplayFragment();
    }

    @Override
    public int GetLayoutID() {
        return R.layout.subject_chooser_activity;
    }

    @Override
    public int GetContainerID() {
        return R.id.subject_choose_layoutContainer;
    }
}
