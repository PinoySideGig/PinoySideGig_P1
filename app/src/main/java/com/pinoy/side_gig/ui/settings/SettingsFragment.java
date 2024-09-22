package com.pinoy.side_gig.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pinoy.side_gig.MainActivity;
import com.pinoy.side_gig.data.database.DatabaseHelper;
import com.pinoy.side_gig.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {
    DatabaseHelper myDb;
    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSettings;
        settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        myDb = new DatabaseHelper(getContext());
        myDb.Logout();
//        if(res){
        Intent i = new Intent(getContext(), MainActivity.class);
        startActivity(i);
//        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}