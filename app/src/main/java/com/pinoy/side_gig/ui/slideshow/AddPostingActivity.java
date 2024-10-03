package com.pinoy.side_gig.ui.slideshow;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.pinoy.side_gig.LoginActivity;
import com.pinoy.side_gig.Logout;
import com.pinoy.side_gig.R;
import com.pinoy.side_gig.data.database.DatabaseHelper;
import com.pinoy.side_gig.databinding.ActivityAddPostingBinding;
import com.pinoy.side_gig.databinding.ActivityRegisterBinding;
import com.pinoy.side_gig.databinding.FragmentSlideshowBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddPostingActivity extends AppCompatActivity {

    private EditText titleInput, dateInput, remarksInput;
    private Button submitButton;
    private ActivityAddPostingBinding binding;
    DatabaseHelper myDb;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityAddPostingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        myDb = new DatabaseHelper(this);
        email="";
        Cursor get_email = myDb.getAccount();
        while (get_email.moveToNext()) {
            email = get_email.getString(0);
        }

        // Initialize the input fields
        titleInput = binding.titleInput;
        dateInput = binding.dateInput;
        remarksInput = binding.remarksInput;
        submitButton = binding.submitButton;

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        String title = titleInput.getText().toString();
                        String date = dateInput.getText().toString();
                        String remarks = remarksInput.getText().toString();


                        // Create JSON object to send
                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("email", email);
                            postData.put("title", title);
                            postData.put("date", date);
                            postData.put("remarks", remarks);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Send data to PHP API
                        new SendPostRequest().execute(postData);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        // Set an onClickListener on the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddPostingActivity.this);

                builder.setMessage("Post this job? Workers will see this posting.").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("NO", dialogClickListener);
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
    }

    // AsyncTask to send POST request
    public class SendPostRequest extends AsyncTask<JSONObject, Void, String> {

        @Override
        protected String doInBackground(JSONObject... params) {
            try {
                URL url = new URL(getResources().getString(R.string.sync_config_base_url) + "sync_posting.php"); // Your PHP API URL
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                // Write data to request body
                OutputStream os = conn.getOutputStream();
                os.write(params[0].toString().getBytes("UTF-8"));
                os.close();

                // Check response code
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Return the JSON response from the server
                    return response.toString();
                } else {
                    return "Failed to save data!";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Display the result of the POST request
            try {
                JSONObject responseJson = new JSONObject(result);
                String status = responseJson.getString("status");
                String message = responseJson.getString("message");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "Success: " + message, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), SlideshowFragment.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_LONG).show();
                }
//                Log.e("JDX", "onPostExecute: " + result);
//                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "JSON Parsing Error", Toast.LENGTH_LONG).show();
            }

        }
    }
}