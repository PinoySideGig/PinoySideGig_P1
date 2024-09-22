package com.pinoy.side_gig.data.network;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class Sync extends BaseSync{

    public Sync (Context context)
    {
        super(context);
        TAG = "Sync";
    }

//    @Override
//    public void get(String email, Result.Listener listener) {

//    }

    @Override
    public void get(String email,String pass, Result.Listener listener) {
        final Map<String, String> params = new HashMap<>();
        String url = SYNC_CONFIG_BASE_URL;
        Log.e("xx", params.toString());
//        String[] split = email.split("/");
        params.put("email", email);
        params.put("pass", pass);
        request(url, params, listener);
    }

    @Override
    public void sync(Result.Listener listener) {

    }

    @Override
    String prepareData() {
        return "";
    }

    @Override
    boolean saveData(JSONArray sync_data) {
        return false;
    }
}
