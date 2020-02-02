package com.example.mostritascabili;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryItem> {

    private LayoutInflater mInflater;

    public HistoryAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public HistoryItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.history_item, parent, false);
        return new HistoryItem(view);
    }
    @Override
    public void onBindViewHolder(HistoryItem holder, int position) {
        final HistoryMob historyMob = HistoryModel.getInstance().getMobByIndex(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("onNavigationItemSelected", "HistoryMob clicked!");
                Intent d = new Intent(view.getContext(), HistoryMobDetails.class);
                d.putExtra("img", historyMob.getImg());
                d.putExtra("id", historyMob.getObject_id());
                d.putExtra("times", historyMob.getTimes());
                view.getContext().startActivity(d);
            }
        });
        holder.generateViewHolder(historyMob);
    }
    @Override
    public int getItemCount() {
        return HistoryModel.getInstance().getHistory().size();
    }
}
