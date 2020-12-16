package ru.mail.z_team.icon_fragments.walks;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;
import ru.mail.z_team.map.MapActivity;

public class WalksFragment extends Fragment {

    private static final String LOG_TAG = "WalksFragment";
    private Logger logger;
    WalkAdapter adapter;
    private WalksViewModel viewModel;
    TextView noWalks;
    FloatingActionButton toMapBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        logger = new Logger(LOG_TAG, true);
        logger.log("onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_walks, container, false);

        logger.log("create view");
        noWalks = view.findViewById(R.id.no_walks_tv);
        toMapBtn = view.findViewById(R.id.map_activity_btn);
        toMapBtn.setOnClickListener(v -> startActivity(new Intent(requireActivity(), MapActivity.class)));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_walks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new WalkAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(WalksViewModel.class);
        viewModel.updateCurrentUserWalks();
        viewModel.getCurrentUserWalks().observe(getActivity(), walks -> {
            logger.log("get walks... " + walks.size());
            if (walks.isEmpty()) {
                noWalks.setVisibility(View.VISIBLE);
            } else {
                noWalks.setVisibility(View.INVISIBLE);
                adapter.setWalks(walks);
            }
        });
    }
}
