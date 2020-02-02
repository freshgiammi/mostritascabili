package com.example.mostritascabili;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HistoryItem extends RecyclerView.ViewHolder {

    private TextView history_name;
    private TextView history_times;
    private CircleImageView history_img;
    private Bitmap image;

    public HistoryItem(View itemView) {
        super(itemView);
        history_name = itemView.findViewById(R.id.history_name);
        history_times = itemView.findViewById(R.id.history_times);
        history_img = itemView.findViewById(R.id.history_img);
    }
    public void generateViewHolder(HistoryMob historyMob) {
        history_times.setText(String.valueOf(historyMob.getTimes()));

        if( historyMob.getImg() != null){
            byte[] decodedString = Base64.decode(historyMob.getImg(), Base64.DEFAULT);
            image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            history_img.setImageBitmap(image);
        }

        ArrayList<MapObject> mapObjects = MapObjectModel.getInstance().getMapObjects();
        for( MapObject mapObject : mapObjects){
            if (mapObject.getId() == historyMob.getObject_id())
                history_name.setText(mapObject.getName());
        }
    }
}
