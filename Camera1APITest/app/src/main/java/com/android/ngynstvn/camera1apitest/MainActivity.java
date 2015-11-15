package com.android.ngynstvn.camera1apitest;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "(" + MainActivity.class.getSimpleName() + "): ";
    private static final int MEDIA_TYPE_IMAGE = 1;

    /**
     * STEPS TO SETTING UP CAMERA 1
     *
     * 1. Detect and access camera
     * 2. Create Preview Class (SurfaceView and something that implements SurfaceHolder interface)
     * 3. Build a Preview Layout
     * 4. Set up listeners for capture
     * 5. Capture and save files
     * 6. Release the camera (Camera.release())
     *
     */

    /**
     *
     * Camera Variables
     *
     */

    private Camera camera = null;
    private int cameraId = -1;
    private int cameraOrientation;
    private CameraPreview cameraPreview;
    private File tempImageFile;
    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File capturedImgFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

            if(capturedImgFile == null) {
                Log.e(TAG, "Error creating media file. Try again.");
                return;
            }

            tempImageFile = capturedImgFile;
            Log.v(TAG, "The image was captured but not saved.");
        }
    };

    /**
     *
     * View Variables
     *
     */

    private FrameLayout previewLayout;
    private RelativeLayout topIconsHolder;
    private Button exitCameraBtn;

    private RelativeLayout bottomIconsHolder;
    private View captureButton;
    private Button cameraSwitchBtn;
    private Button flashModeBtn;

    private RelativeLayout cancelCapBtnHolder;
    private View cancelCaptureBtn;
    private RelativeLayout approveCapBtnHolder;
    private View approveCaptureBtn;

    DisplayMetrics displayMetrics = new DisplayMetrics();
    private long transDuration = 200L;
    private long fadeDuration = 600L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() called");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Basically get this whole cycle working...it all starts with TextureView

        previewLayout = (FrameLayout) findViewById(R.id.fl_camera_preview);
        topIconsHolder = (RelativeLayout) findViewById(R.id.rl_top_icons);
        exitCameraBtn = (Button) findViewById(R.id.btn_exit_camera);
        bottomIconsHolder = (RelativeLayout) findViewById(R.id.rl_bottom_icons);
        captureButton = findViewById(R.id.v_pic_capture);
        cameraSwitchBtn = (Button) findViewById(R.id.btn_camera_switch);
        flashModeBtn = (Button) findViewById(R.id.btn_flash_mode);
        cancelCapBtnHolder = (RelativeLayout) findViewById(R.id.rl_cancel_picture);
        approveCapBtnHolder = (RelativeLayout) findViewById(R.id.rl_approve_pic);
        cancelCaptureBtn = findViewById(R.id.v_pic_cancel);
        approveCaptureBtn = findViewById(R.id.v_pic_approve);

        // Camera Material here

        if(isCameraHardwareAvailable()) {
            Log.v(TAG, "Camera hardware is available.");
            cameraId = getCurrentCameraId();
            Log.v(TAG, "Current cameraId: " + cameraId);

            camera = getCameraInstance(cameraId);
            cameraPreview = new CameraPreview(this, camera);
            camera.setDisplayOrientation(90);
            previewLayout.addView(cameraPreview);

            if(camera != null) {
                Log.e(TAG, "Camera Instance is not null");
            }
            else {
                Log.e(TAG, "Camera is null. Ending activity.");
                if(isTaskRoot()) {
                    finish();
                }
                return;
            }
        }

        exitCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takePhoto();

                int upDownTransHeight = bottomIconsHolder.getMeasuredHeight();

                moveVerticalAnimation(bottomIconsHolder, 0, upDownTransHeight, transDuration);
                moveVerticalAnimation(topIconsHolder, 0, (-1 * upDownTransHeight), transDuration);

                topIconsHolder.setEnabled(false);
                bottomIconsHolder.setEnabled(false);
                topIconsHolder.setVisibility(View.GONE);
                bottomIconsHolder.setVisibility(View.GONE);

                cancelCapBtnHolder.setEnabled(true);
                approveCapBtnHolder.setEnabled(true);

                int leftRightTransWidth = cancelCapBtnHolder.getMeasuredWidth();

                moveFadeAnimation(cancelCapBtnHolder, (-1 * leftRightTransWidth),
                        displayMetrics.widthPixels, 0, 0, 0.00f, 1.00f, transDuration, fadeDuration);

                moveFadeAnimation(approveCapBtnHolder, leftRightTransWidth, displayMetrics.widthPixels
                        , 0, 0, 0.0f, 1.0f, transDuration, fadeDuration);

                cancelCapBtnHolder.setVisibility(View.VISIBLE);
                approveCapBtnHolder.setVisibility(View.VISIBLE);
            }
        });

        cameraSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        flashModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cancelCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                int leftRightTransWidth = cancelCapBtnHolder.getMeasuredWidth();

                moveFadeAnimation(cancelCapBtnHolder, displayMetrics.widthPixels
                        , (-1 * leftRightTransWidth), 0, 0, 1.00f, 0.00F, transDuration, fadeDuration);

                moveFadeAnimation(approveCapBtnHolder, displayMetrics.widthPixels
                        , leftRightTransWidth, 0, 0, 1.00F, 0.00F, transDuration, fadeDuration);

                cancelCapBtnHolder.setVisibility(View.GONE);
                approveCapBtnHolder.setVisibility(View.GONE);
                cancelCapBtnHolder.setEnabled(false);
                approveCapBtnHolder.setEnabled(false);

                bottomIconsHolder.setEnabled(true);
                topIconsHolder.setEnabled(true);

                int upDownTransHeight = bottomIconsHolder.getMeasuredHeight();

                moveVerticalAnimation(bottomIconsHolder, upDownTransHeight, 0, transDuration);
                moveVerticalAnimation(topIconsHolder, (-1 * upDownTransHeight), 0, transDuration);

                bottomIconsHolder.setVisibility(View.VISIBLE);
                topIconsHolder.setVisibility(View.VISIBLE);
            }
        });

        approveCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart() called");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume() called");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause() called");
        releaseCamera();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "onSaveInstanceState() called");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop() called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy() called");
        super.onDestroy();
    }

    /**
     *
     * Animation Methods
     *
     */

    private void moveFadeAnimation(ViewGroup viewGroup, float fromX, float toX, float fromY, float toY,
                                   float fromAlpha, float toAlpha, long time1, long time2) {
        AnimationSet animationSet = new AnimationSet(true);

        TranslateAnimation translateAnimation = new TranslateAnimation(fromX, toX, fromY, toY);
        translateAnimation.setDuration(time1);

        AlphaAnimation fadeAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        fadeAnimation.setDuration(time2);

        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(fadeAnimation);

        viewGroup.startAnimation(animationSet);
    }

    private void moveVerticalAnimation(ViewGroup viewGroup, int fromY, int toY, long time) {

        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, fromY, toY);
        translateAnimation.setDuration(time);
        viewGroup.startAnimation(translateAnimation);

    }

    /**
     *
     * Camera Methods
     *
     */

    private boolean isCameraHardwareAvailable() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private Camera getCameraInstance(int cameraId) {

        if(isCameraHardwareAvailable()) {
            try {
                return Camera.open(cameraId);
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;
    }

    private int getCurrentCameraId() {

        int cameraId = -1;
        int numOfCameras = Camera.getNumberOfCameras();

        if(numOfCameras == 0) {
            Log.e(TAG, "There are no cameras on this phone.");
            Toast.makeText(this, "There are no cameras on this phone", Toast.LENGTH_SHORT).show();
            return cameraId;
        }

        // Get camera Ids

        for(int i = 0; i < numOfCameras; i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);

            if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.v(TAG, "Front facing camera detected");
                cameraId = i;
            }
            else if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.v(TAG, "Back facing camera detected");
            }
        }

        return cameraId;
    }

    private void setCameraDisplayOrientaiton() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 90;
                break;
            case  Surface.ROTATION_90:
                degrees = 0;
                break;
            case  Surface.ROTATION_180:
                degrees = 90;
                break;
            case  Surface.ROTATION_270:
                degrees = 0;
                break;
        }

        if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cameraOrientation = (cameraInfo.orientation + degrees) % 360;
            cameraOrientation = (360 - cameraOrientation) % 360;
        }
        else {
            cameraOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(cameraOrientation);
    }

    private void releaseCamera() {
        if(camera != null) {
            camera.release();
            camera = null;
            cameraPreview = null;
        }
    }

    private void takePhoto() {
        if(camera != null) {
            camera.takePicture(null, null, pictureCallback);
        }

        releaseCamera();
    }

    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File storageDirectory = new File(Environment.getExternalStorageDirectory() + "/Blocparty/");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! storageDirectory.exists()){
            if (! storageDirectory.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(storageDirectory.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        }
        else {
            return null;
        }

        return mediaFile;
    }
}
