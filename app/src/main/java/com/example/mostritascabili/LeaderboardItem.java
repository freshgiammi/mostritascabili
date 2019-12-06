package com.example.mostritascabili;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class LeaderboardItem extends RecyclerView.ViewHolder {
    private TextView mTextView;

    public LeaderboardItem(View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.textView);
    }
    public void setText(Profile profile) {

        mTextView.setText(profile.getUsername());
    }
}
