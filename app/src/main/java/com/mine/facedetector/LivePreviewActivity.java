/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mine.facedetector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.annotation.KeepName;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Live preview demo for ML Kit APIs.
 */
@KeepName
public final class LivePreviewActivity extends AppCompatActivity {

    private static final String TAG = "LivePreviewActivity";

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private Boolean isFrontFacing = true;
    private ImageView imgCameraCapture;
    private ImageView imgCapture;
    private ImageView imgDone;
    private boolean isPhotoDetected = false;
    public static boolean isPhotoClicked = false;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_vision_live_preview);

        preview = findViewById(R.id.preview_view);
        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = findViewById(R.id.graphic_overlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }
        //FACE_DETECTION

        ImageView facingSwitch = findViewById(R.id.facing_switch);
        imgCameraCapture = findViewById(R.id.imgCameraCapture);
        imgCapture = findViewById(R.id.imgCapture);
        imgDone = findViewById(R.id.imgDone);

        facingSwitch.setOnClickListener(v -> {
          isFrontFacing = !isFrontFacing;
          toggleCamera();
        });

        imgCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmap != null){
                    Intent intent = new Intent(LivePreviewActivity.this, FullImageView.class);
                    startActivity(intent);
                }
            }
        });
        imgDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmap != null){
                    Intent intent = new Intent(LivePreviewActivity.this, FullImageView.class);
                    startActivity(intent);
                }
            }
        });

        imgCameraCapture.setOnClickListener(v -> {
            if(isPhotoDetected){
                isPhotoClicked = true;
                bitmap = loadBitmapFromView(graphicOverlay);
                imgCapture.setImageBitmap(bitmap);
                imgCapture.setVisibility(View.VISIBLE);
                imgDone.setVisibility(View.VISIBLE);
                createImageFromBitmap(bitmap);
            }else{
                Toast.makeText(this, "Please capture image only!", Toast.LENGTH_SHORT).show();
            }

        });


        createCameraSource();
        toggleCamera();
    }

    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "myImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    private void toggleCamera(){
      Log.d(TAG, "Set facing");
      if (cameraSource != null) {
        if (isFrontFacing) {
          cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
        } else {
          cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
        }
      }
      preview.stop();
      startCameraSource();
    }


    private void createCameraSource() {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }
        cameraSource.setMachineLearningFrameProcessor(new FaceDetectorProcessor(this, new OnFaceDetectedListener() {
            @Override
            public void onFaceDetected(Boolean isDetected) {
                isPhotoDetected = isDetected;
                if(isDetected){
                    imgCameraCapture.setImageResource(R.drawable.ic_camera_capture);
                }else{
                    imgCameraCapture.setImageResource(R.drawable.ic_baseline_camera_grey);
                }
            }

            @Override
            public void onMultipleFaceDetected() {

            }
        }));

    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        createCameraSource();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }
}
