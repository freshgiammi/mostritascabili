package com.example.mostritascabili;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderboardItem extends RecyclerView.ViewHolder {
    private TextView leaderboard_username;
    private TextView leaderboard_xp;
    private TextView leaderboard_rank;
    private CircleImageView leaderboard_img;
    private Bitmap image;

    public LeaderboardItem(View itemView) {
        super(itemView);
        leaderboard_username = itemView.findViewById(R.id.leaderboard_username);
        leaderboard_img = itemView.findViewById(R.id.leaderboard_img);
        leaderboard_xp = itemView.findViewById(R.id.leaderboard_xp);
        leaderboard_rank = itemView.findViewById(R.id.leaderboard_rank);

    }
    public void generateViewHolder(Profile profile) {
        leaderboard_img.setImageResource(R.drawable.ic_person_outline_black_24dp); // Clean image before recycling
        if (profile.getImg() != null && org.apache.commons.codec.binary.Base64.isBase64(profile.getImg())) { // Check that IMG value isn't null.
            byte[] decodedString = Base64.decode(profile.getImg(), Base64.DEFAULT);
            image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            if (image != null)
                // Check that Base64 has been decoded correctly into a Bitmap image.
                // Some strings could be passed as valid (i.e "img", "aaa")
                // But they're actually not Base64 encoded bitmaps.
                // Only display the image if it's been correctly encoded.
                leaderboard_img.setImageBitmap(image);
            } else if (profile.getImg() != null)
                // If the string isn't Base64, log it
                Log.d("LeaderboardItem",profile.getUsername()+" has an invalid Base64 encoded profile image.");

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
             default:
                 leaderboard_rank.setTextColor(Color.BLACK);
                 leaderboard_rank.setTypeface(null, Typeface.NORMAL); // Set null, otherwise we'll pick up BOLD again and BOLD > NORMAL
                 break;

         }

        leaderboard_xp.setText(String.valueOf(profile.getXp()));
    }
}
