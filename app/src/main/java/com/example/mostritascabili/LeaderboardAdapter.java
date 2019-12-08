package com.example.mostritascabili;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardItem> {
    private LayoutInflater mInflater;

    public LeaderboardAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public LeaderboardItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.leaderboard_item, parent, false);
        return new LeaderboardItem(view);
    }
    @Override
    public void onBindViewHolder(LeaderboardItem holder, int position) {
        Profile profile = LeaderboardModel.getInstance().getProfileByIndex(position);
        holder.setText(profile);
    }
    @Override
    public int getItemCount() {
        return LeaderboardModel.getInstance().getProfile().size();
    }
}

