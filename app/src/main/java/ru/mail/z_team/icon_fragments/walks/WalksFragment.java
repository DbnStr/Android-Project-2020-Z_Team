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

import com.google.firebase.auth.FirebaseAuth;

import ru.mail.z_team.R;
import ru.mail.z_team.icon_fragments.go_out.WalkViewModel;

public class WalksFragment extends Fragment {

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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new WalkAdapter(getActivity());

        String userId = FirebaseAuth.getInstance().getUid();
        viewModel = new ViewModelProvider(this).get(WalkViewModel.class);
        viewModel.update();
        viewModel.getCurrentUserWalks().observe(getActivity(), walks -> {
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
