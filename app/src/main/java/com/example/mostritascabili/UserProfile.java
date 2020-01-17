package com.example.mostritascabili;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {
    private TextView username;
    private TextView xp;
    private TextView lp;
    private CircleImageView img;
    private MaterialButton button;
    private static int RESULT_LOAD_IMAGE = 1;
    private JSONObject sessionIdObject = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        final SharedPreferences storedSessionID = getSharedPreferences("session_id", MODE_PRIVATE);
        final String session_id = storedSessionID.getString("session_id", null);

        try {
            sessionIdObject.put("session_id", session_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        MaterialToolbar toolbar =  findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Populate UserProfile view
        username = findViewById(R.id.profile_username);
        img = findViewById(R.id.profile_image);
        lp = findViewById(R.id.profile_lp);
        xp = findViewById(R.id.profile_xp);

        username.setText(ProfileModel.getInstance().getProfile().getUsername());
        if (ProfileModel.getInstance().getProfile().getImg() != null) { // Check that IMG value isn't null.
            byte[] decodedString = Base64.decode(ProfileModel.getInstance().getProfile().getImg(), Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            img.setImageBitmap(image);
        }
        if (ProfileModel.getInstance().getProfile().getUsername() == null)
            username.setText("My Profile");
        else
            username.setText(ProfileModel.getInstance().getProfile().getUsername());

        lp.setText(String.valueOf(ProfileModel.getInstance().getProfile().getLp()));
        xp.setText(String.valueOf(ProfileModel.getInstance().getProfile().getXp()));


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ask user for READ_EXTERNAL_STORAGE permission, then load Intent if response is positive
                if (ContextCompat.checkSelfPermission(UserProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UserProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
        });
        button = findViewById(R.id.profile_change_username);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(UserProfile.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(15)});
                new MaterialAlertDialogBuilder(UserProfile.this)
                        .setView(input)
                        .setTitle("Changing username")
                        .setMessage("Remember, your username can't be longer that 15 characters.")
                        .setPositiveButton("Set username", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                username.setText(input.getText().toString());

                                JSONObject param = sessionIdObject;
                                try {
                                    param.put("username",input.getText().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                NetworkRequestHandler.setProfile(UserProfile.this, param, new ServerCallback() {
                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        NetworkRequestHandler.getProfile(UserProfile.this, sessionIdObject, new ServerCallback() {
                                            @Override
                                            public void onSuccess(JSONObject response) {
                                                ProfileModel.getInstance().clearAll();
                                                ProfileModel.getInstance().populate(response);
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Go back",null)
                        .show();
            }
        });
    }

    // Changes profile picture and updates user data on server
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            Bitmap bitmap = null;
            String encoded = null;
            try {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.getContentResolver(),selectedImage));
                img.setImageBitmap(bitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject param = sessionIdObject;
            try {
                param.put("img",encoded);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (encoded.length() < 137000){
                NetworkRequestHandler.setProfile(this, param, new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        NetworkRequestHandler.getProfile(UserProfile.this, sessionIdObject, new ServerCallback() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                ProfileModel.getInstance().clearAll();
                                ProfileModel.getInstance().populate(response);
                                Toast.makeText(UserProfile.this, "Profile image changed successfully.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(UserProfile.this, "Image is too big! Select something smaller than 100kb..", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Called when user allows READ_EXTERNAL_STORAGE permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(UserProfile.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

}
