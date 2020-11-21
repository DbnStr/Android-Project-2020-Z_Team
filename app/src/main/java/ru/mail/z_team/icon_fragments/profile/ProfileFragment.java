package ru.mail.z_team.icon_fragments.profile;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

import ru.mail.z_team.R;

public class ProfileFragment extends Fragment {

    private static final String LOG_TAG = "ProfileFragment";

    private ProfileViewModel profileViewModel;
    private EditText name;
    private EditText age;
    private Button editBtn;
    private Button saveChangesBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        log("OnCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        name = view.findViewById(R.id.profile_name);
        age = view.findViewById(R.id.profile_age);
        editBtn = view.findViewById(R.id.edit_btn);
        saveChangesBtn = view.findViewById(R.id.save_changes_btn);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        disableEditAbilityAll();

        editBtn.setOnClickListener(v -> enableEditAbilityAll());
        saveChangesBtn.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getUid();
            profileViewModel.changeUserInformation(userId, getProfileInfo());
            disableEditAbilityAll();
        });


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

    private void enableEditAbilityAll() {
        enableEditAbility(name);
        enableEditAbility(age);
    }

    private void disableEditAbilityAll() {
        disableEditAbility(name);
        disableEditAbility(age);
    }

    private void disableEditAbility(EditText editText) {
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }

    private void enableEditAbility(EditText editText) {
        editText.setEnabled(true);
        editText.setCursorVisible(true);
        editText.setBackgroundColor(Color.LTGRAY);
    }

    private HashMap<String, String> getProfileInfo() {
        HashMap<String, String> info = new HashMap<>();
        String nameText = name.getText().toString();
        String ageText = age.getText().toString();
        info.put("age", ageText);
        info.put("name", nameText);
        return info;
    }

    @SuppressLint("SetTextI18n")
    private void setProfileData(@NonNull User user) {
        name.setText(user.getName());
        age.setText(String.valueOf(user.getAge()));
    }
}