package ru.mail.z_team.icon_fragments.go_out;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;
import ru.mail.z_team.map.MapActivity;

public class GoOutFragment extends Fragment {

    Button toMapBtn;

    private Logger logger;
    private static final String LOG_TAG = "GoOutFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        logger = new Logger(LOG_TAG, true);
        logger.log("onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_go_out, container, false);

        toMapBtn = view.findViewById(R.id.map_activity_btn);
        toMapBtn.setOnClickListener(v -> startActivity(new Intent(requireActivity(), MapActivity.class)));

        return view;
    }
}
