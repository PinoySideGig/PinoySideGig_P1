package com.pinoy.side_gig;

//import static com.pinoy.side_gig.MainActivity.CAMERA_REQUEST_CODE;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

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
import com.pinoy.side_gig.data.database.VolleySingleton;
import com.pinoy.side_gig.databinding.ActivityRegisterBinding;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityRegisterBinding binding;

    Calendar calendar;

    String url;
    String email,password,lastname,firstname,gender,age,phone,bday,location,skills,experience,valid_id,file_1,display_photo;
    String re_password;
    String userType;
    StringRequest stringRequest;
    JSONObject obj;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int PICK_FILE_REQUEST = 1;
    String selectedAttachment, s_fileExtension;
    ImageView img1,imgId,imgResume;
    String imageFileName;
    String s_id_pic,s_display_photo,s_resume;
    Bitmap bitmap_display_photo,bitmap_id,bitmap_resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         url = this.getResources().getString(R.string.sync_config_base_url) + "sync_register.php";
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        calendar = Calendar.getInstance();

//        setSupportActionBar(binding.toolbar);

        Bundle bundle = getIntent().getExtras();
        userType = bundle.getString("user_type");
        if(userType.matches("Client")){
            binding.skills.setVisibility(View.GONE);
            binding.experience.setVisibility(View.GONE);
        }

        binding.title.setText(userType + " Registration Form");
        binding.password.setTransformationMethod(new PasswordTransformationMethod());

        binding.bday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                // Show DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                // Format and set the selected date in the EditText
                                binding.bday.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                                int age = computeAge(selectedYear, selectedMonth, selectedDay);
                                binding.age.setText("" + age);
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }
        });

        binding.buttonSelectPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker("resume");
            }
        });
        binding.buttonSelectID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker("id_pic");
            }
        });
        binding.selectedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker("display_photo");
            }
        });

        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = binding.email.getText().toString();
                password = binding.password.getText().toString();
                re_password = binding.confirmPassword.getText().toString();
                lastname = binding.lastName.getText().toString();
                firstname = binding.firstName.getText().toString();
                gender = binding.genderSpinner.getSelectedItem().toString();
                age = binding.age.getText().toString();
                phone = binding.phone.getText().toString();
                bday = binding.bday.getText().toString();
                location = binding.location.getText().toString();
                skills = binding.skills.getText().toString();
                experience = binding.experience.getText().toString();
                s_display_photo = binding.displayPhotoTextView.getText().toString();
                s_id_pic = binding.textViewFilePathID.getText().toString();
                s_resume = binding.textViewFilePathResume.getText().toString();


                if(!isValidEmail(email)){
                    Toast.makeText(RegisterActivity.this, "Invalid Email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!password.matches(re_password)){
                    Toast.makeText(RegisterActivity.this, "Password not matched!", Toast.LENGTH_SHORT).show();
                    return;
                }


                img1 =  binding.selectedImageView;
                BitmapDrawable draw = (BitmapDrawable) img1.getDrawable();
                bitmap_display_photo = draw.getBitmap();

                imgId =  binding.imageViewFilePathID;
                BitmapDrawable draw_id = (BitmapDrawable) imgId.getDrawable();
                bitmap_id = draw_id.getBitmap();

                imgResume =  binding.imageViewFilePathResume;
                BitmapDrawable draw_resume = (BitmapDrawable) imgResume.getDrawable();
                bitmap_resume = draw_resume.getBitmap();

                passdata(email,password,lastname,firstname,gender,age,phone,bday,location,skills,experience,userType,s_display_photo,s_id_pic,s_resume);



            }
        });

        Spinner genderSpinner = binding.genderSpinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genderSpinner.setAdapter(adapter);



//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_register);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


    }
    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    public void passdata(String email,String password,String lastname,String firstname,String gender,String age,String phone,String bday,String location,String skills,String experience,String user_type,String display_photo,String id_pic,String resume) {
        /* Get all the data needed to pass*/


    stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onResponse(String response) {


            String incase_error = response;
            try {
                obj = new JSONObject(response);
                if(obj.getString("status")=="1"){
                    Toast.makeText(RegisterActivity.this, "Email already exists!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(img1.getDrawable() != null){
                    Log.e("DP ko", "onClick: display photo" );
                    saveImageToGallery(bitmap_display_photo);
                }
                if(imgId.getDrawable() != null){
                    Log.e("DP ko", "onClick: display photo" );
                    saveImageToGallery(bitmap_id);
                }
                if(imgResume.getDrawable() != null){
                    Log.e("DP ko", "onClick: display photo" );
                    saveImageToGallery(bitmap_resume);
                }



                GetDataTask_in GetDataTask_in = new GetDataTask_in();
                GetDataTask_in.execute();
                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);

            } catch (JSONException e) {
                e.printStackTrace();

//                if (incase_error.toString().contains("too many connections")) {
//                    Intent disc_view = new Intent(RegisterActivity.this, disconnect.class);
//                    startActivity(disc_view);
//                } else if (incase_error.toString().toLowerCase().contains("incorrect string value")) {
//                    response = "Please do not use any smileys, emoticons, or emojis in inputting ANY DATA in our SFA, as it currently does not support such characters. The use of any smileys, emoticons, or emojis will cause errors in your account.";
//                    Intent error_view = new Intent(data_syncing_v3.this, sync_error.class);
//                    error_view.putExtra("error", response);
//                    startActivity(error_view);
//                } else {
//                    Intent error_view = new Intent(data_syncing_v3.this, sync_error.class);
//                    error_view.putExtra("error", response);
//                    startActivity(error_view);
//                }
            }
        }
    }, new Response.ErrorListener() {
        @Override
//            public void onErrorResponse(VolleyError error) {
//                /* TODO : In case there is an error */
//                Log.e("MYAPP", "exception", error);
//                Toast.makeText(mContext, "" + error, Toast.LENGTH_LONG).show();
//                back_holder.setVisibility(View.VISIBLE);
//            }
        public void onErrorResponse(VolleyError error) {
            String specific_error = "";
            if (error instanceof NoConnectionError) {
                ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = null;
                if (cm != null) {
                    activeNetwork = cm.getActiveNetworkInfo();
                }
                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                    specific_error = "Server is not connected to internet.";
                } else {
                    specific_error = "Your device is not connected to internet.";
                }
//                } else if (error instanceof NetworkError || error.getCause() instanceof ConnectException
//                        || (error.getCause().getMessage() != null
//                        && error.getCause().getMessage().contains("connection"))) {
//                    specific_error = "Your device is not connected to internet.";
            } else if (error.getCause() instanceof MalformedURLException) {
                specific_error = "";
            } else if (error instanceof ParseError || error.getCause() instanceof IllegalStateException
                    || error.getCause() instanceof JSONException
                    || error.getCause() instanceof XmlPullParserException) {
                specific_error = "Parse Error (because of invalid json or xml).";
            } else if (error.getCause() instanceof OutOfMemoryError) {
                specific_error = "Out Of Memory Error.";
            } else if (error instanceof AuthFailureError) {
                specific_error = "server couldn't find the authenticated request.";
            } else if (error instanceof ServerError || error.getCause() instanceof ServerError) {
                specific_error = "Server is not responding.";
            } else if (error instanceof TimeoutError || error.getCause() instanceof SocketTimeoutException
                    || error.getCause() instanceof ConnectTimeoutException
                    || error.getCause() instanceof SocketException
                    || (error.getCause().getMessage() != null
                    && error.getCause().getMessage().contains("Connection timed out"))) {
                specific_error = "Connection timeout error";
            } else {
                specific_error = "An unknown error occurred.";
            }

//            Intent error_view = new Intent(data_syncing_v3.this, sync_error.class);
//            error_view.putExtra("error", specific_error);
//            startActivity(error_view);
        }
        public void onPostExecute(String result) {
            // this is executed on the main thread after the process is over
            // update your UI here


        }
    }) {
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            params.put("email", email);
            params.put("password", password);
            params.put("lastname", lastname);
            params.put("firstname", firstname);
            params.put("gender", gender);
            params.put("age", age);
            params.put("phone", phone);
            params.put("bday", bday);
            params.put("location", location);
            params.put("skills", skills);
            params.put("experience", experience);
            params.put("user_type", user_type);
            params.put("display_photo", display_photo);
            params.put("id_pic", id_pic);
            params.put("resume", resume);



            /* new parameters to pass */
//            params.put("insert_fctexp_new", exp_array.toString());
//            params.put("insert_fctvisit_new", vis_array.toString());



            return params;
        }
    };
    //VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

}
    class GetDataTask_in extends AsyncTask<Void, Integer, Void> {

        public GetDataTask_in() {
            super();

        }

        protected Void doInBackground(Void... params) {

            //String current = "";
            return null;
        }


    }

    private int computeAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        dob.set(year, month, day);

        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }

    private void openFilePicker(String isSelected) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Set the type of file you want to allow (e.g. "*/*" for all files, "image/*" for images only)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        selectedAttachment = isSelected;
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData(); // Get the Uri of the selected file
            String fileName = getFileName(fileUri); // Get file name from Uri (optional)
            s_fileExtension = getFileExtension(fileUri);

            imageFileName = selectedAttachment + "_" + System.currentTimeMillis() + "." + s_fileExtension;

//            binding.fileNameTextView.setText(fileName); // Display file name
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
                if(selectedAttachment=="display_photo"){
                    binding.displayPhotoTextView.setText(imageFileName);
                    binding.selectedImageView.setVisibility(View.VISIBLE);
                    binding.selectedImageView.setImageBitmap(bitmap);
                    binding.selectedImageView.setAdjustViewBounds(true);
                    binding.selectedImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
                if(selectedAttachment=="id_pic"){
                    binding.textViewFilePathID.setText(imageFileName);
                    binding.imageViewFilePathID.setVisibility(View.VISIBLE);
                    binding.imageViewFilePathID.setImageBitmap(bitmap);
                    binding.imageViewFilePathID.setAdjustViewBounds(true);
                    binding.imageViewFilePathID.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
                if(selectedAttachment=="resume"){
                    binding.textViewFilePathResume.setText(imageFileName);
                    binding.imageViewFilePathResume.setVisibility(View.VISIBLE);
                    binding.imageViewFilePathResume.setImageBitmap(bitmap);
                    binding.imageViewFilePathResume.setAdjustViewBounds(true);
                    binding.imageViewFilePathResume.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to get the file name from the Uri
    private String getFileName(Uri uri) {
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            return path.substring(cut + 1);
        }
        return path;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_register);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public String getFileExtension(Uri uri) {
        String extension = null;

        // Get the MIME type of the file from its URI
        ContentResolver contentResolver = getContentResolver();
        String mimeType = contentResolver.getType(uri);

        // If MIME type is available, get the extension from MimeTypeMap
        if (mimeType != null) {
            extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        } else {
            // Fallback: Try to get extension from the URI's file path
            extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        }

        return extension;
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
        } else {
            // Permission already granted
//            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with accessing the camera
//                openCamera();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImageToGallery(Bitmap bitmap) {
        String savedImagePath = null;

//        Environment env = new Environment();
//
//        if(selectedAttachment.matches("resume")){
//           env = env.DIRECTORY_PICTURESl;
//        }
//        else{
//
//        }

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/PinoySideGig");


        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        Log.e("DP ko", "onClick: display photo" + imageFileName);
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            Log.e("DP ko", "onClick: display photo" + imageFileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();

                // Add the image to the gallery
                galleryAddPic(savedImagePath);
                Toast.makeText(this, "Image Saved to Gallery", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to Save Image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Notify the gallery to scan the new image so that it appears in the gallery
    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(imagePath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

//    private void saveImg() throws IOException {
//        BitmapDrawable draw = (BitmapDrawable) iv.getDrawable();
//        Bitmap bitmap = draw.getBitmap();
//
//        FileOutputStream outStream = null;
//        File sdCard = Environment.getExternalStorageDirectory();
//        File dir = new File(sdCard.getAbsolutePath() + "/YourFolderName");
//        dir.mkdirs();
//        String fileName = String.format("%d.jpg", System.currentTimeMillis());
//        File outFile = new File(dir, fileName);
//        outStream = new FileOutputStream(outFile);
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
//        outStream.flush();
//        outStream.close();
//
//    }
}