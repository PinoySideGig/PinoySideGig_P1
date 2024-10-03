package com.pinoy.side_gig.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SlideshowViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private String title;
    private String date;
    private String remarks;

    public SlideshowViewModel(String title, String date, String remarks) {
        mText = new MutableLiveData<>();
        this.title = title;
        this.date = date;
        this.remarks = remarks;
//        mText.setValue("This is slideshow fragment");
    }
    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getRemarks() {
        return remarks;
    }

    public LiveData<String> getText() {
        return mText;
    }
}