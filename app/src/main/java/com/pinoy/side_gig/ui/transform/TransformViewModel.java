package com.pinoy.side_gig.ui.transform;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class TransformViewModel extends ViewModel {

    private final MutableLiveData<List<String>> mTexts;

    public TransformViewModel() {
        mTexts = new MutableLiveData<>();
        List<String> texts = new ArrayList<>();
        texts.add("CLEANING SERVICES");
        texts.add("MANICURIST");
        texts.add("SHOEMAKER");
        texts.add("COOKING SERVICE");
        texts.add("ELECTRICIAN");
        texts.add("DRIVER");
        texts.add("BEAUTY CARE SERVICES");
        texts.add("LAUNDROMAT");
        texts.add("PAINTER");
        texts.add("CARPENTER");
        texts.add("PLUMBER");
        texts.add("TUTOR");
        texts.add("BABY SITTER");
        texts.add("FRIDGE REPAIRING SERVICES");
        texts.add("COMPUTER REPAIRING SERVICES");
        texts.add("PHOTOGRAPHY SERVICES");
        texts.add("VIDEOGRAPHY SERVICES");
        texts.add("EDITING SERVICES");
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