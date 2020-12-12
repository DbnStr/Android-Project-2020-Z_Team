package ru.mail.z_team.icon_fragments.go_out;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.mapbox.geojson.Point;

import ru.mail.z_team.MapActivity;
import ru.mail.z_team.R;

public class StoryFragment extends Fragment {


    public StoryFragment(Point storyPoint) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);

        return  view;
    }

    @Override
    public void onStop() {
        super.onStop();
        ((MapActivity) getActivity()).backToActivity();
    }
}