package ru.mail.z_team.icon_fragments.walks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;
import ru.mail.z_team.map.Story;

public class StoryFragment extends Fragment {

    private static final String LOG_TAG = "StoryFragment";

    private Story story;
    private TextView place, description;
    private LinearLayout gallery;

    private WalkProfileViewModel viewModel;

    private Logger logger;

    private static final String BASE_URL = "gs://android-project-2020-zteam.appspot.com";

    public StoryFragment() {
    }

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

        viewModel = new ViewModelProvider(this).get(WalkProfileViewModel.class);

        if (savedInstanceState != null) {
            viewModel.getStory().observe(getActivity(), s -> {
                story = s;
                showStory();
            });
        } else {
            showStory();
        }

        return view;
    }

    private void showStory() {
        place.setText(story.getPlace());
        description.setText(story.getDescription());
        if (story.getUrlImages() != null) {
            for (String url : story.getUrlImages()) {
                ImageView imageView = new ImageView(getActivity());
                gallery.addView(imageView);
                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(BASE_URL + url);
                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                    Picasso.get()
                            .load(uri)
                            .placeholder(ContextCompat.getDrawable(getActivity(), R.drawable.ic_baseline_photo_24))
                            .into(imageView);
                }).addOnFailureListener(e -> {
                    logger.errorLog(e.getMessage());
                });
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        viewModel.setStory(story);
    }
}