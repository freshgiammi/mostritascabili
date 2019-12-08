package com.example.mostritascabili;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class LeaderboardItem extends RecyclerView.ViewHolder {
    private TextView leaderboard_username;
    private TextView leaderboard_xp;
    private TextView leaderboard_rank;
    private ImageView leaderboard_img;
    private Bitmap image;

    public LeaderboardItem(View itemView) {
        super(itemView);
        leaderboard_username = itemView.findViewById(R.id.leaderboard_username);
        leaderboard_img = itemView.findViewById(R.id.leaderboard_img);
        leaderboard_xp = itemView.findViewById(R.id.leaderboard_xp);
        leaderboard_rank = itemView.findViewById(R.id.leaderboard_rank);

    }
    public void setText(Profile profile) {
        setIsRecyclable(false); //Todo: Solve issue of textColor and img not being correctly set to each CardView

        if (profile.getImg() != null) { // Check that IMG value isn't null.
            byte[] decodedString = Base64.decode(profile.getImg(), Base64.DEFAULT);
            image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            leaderboard_img.setImageBitmap(image);
        }

         if (profile.getUsername() != null)
             leaderboard_username.setText(profile.getUsername());
         else
             leaderboard_username.setText("NULL_USERNAME");

         leaderboard_rank.setText(String.valueOf(LeaderboardModel.getInstance().getProfile().indexOf(profile)+1));
         switch(leaderboard_rank.getText().toString()){
             case "1":
                 leaderboard_rank.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.gold));
                 leaderboard_rank.setTypeface(leaderboard_rank.getTypeface(), Typeface.BOLD);
                 break;
             case "2":
                 leaderboard_rank.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.silver));
                 leaderboard_rank.setTypeface(leaderboard_rank.getTypeface(), Typeface.BOLD);
                 break;
             case "3":
                 leaderboard_rank.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.bronze));
                 leaderboard_rank.setTypeface(leaderboard_rank.getTypeface(), Typeface.BOLD);
                 break;
         }

        leaderboard_xp.setText(String.valueOf(profile.getXp()));
    }
}
