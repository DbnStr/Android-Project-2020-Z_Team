package ru.mail.z_team.user;

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
import ru.mail.z_team.icon_fragments.friends.FriendsViewModel;

public class UserFragment extends Fragment {

    public User user;

    private FriendsViewModel viewModel;

    private TextView userName, userAge;
    private ImageView userImage;
    private LinearLayout ageLayout;

    private static final String LOG_TAG = "UserFragment";
    private final Logger logger = new Logger(LOG_TAG, true);

    public UserFragment() {

    }

    public UserFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(getActivity()).get(FriendsViewModel.class);

        View view = inflater.inflate(R.layout.fragment_user, container, false);

        userName = view.findViewById(R.id.user_name);
        userAge = view.findViewById(R.id.user_age);
        userImage = view.findViewById(R.id.user_image);
        ageLayout = view.findViewById(R.id.linear_user_age);

        if (savedInstanceState != null) {
            viewModel.getUserProfileData().observe(getActivity(), u -> {
                user = u;
                showProfile();
            });
        } else {
            showProfile();
        }
        return view;
    }

    private void showProfile() {
        if (!user.getName().isEmpty()) {
            userName.setText(user.getName());
        }

        if (user.getAge() != 0) {
            userAge.setText(String.valueOf(user.getAge()));
        } else {
            ageLayout.setVisibility(View.INVISIBLE);
        }

        if (user.getImageUrl() != null
                && !user.getImageUrl().isEmpty()
                && !user.getImageUrl().equals("no Image")) {
            StorageReference reference = FirebaseStorage.getInstance()
                    .getReferenceFromUrl(getString(R.string.base_storage_url) + user.getImageUrl());
            reference.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get()
                    .load(uri)
                    .resize(userImage.getHeight(), userImage.getWidth())
                    .centerCrop()
                    .placeholder(ContextCompat.getDrawable(getActivity(), R.drawable.ic_baseline_photo_24))
                    .into(userImage)).addOnFailureListener(e -> logger.errorLog(e.getMessage()));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (viewModel != null) {
            viewModel.setUser(user);
        }
    }
}