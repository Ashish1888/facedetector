package com.mine.facedetector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnDetect;
    Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnDetect = findViewById(R.id.btnDetect);
        requestForPermission();

        btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hasPermission = ContextCompat.checkSelfPermission(mContext,Manifest.permission.CAMERA);
                if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(mContext,LivePreviewActivity.class);
                    startActivity(i);
                }else{
                    requestForPermission();
                }
            }
        });
    }

    public void requestForPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
    }


}