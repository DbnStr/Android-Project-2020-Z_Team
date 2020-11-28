package ru.mail.z_team.icon_fragments.news;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import ru.mail.z_team.R;
import ru.mail.z_team.icon_fragments.walks.Walk;

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {
    private static final String LOG_TAG = "NewsAdapter";
    private ArrayList<Walk> walks;
    private final Context context;

    public NewsAdapter(Context context) {
        this.context = context;
        this.walks = new ArrayList<>();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Log.d(LOG_TAG, "OnBindViewHolderWalk " + position);
        String title = walks.get(position).getTitle();
        Date date = walks.get(position).getDate();
        holder.newsTitle.setText(title);
        SimpleDateFormat sdf =
                new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        holder.newsDate.setText(sdf.format(date));
    }

    @Override
    public int getItemCount() {
        return walks.size();
    }

    public void setWalks(ArrayList<Walk> walks) {
        this.walks = walks;
        this.notifyItemRangeChanged(0, this.walks.size());
    }
}