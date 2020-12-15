package ru.mail.z_team.icon_fragments.walks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;
import ru.mail.z_team.map.Story;

public class StoryFragment extends Fragment {

    private static final String LOG_TAG = "StoryFragment";

    private final Story story;
    private TextView place, description;
    private LinearLayout gallery;

    private Logger logger;

    private static final String BASE_URL = "gs://android-project-2020-zteam.appspot.com";

    public StoryFragment(Story story) {
        this.story = story;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);

        logger = new Logger(LOG_TAG, true);
        place = view.findViewById(R.id.displayed_story_place);
        description = view.findViewById(R.id.displayed_story_description);
        gallery = view.findViewById(R.id.displayed_story_gallery);

        place.setText(story.getPlace());
        description.setText(story.getDescription());
        if (story.getUrlImages() != null) {
            for (String url : story.getUrlImages()) {
                ImageView imageView = new ImageView(getActivity());
                gallery.addView(imageView);
                Glide.with(getActivity())
                        .load(BASE_URL + url)
                        .placeholder(R.drawable.ic_baseline_image_24)
                        .into(imageView);
            }
        }

        return view;
    }
}