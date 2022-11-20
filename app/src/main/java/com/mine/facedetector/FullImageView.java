package com.mine.facedetector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;

public class FullImageView extends AppCompatActivity {

    Context context = this;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullimage);
        ImageView imgFullImage = findViewById(R.id.imgFullImage);

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.openFileInput("myImage"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        imgFullImage.setImageBitmap(bitmap);


    }
}
