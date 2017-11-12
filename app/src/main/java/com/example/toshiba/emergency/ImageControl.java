package com.example.toshiba.emergency;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class ImageControl extends AppCompatActivity {
    private ImageView img;
    private TextView textLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        img = (ImageView)findViewById(R.id.viewImage);
        textLabel = (TextView) findViewById(R.id.textLabel);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_control);

        String ImageUrl = getIntent().getStringExtra("ImageURL");
        Log.d("COME ON ", "url: " + ImageUrl);
    }
}
