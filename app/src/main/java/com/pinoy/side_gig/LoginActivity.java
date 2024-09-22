package com.pinoy.side_gig;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pinoy.side_gig.data.database.DatabaseHelper;
import com.pinoy.side_gig.data.network.BaseSync;
import com.pinoy.side_gig.data.network.Sync;
import com.pinoy.side_gig.databinding.ActivityLoginBinding;
import com.pinoy.side_gig.databinding.ActivityRegisterBinding;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    TextView register;
    private ActivityLoginBinding binding;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        myDb = new DatabaseHelper(this);

        // Initialize views
        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        register = findViewById(R.id.register);

        // Set up the login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get user input

                String username = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    etEmail.setError("Username is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password is required");
                    return;
                }
                String acc = username +"/"+password;
                Sync res = new Sync(LoginActivity.this);
                res.get(username,password, new BaseSync.Result.Listener() {
                    @Override
                    public void onFinish(BaseSync.Result result) {
                        if(result.success){
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                        }


//                        curr_category = "";
//                        fromDate = "";
//                        toDate = "";
//                        if (s_so.matches("")) {
//
//                            readAllData(curr_category, "initial", fromDate, toDate);
//                        } else {
//                            f_search_so(s_so, curr_category, fromDate, toDate);
//                        }
//                        swipeContainer.setRefreshing(false);
                    }
                });
                // Check if username and password are not empty


                // Simple authentication logic (hardcoded credentials)
//                if (username.equals("admin") && password.equals("12345")) {
//                    // Show success and redirect to another activity
//                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
//                     Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                     startActivity(intent);
//                     finish();
//                } else {
//                    // Show error message
//                    Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),UserTypeActivity.class);
                startActivity(i);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        String email = "";
        myDb = new DatabaseHelper(this);
        Cursor get_email = myDb.getAccount();
        while (get_email.moveToNext()) {
            email = get_email.getString(0);
        }


        if(email!=""){
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("CDA", "onBackPressed Called");
        this.finishAffinity();
//        Intent setIntent = new Intent(Intent.ACTION_MAIN);
//        setIntent.addCategory(Intent.CATEGORY_HOME);
//        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(setIntent);
    }
}
