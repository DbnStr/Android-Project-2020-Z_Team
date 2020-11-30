package ru.mail.z_team.icon_fragments.news;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.mail.z_team.R;

public class NewsViewHolder extends RecyclerView.ViewHolder {
    public final TextView newsTitle;
    public final TextView newsDate;
    public final TextView newsAuthor;

    public NewsViewHolder(@NonNull View itemView) {
        super(itemView);
        newsTitle = itemView.findViewById(R.id.news_item_title);
        newsDate = itemView.findViewById(R.id.news_item_date);
        newsAuthor = itemView.findViewById(R.id.news_item_author);
    }
}
