package ru.mail.z_team.icon_fragments.news;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Objects;

import ru.mail.z_team.Logger;
import ru.mail.z_team.R;

public class NewsFragment extends Fragment {

    private static final String LOG_TAG = "News Fragment";
    private Logger logger;

    private NewsAdapter adapter;

    private TextView noNews;
    private SwipeRefreshLayout newsRefreshLayout;

    private NewsViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("NewsFragment", "OnCreate");
        logger = new Logger(LOG_TAG, true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        noNews = view.findViewById(R.id.no_news_tv);
        newsRefreshLayout = view.findViewById(R.id.swipe_to_refresh_news);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_news);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new NewsAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        viewModel.updateCurrentUserNews();
        viewModel.getCurrentUserNews().observe(Objects.requireNonNull(getActivity()), walks -> {
            logger.log("get walks... " + walks.size());
            if (walks.isEmpty()){
                noNews.setVisibility(View.VISIBLE);
            }
            else {
                noNews.setVisibility(View.INVISIBLE);
                adapter.setWalks(walks);
            }
        });

        newsRefreshLayout.setOnRefreshListener(() -> {
            viewModel.updateCurrentUserNews();
            viewModel.getRefreshStatus()
                    .observe(getActivity(), refreshStatus -> {
                        if (refreshStatus == NewsRepository.RefreshStatus.FAILED) {
                            Toast toast = Toast.makeText(getContext(),
                                    "Fail with update", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        newsRefreshLayout.setRefreshing(false);
                        viewModel.getRefreshStatus().removeObservers(getActivity());
                    });
            newsRefreshLayout.setRefreshing(false);
        });
    }
}