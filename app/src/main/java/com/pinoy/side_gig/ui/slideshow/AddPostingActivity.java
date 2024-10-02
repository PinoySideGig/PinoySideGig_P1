package com.pinoy.side_gig.ui.slideshow;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.pinoy.side_gig.R;
import com.pinoy.side_gig.databinding.ActivityAddPostingBinding;
import com.pinoy.side_gig.databinding.ActivityRegisterBinding;
import com.pinoy.side_gig.databinding.FragmentSlideshowBinding;

public class AddPostingActivity extends AppCompatActivity {

    private EditText titleInput, dateInput, remarksInput;
    private Button submitButton;
    private ActivityAddPostingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityAddPostingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the input fields
        titleInput = binding.titleInput;
        dateInput = binding.dateInput;
        remarksInput = binding.remarksInput;
        submitButton = binding.submitButton;

        // Set an onClickListener on the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Capture the input from the fields
                String title = titleInput.getText().toString();
                String date = dateInput.getText().toString();
                String remarks = remarksInput.getText().toString();

                // Show a simple message with the collected data (you can replace this with actual logic)
                Toast.makeText(getApplicationContext(), "Title: " + title + "\nDate: " + date + "\nRemarks: " + remarks, Toast.LENGTH_LONG).show();
            }
        });
    }
}