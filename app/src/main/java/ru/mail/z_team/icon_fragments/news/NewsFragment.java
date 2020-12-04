package ru.mail.z_team.icon_fragments.news;

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

public class NewsFragment extends Fragment {

    private static final String LOG_TAG = "News Fragment";
    NewsAdapter adapter;
    TextView noNews;
    NewsViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("NewsFragment", "OnCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        noNews = view.findViewById(R.id.no_news_tv);

        final RecyclerView recyclerView = view.findViewById(R.id.recycler_news);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new NewsAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        viewModel.updateCurrentUserNews();
        viewModel.getCurrentUserNews().observe(getActivity(), walks -> {
            Log.d(LOG_TAG, "get walks... " + walks.size());
            if (walks.isEmpty()){
                noNews.setVisibility(View.VISIBLE);
            }
            else {
                noNews.setVisibility(View.INVISIBLE);
                adapter.setWalks(walks);
            }
        });

        return view;
    }
}