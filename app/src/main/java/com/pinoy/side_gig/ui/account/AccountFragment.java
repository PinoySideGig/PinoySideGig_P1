package com.pinoy.side_gig.ui.account;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pinoy.side_gig.Logout;
import com.pinoy.side_gig.UserTypeActivity;
import com.pinoy.side_gig.data.database.DatabaseHelper;
import com.pinoy.side_gig.databinding.FragmentAccountBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    Button btnLogout;
    DatabaseHelper myDb;
    String email, firstname, lastname, phone,user_type,skill,display_photo,id_pic,resume;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AccountViewModel accountViewModel =
                new ViewModelProvider(this).get(AccountViewModel.class);
        myDb = new DatabaseHelper(getContext());
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Cursor get_email = myDb.getAccount();
        while (get_email.moveToNext()) {
            email = get_email.getString(0);
            firstname = get_email.getString(3);
            lastname = get_email.getString(2);
            phone = get_email.getString(6);
            user_type = get_email.getString(14);
            skill = get_email.getString(9);
            display_photo = get_email.getString(13);
            id_pic = get_email.getString(11);
            resume = get_email.getString(12);

        }
//        Toast.makeText(getContext(), phone, Toast.LENGTH_SHORT).show();

        binding.emailTextView.setText(email);
        binding.usernameTextView.setText(toTitleCase(firstname + " " + lastname));
        binding.phoneTextView.setText(phone);
        binding.skills.setText(toTitleCase(skill));
        btnLogout = binding.logoutButton;

        if(!display_photo.matches("")){
            Bitmap myBitmapDisplayPhoto = getBitmapImg(display_photo);
            binding.profileImageView.setImageBitmap(myBitmapDisplayPhoto);
            binding.profileImageView.setAdjustViewBounds(true);
            binding.profileImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        if(!id_pic.matches("")){
            Bitmap myBitmapDisplayPhoto = getBitmapImg(id_pic);
            binding.idImageView.setImageBitmap(myBitmapDisplayPhoto);
            binding.idImageView.setAdjustViewBounds(true);
            binding.idImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            binding.idImageView.setVisibility(View.VISIBLE);
            binding.idTextview.setVisibility(View.VISIBLE);
        }

        if(!resume.matches("")){
            Bitmap myBitmapDisplayPhoto = getBitmapImg(resume);
            binding.resumeImageView.setImageBitmap(myBitmapDisplayPhoto);
            binding.resumeImageView.setAdjustViewBounds(true);
            binding.resumeImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            binding.resumeImageView.setVisibility(View.VISIBLE);
            binding.resumeTextview.setVisibility(View.VISIBLE);
        }



        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent i = new Intent(getContext(), Logout.class);
                        startActivity(i);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setMessage("Log out of your account?").setPositiveButton("LOGOUT", dialogClickListener)
                        .setNegativeButton("NO", dialogClickListener);
                AlertDialog alert = builder.create();
                alert.show();

                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.RED);

            }
        });


//        final TextView textView = binding.textReflow;
//        accountViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public static String toTitleCase(String string) {

        // Check if String is null
        if (string == null) {

            return null;
        }

        boolean whiteSpace = true;

        StringBuilder builder = new StringBuilder(string); // String builder to store string
        final int builderLength = builder.length();

        // Loop through builder
        for (int i = 0; i < builderLength; ++i) {

            char c = builder.charAt(i); // Get character at builders position

            if (whiteSpace) {

                // Check if character is not white space
                if (!Character.isWhitespace(c)) {

                    // Convert to title case and leave whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    whiteSpace = false;
                }
            } else if (Character.isWhitespace(c)) {

                whiteSpace = true; // Set character is white space

            } else {

                builder.setCharAt(i, Character.toLowerCase(c)); // Set character to lowercase
            }
        }

        return builder.toString(); // Return builders text
    }

    public static Bitmap getBitmapImg(String str_img) {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/PinoySideGig/" + str_img);
        Bitmap myBitmap = BitmapFactory.decodeFile(storageDir.getAbsolutePath());
        return myBitmap;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}