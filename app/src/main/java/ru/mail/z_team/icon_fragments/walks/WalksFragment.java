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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.mail.z_team.R;
import ru.mail.z_team.WrapContentLayoutManager;
import ru.mail.z_team.icon_fragments.go_out.WalkViewModel;

public class WalksFragment extends Fragment {

    private static final String LOG_TAG = "WalksFragment";
    WalkAdapter adapter;
    WalkViewModel viewModel;
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

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_walks);
        recyclerView.setLayoutManager(new WrapContentLayoutManager(getActivity()));

        adapter = new WalkAdapter(getActivity());

        viewModel = new ViewModelProvider(this).get(WalkViewModel.class);
        viewModel.update();
        viewModel.getCurrentUserWalks().observe(getActivity(), walks -> {
            Log.d(LOG_TAG, "get walks... " + walks.size());
            if (walks.isEmpty()){
                noWalks.setVisibility(View.VISIBLE);
            }
            else {
                noWalks.setVisibility(View.INVISIBLE);
                adapter.setWalks(walks);
            }
        });

        recyclerView.setAdapter(adapter);

        return view;
    }
}
