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

    private ProfileViewModel mProfileVewModel;
    private static final String LOG_TAG = "ProfileFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("ProfileFragment", "OnCreate");
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
        final TextView textView = getActivity().findViewById(R.id.profile_text);
        String id = FirebaseAuth.getInstance().getUid();
        Observer<String> observer = s -> textView.setText(s);

        mProfileVewModel = new ViewModelProvider(getActivity())
                .get(ProfileViewModel.class);
        mProfileVewModel.update(id);
        mProfileVewModel
                .getUserInfoById(id)
                .observe(getViewLifecycleOwner(), observer);
        textView.setText(mProfileVewModel.getUserInfoById(id).getValue());

    }

    private void log(String message) {
        Log.d(LOG_TAG, message);
    }
}