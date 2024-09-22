package com.pinoy.side_gig;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pinoy.side_gig.data.database.DatabaseHelper;
import com.pinoy.side_gig.databinding.FragmentSettingsBinding;
import com.pinoy.side_gig.ui.settings.SettingsViewModel;

public class Logout extends AppCompatActivity {
    DatabaseHelper myDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DatabaseHelper(this);
        myDb.Logout();
//        if(res){
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
//        }
    }

}
