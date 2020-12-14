package ru.mail.z_team.icon_fragments.walks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import ru.mail.z_team.R;
import ru.mail.z_team.map.Story;

public class StoryFragment extends Fragment {

    private final Story story;
    private TextView place, description;

    public StoryFragment(Story story) {
        this.story = story;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);

        place = view.findViewById(R.id.displayed_story_place);
        description = view.findViewById(R.id.displayed_story_description);

        place.setText(story.getPlace());
        description.setText(story.getDescription());

        return view;
    }
}