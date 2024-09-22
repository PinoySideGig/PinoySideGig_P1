package com.pinoy.side_gig.data.network;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pinoy.side_gig.R;
//import com.pinoy.side_gig.database.DatabaseHelper;
import com.pinoy.side_gig.data.database.DatabaseHelper;
import com.pinoy.side_gig.data.database.VolleySingleton;
import com.pinoy.side_gig.data.Base64;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

public abstract class BaseSync {
    protected String TAG = "BaseSync";
    protected String DATA_SAVED_BROADCAST = "online-pinoysidegig.com";
    protected String SYNC_CONFIG_BASE_URL;

    protected Context context;
    protected DefaultRetryPolicy defaultRetryPolicy;

    protected BaseSync(Context context){
        this.context = context;
        SYNC_CONFIG_BASE_URL = context.getResources().getString(R.string.sync_config_base_url) + "sync.php";
        this.defaultRetryPolicy = new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    protected void request(String url, final Map<String, String> params, final Result.Listener listener)
    {

        final Result syncingResult = new Result();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(final String response)
                    {
                        syncingResult.message = response;
                        Log.e("JDTEST", response);
                        try
                        {
                            syncingResult.success = true;
                            JSONObject obj = new JSONObject(response);
                            if(obj.getString("email").isEmpty() || obj.getString("email")==""){
                                syncingResult.success = false;
                            }

//                            Log.e("JDEMAIL", obj.getString("email"));
//                            JSONArray sync_data = obj.getJSONArray("email");
//                            syncingResult.success = saveData(sync_data);
                            saveAccount(obj);
                        }
                        catch (Exception e){
                            Log.e(TAG, e.getMessage());
                            Log.e(TAG, syncingResult.message);
                            syncingResult.message = e.getMessage();
                            syncingResult.success = false;
                        }
                        listener.onFinish(syncingResult);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        syncingResult.success = false;
                        syncingResult.message = getSpecificErrorMessage(error);
                        listener.onFinish(syncingResult);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError { return params; }

        };

        stringRequest.setRetryPolicy(defaultRetryPolicy);
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

//    public abstract void get(String email, final Result.Listener listener);

    public abstract void get(String email, String pass, Result.Listener listener);

    public abstract void sync(final Result.Listener listener);

    public BroadcastReceiver autoSync(final Result.Listener listener)
    {
        return new BroadcastReceiver()
        {
            CountDownTimer syncInterval;

            @Override
            public void onReceive(Context context, Intent intent)
            {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                //timer with 10 sec interval
                syncInterval =  new CountDownTimer(10000, 1000)
                {
                    public void onTick(long millisUntilFinished) { }

                    public void onFinish()
                    {
                        //when timer ends, set to null
                        syncInterval = null;
                    }
                };

                syncInterval.start();

                //add timer for sync interval
                if (activeNetwork != null)
                {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        sync(listener);
                    }
                }
                else {
                    syncInterval = null;
                }
            }
        };
    }

    abstract String prepareData();


    boolean saveData(JSONArray sync_data){

        return false;
    };
    boolean saveAccount(JSONObject sync_data) throws JSONException {
//        try {
            DatabaseHelper myDb = new DatabaseHelper(context);

            if (sync_data.length() != 0) {

//                SQLiteDatabase db = myDb.getWritableDatabase();
//                db.beginTransaction();
                myDb.insertAccount(sync_data.getString("email"),sync_data.getString("password"));

            }
//        }
//        catch (JSONException e) {
//            e.printStackTrace();
//            return false;
//            Log.e(TAG, "exception", e);
//        }
//        finally {
//            return success;
//        }

        return false;
    };

    protected List<String> prepareAttachments(){
        //return empty attachments list as default, override this in subclasses
        return new ArrayList<>();
    }

    protected void syncAttachments(List<String> image_map){
        int i = 0;

        while (i < image_map.size()) {
            String image_string = image_map.get(i);
//            Log.e(TAG,image_string);
            String[] separated_image_string = image_string.split("\\:");
            String folder = separated_image_string[0];
            String image;

            try {
                image = separated_image_string[1];
            } catch (Exception e) {
                image = "";
            }

            if (!formatData(image).equals("")) {
                try {
                    UploadImage(folder, image);
                } catch (Exception e) {
                    //image = "";
                }
            }
            i++;
        }
    }

    protected void UploadImage(String folder, String image_name) {

        String uploader = SYNC_CONFIG_BASE_URL + "dashboard/android/uploader.php";

        if (!TextUtils.isEmpty(image_name)) {

            if (!image_name.matches("")) {

                final File directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                final StringBuilder data_image = new StringBuilder("");
                String imgpath = directory + "/" + image_name;
                String fileName = imgpath;
                final File findfile = new File(imgpath);
                String imgPath = directory + "/" + findfile.getName();
                fileName = findfile.getName();

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(new File(imgPath)));
                } catch (IOException e) {
                    e.printStackTrace();
//                    Log.e(TAG, e.getMessage());
                }

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] image = stream.toByteArray();
                String ConvertImage = Base64.encode(image);
                data_image.append(folder + "**cut_here**" + fileName.toString() + "**cut_here**" + ConvertImage + "**new_line**");



                Map<String, String> params = new HashMap<>();
                params.put("image_data", data_image.toString());

                request(uploader, params, new Result.Listener() {
                    @Override
                    public void onFinish(Result result) {

                    }
                });
            }
        }
    }

    protected String getBasisDate(Boolean DBTableHasData){
        //if db has data for this table, dont sync all
        if(DBTableHasData){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.MONTH, -2);
            Date d = c.getTime();
            return format.format(d);
        }
        else return "all";
    }

    //separate table deletion per module?
//    protected void basisDateDeletion(String basisDate){
//        DatabaseHelper myDb = new DatabaseHelper(context);
//        if (basisDate.equals("all")) {
//            myDb.Delete_synced_Data();
//        } else {
//            myDb.delete_based_on_date(basisDate);
//        }
//    }

    protected String getSpecificErrorMessage(VolleyError error){
        if (error instanceof NoConnectionError) {

            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = null;
            if (cm != null) {
                activeNetwork = cm.getActiveNetworkInfo();
            }
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                return "Server is not connected to internet.";
            } else {
                return "Your device is not connected to internet.";
            }
//                } else if (error instanceof NetworkError || error.getCause() instanceof ConnectException
//                        || (error.getCause().getMessage() != null
//                        && error.getCause().getMessage().contains("connection"))) {
//                    specific_error = "Your device is not connected to internet.";
        } else if (error.getCause() instanceof MalformedURLException) {
            return"";
        } else if (error instanceof ParseError || error.getCause() instanceof IllegalStateException
                || error.getCause() instanceof JSONException
                || error.getCause() instanceof XmlPullParserException) {
            return "Parse Error (because of invalid json or xml).";
        } else if (error.getCause() instanceof OutOfMemoryError) {
            return "Out Of Memory Error.";
        } else if (error instanceof AuthFailureError) {
            return "server couldn't find the authenticated request.";
        } else if (error instanceof ServerError || error.getCause() instanceof ServerError) {
            return "Server is not responding.";
        } else if (error instanceof TimeoutError || error.getCause() instanceof SocketTimeoutException
                || error.getCause() instanceof ConnectTimeoutException
                || error.getCause() instanceof SocketException
                || (error.getCause().getMessage() != null
                && error.getCause().getMessage().contains("Connection timed out"))) {
            return "Connection timeout error";
        } else {
            return "An unknown error occurred.";
        }
    }

    protected String formatData(String data) {
        if (TextUtils.isEmpty(data)) {
            data = "";
        } else {
            data = data.replace("null", "");
            data = data.replace("\r", "");
        }
        return data;
    }


    public static class Result {
        public boolean success;
        public String message;

        public interface Listener {
            void onFinish(Result result);
        }
    }
}
