package com.pinoy.side_gig;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.pinoy.side_gig.databinding.ActivityRegisterBinding;
import com.pinoy.side_gig.databinding.ActivityUserTypeBinding;

import java.util.Calendar;

public class UserTypeActivity extends AppCompatActivity {
    private ActivityUserTypeBinding binding;
    private RadioButton radioButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rdUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int clientSelectedId =  binding.clientRadioButton.getId();
                int workerSelectedId =  binding.workerRadioButton.getId();
                if(checkedId == clientSelectedId){
                    Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("user_type", "Client");
                    i.putExtras(bundle);
                    startActivity(i);

                } else if (checkedId == workerSelectedId) {
                    Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("user_type", "Worker");
                    i.putExtras(bundle);
                    startActivity(i);
                }

            }
        });

    }
}
