package ru.mail.z_team.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import ru.mail.z_team.R;

public class UserFragment extends Fragment {

    public User user;

    public UserFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        TextView userName = view.findViewById(R.id.user_name);
        userName.setText(user.getName());
        return view;
    }
}