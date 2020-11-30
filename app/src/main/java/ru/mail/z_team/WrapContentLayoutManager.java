package ru.mail.z_team;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WrapContentLayoutManager extends LinearLayoutManager {
    private static final String LOG_TAG = "WrapContentLayoutManager";

    public WrapContentLayoutManager(Context context) {
        super(context);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Log.e(LOG_TAG, "meet a IOOBE in RecyclerView");
        }
    }
}
