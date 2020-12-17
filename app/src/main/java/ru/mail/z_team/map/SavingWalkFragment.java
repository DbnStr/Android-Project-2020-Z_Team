package ru.mail.z_team.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mapbox.geojson.FeatureCollection;

import java.util.ArrayList;

import ru.mail.z_team.R;
import ru.mail.z_team.icon_fragments.go_out.GoOutViewModel;

public class SavingWalkFragment extends Fragment {

    private static final String LOG_TAG = "SavingWalkFragment";
    Button addWalk;
    EditText walkTitle;
    String title;
    GoOutViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saving_walk, container, false);

        addWalk = view.findViewById(R.id.add_walk_btn);
        walkTitle = view.findViewById(R.id.walk_title_et);

        if (savedInstanceState != null) {
            title = savedInstanceState.getString("title");
        }

        addWalk.setOnClickListener(v -> {
            title = walkTitle.getText().toString().trim();
            if (title.equals("")) {
                walkTitle.setError("Title can't be empty");
                walkTitle.setFocusable(true);
            } else {
                postWalk();
            }
        });

        viewModel = new GoOutViewModel(getActivity().getApplication());
        return view;
    }

    private void postWalk() {
        Log.d(LOG_TAG, "postWalk");
        FeatureCollection walk = ((MapActivity) getActivity()).getWalkGeoJSON();
        ArrayList<Story> stories = ((MapActivity) getActivity()).getStories();
        viewModel.postWalk(title, walk, stories);
        viewModel.getPostStatus().observe(getActivity(), s -> {
            if (s == getString(R.string.SUCCESS)) {
                Toast.makeText(getActivity(), "Posted successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Failed post the walk", Toast.LENGTH_LONG).show();
            }
            getActivity().finish();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", title);
    }
}