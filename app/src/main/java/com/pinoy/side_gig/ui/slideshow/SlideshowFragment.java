package com.pinoy.side_gig.ui.slideshow;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pinoy.side_gig.R;
import com.pinoy.side_gig.databinding.FragmentSlideshowBinding;
import com.pinoy.side_gig.databinding.ItemTransformBinding;
import com.pinoy.side_gig.databinding.MyPostingDataBinding;
import com.pinoy.side_gig.ui.services.ServicesFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private RecyclerView recyclerView;
    private List<SlideshowViewModel> dataList;
    private SlideshowAdapter dataAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        SlideshowViewModel slideshowViewModel =
//                new ViewModelProvider(this).get(SlideshowViewModel.class);
//        ListAdapter<String, SlideshowFragment.SlideshowViewHolder> adapter = new SlideshowFragment.SlideshowAdapter();
        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = root.findViewById(R.id.recyclerViewPostings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize data list
        dataList = new ArrayList<>();

        // Fetch data from the API
        new FetchDataTask().execute();



        FloatingActionButton fab = binding.addPosting;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Action to be performed when FAB is clicked
                Intent i = new Intent(getActivity(), AddPostingActivity.class);
                startActivity(i);
            }
        });

//        final TextView textView = binding.textSlideshow;
//        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }


    private class FetchDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Your PHP API URL
                URL url = new URL(getResources().getString(R.string.sync_config_base_url) + "get_my_postings.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    return response.toString();
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    // Parse JSON response
                    JSONArray jsonArray = new JSONArray(result);
                    Log.e("XX", "onPostExecute: " + jsonArray );
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String title = jsonObject.getString("type_of_job");
                        String date = jsonObject.getString("description");
                        String remarks = jsonObject.getString("created_at");

                        // Add data to list
                        dataList.add(new SlideshowViewModel(title, date, remarks));
                    }

                    // Set adapter to RecyclerView
                    dataAdapter = new SlideshowAdapter(dataList);
                    recyclerView.setAdapter(dataAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error parsing JSON", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "Failed to fetch data", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private static class SlideshowAdapter extends ListAdapter<String, SlideshowFragment.SlideshowViewHolder> {
        private List<SlideshowViewModel> dataList;

        protected SlideshowAdapter(List<SlideshowViewModel> dataList) {
            super(new DiffUtil.ItemCallback<String>() {
                @Override
                public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                    return oldItem.equals(newItem);
                }
            });
        }

        @NonNull
        @Override
        public SlideshowFragment.SlideshowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MyPostingDataBinding binding = MyPostingDataBinding.inflate(LayoutInflater.from(parent.getContext()));
            return new SlideshowFragment.SlideshowViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull SlideshowFragment.SlideshowViewHolder holder, int position) {
            holder.title.setText(getItem(position));
            holder.description.setText(getItem(position));
        }
    }
    private static class SlideshowViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView description;

        public SlideshowViewHolder(MyPostingDataBinding binding) {
            super(binding.getRoot());
            title = binding.titleText;
            description = binding.remarksText;
        }
    }
}