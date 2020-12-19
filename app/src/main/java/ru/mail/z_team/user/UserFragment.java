package ru.mail.z_team.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ru.mail.z_team.R;
import ru.mail.z_team.icon_fragments.friends.FriendsViewModel;

public class UserFragment extends Fragment {

    public User user;

    private FriendsViewModel viewModel;

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
        TextView userName = view.findViewById(R.id.user_name);

        if (savedInstanceState != null) {
            viewModel.getUserProfileData().observe(getActivity(), u -> {
                user = u;
                userName.setText(user.getName());
            });
        }
        else {
            userName.setText(user.getName());
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        viewModel.setUser(user);
    }
}