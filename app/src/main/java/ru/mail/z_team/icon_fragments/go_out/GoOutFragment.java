package ru.mail.z_team.icon_fragments.go_out;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.mail.z_team.AddWalkActivity;
import ru.mail.z_team.R;

public class GoOutFragment extends Fragment {

    Button addWalk;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("GoForWalk", "OnCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_go_out, container, false);
        addWalk = view.findViewById(R.id.create_walk_btn);
        addWalk.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddWalkActivity.class));
        });
        return view;
    }
}
