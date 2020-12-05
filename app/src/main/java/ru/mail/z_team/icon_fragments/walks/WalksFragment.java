package ru.mail.z_team.icon_fragments.walks;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import ru.mail.z_team.R;
import ru.mail.z_team.WrapContentLayoutManager;

public class WalksFragment extends Fragment {

    private static final String LOG_TAG = "WalksFragment";
    WalkAdapter adapter;
    private WalksViewModel viewModel;
    TextView noWalks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("WalksFragment", "OnCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_walks, container, false);

        noWalks = view.findViewById(R.id.no_walks_tv);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_walks);
        recyclerView.setLayoutManager(new WrapContentLayoutManager(getActivity()));

        adapter = new WalkAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(WalksViewModel.class);
        viewModel.updateCurrentUserWalks();
        viewModel.getCurrentUserWalks().observe(getActivity(), walks -> {
            log("get walks... " + walks.size());
            if (walks.isEmpty()) {
                noWalks.setVisibility(View.VISIBLE);
            } else {
                noWalks.setVisibility(View.INVISIBLE);
                adapter.setWalks(walks);
            }
        });
    }

    private void log(final String message) {
        Log.d(LOG_TAG, message);
    }
}
