package com.pinoy.side_gig.ui.reflow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReflowViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ReflowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Account tab");
    }

    public LiveData<String> getText() {
        return mText;
    }
}