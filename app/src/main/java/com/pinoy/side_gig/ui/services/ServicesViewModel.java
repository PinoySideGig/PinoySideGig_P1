package com.pinoy.side_gig.ui.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ServicesViewModel extends ViewModel {

    private final MutableLiveData<List<String>> mTexts;

    public ServicesViewModel() {
        mTexts = new MutableLiveData<>();
        List<String> texts = new ArrayList<>();
        texts.add("BABY SITTER");
        texts.add("BEAUTY CARE SERVICES");
        texts.add("CARPENTER");
        texts.add("CLEANING SERVICES");
        texts.add("COMPUTER REPAIRING SERVICES");
        texts.add("COOKING SERVICE");
        texts.add("DRIVER");
        texts.add("EDITING SERVICES");
        texts.add("ELECTRICIAN");
        texts.add("FRIDGE REPAIRING SERVICES");
        texts.add("LAUNDROMAT");
        texts.add("MANICURIST");
        texts.add("PAINTER");
        texts.add("PHOTOGRAPHY SERVICES");
        texts.add("PLUMBER");
        texts.add("SHOEMAKER");
        texts.add("TUTOR");
        texts.add("VIDEOGRAPHY SERVICES");
        texts.add("WELDER");
//        for (int i = 1; i <= 16; i++) {
//            texts.add("Service " + i);
//        }
        mTexts.setValue(texts);
    }

    public LiveData<List<String>> getTexts() {
        return mTexts;
    }
}