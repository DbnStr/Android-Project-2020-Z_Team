package ru.mail.z_team.icon_fragments.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import ru.mail.z_team.R;

public class ProfileFragment extends Fragment {

    private static final String LOG_TAG = "ProfileFragment";

    private ProfileViewModel profileViewModel;
    private TextView name;
    private TextView age;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        log("OnCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = getActivity().findViewById(R.id.profile_name);
        age = getActivity().findViewById(R.id.profile_age);

        Observer<User> observer = user -> {
            if (user != null) {
                setProfileData(user);
            }
        };
        String userId = FirebaseAuth.getInstance().getUid();
        profileViewModel = new ViewModelProvider(getActivity())
                .get(ProfileViewModel.class);
        profileViewModel.update(userId);
        profileViewModel
                .getUserInfoById(userId)
                .observe(getViewLifecycleOwner(), observer);
    }

    private void log(String message) {
        Log.d(LOG_TAG, message);
    }

    private void setProfileData(@NonNull User user) {
        name.setText(user.getName());
        age.setText(String.valueOf(user.getAge()));
    }
}