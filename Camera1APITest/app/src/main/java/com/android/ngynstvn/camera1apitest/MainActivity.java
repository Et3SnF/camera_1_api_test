package com.android.ngynstvn.camera1apitest;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    /**
     * STEPS TO SETTING UP CAMERA 1
     *
     * 1. Open the Camera object. Detect and access camera
     * 2. Create the camera preview
     * 3. Modify camera settings
     * 4. Set the preview orientation
     * 5. Take a picture
     * 6. Restart the preview
     * 7. Stop the camera preview and release the camera
     *
     */

    private static final String TAG = "(" + MainActivity.class.getSimpleName() + "): ";
    private static final int MEDIA_TYPE_IMAGE = 1;

    /**
     *
     * Camera Variables
     *
     */

    private boolean isCameraOpened = false;
    private Camera camera = null;
    private SurfaceHolder surfaceHolder;
    private Camera.Size previewSize = null;
    private int currentCameraId = -1;
    private File tempImageFile;

    /**
     *
     * View Variables
     *
     */

    private SurfaceView surfaceView;

    private RelativeLayout topIconsHolder;
    private Button exitCameraBtn;

    private RelativeLayout bottomIconsHolder;
    private View captureButton;
    private Button cameraSwitchBtn;
    private Button flashModeBtn;

    private RelativeLayout cancelCaptureBtn;
    private RelativeLayout approveCaptureBtn;

    DisplayMetrics displayMetrics = new DisplayMetrics();
    private long transDuration = 200L;
    private long fadeDuration = 600L;
    private boolean isFrontFacing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() called");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Basically get this whole cycle working...it all starts with TextureView

        surfaceView = (SurfaceView) findViewById(R.id.sv_camera_preview);
        topIconsHolder = (RelativeLayout) findViewById(R.id.rl_top_icons);
        exitCameraBtn = (Button) findViewById(R.id.btn_exit_camera);
        bottomIconsHolder = (RelativeLayout) findViewById(R.id.rl_bottom_icons);
        captureButton = findViewById(R.id.v_pic_capture);
        cameraSwitchBtn = (Button) findViewById(R.id.btn_camera_switch);
        flashModeBtn = (Button) findViewById(R.id.btn_flash_mode);
        cancelCaptureBtn = (RelativeLayout) findViewById(R.id.rl_cancel_picture);
        approveCaptureBtn = (RelativeLayout) findViewById(R.id.rl_approve_pic);

        exitCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isTaskRoot()) {
                    finish();
                }
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int upDownTransHeight = bottomIconsHolder.getMeasuredHeight();

                moveVerticalAnimation(bottomIconsHolder, 0, upDownTransHeight, transDuration);
                moveVerticalAnimation(topIconsHolder, 0, (-1 * upDownTransHeight), transDuration);

                topIconsHolder.setEnabled(false);
                bottomIconsHolder.setEnabled(false);
                topIconsHolder.setVisibility(View.GONE);
                bottomIconsHolder.setVisibility(View.GONE);

                cancelCaptureBtn.setEnabled(true);
                approveCaptureBtn.setEnabled(true);

                int leftRightTransWidth = cancelCaptureBtn.getMeasuredWidth();

                moveFadeAnimation(cancelCaptureBtn, (-1 * leftRightTransWidth),
                        displayMetrics.widthPixels, 0, 0, 0.00f, 1.00f, transDuration, fadeDuration);

                moveFadeAnimation(approveCaptureBtn, leftRightTransWidth, displayMetrics.widthPixels
                        , 0, 0, 0.0f, 1.0f, transDuration, fadeDuration);

                cancelCaptureBtn.setVisibility(View.VISIBLE);
                approveCaptureBtn.setVisibility(View.VISIBLE);
            }
        });

        cameraSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCameraIcons(isFrontFacing);
                isFrontFacing = !isFrontFacing;
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

                int leftRightTransWidth = cancelCaptureBtn.getMeasuredWidth();

                moveFadeAnimation(cancelCaptureBtn, displayMetrics.widthPixels
                        , (-1 * leftRightTransWidth), 0, 0, 1.00f, 0.00F, transDuration, fadeDuration);

                moveFadeAnimation(approveCaptureBtn, displayMetrics.widthPixels
                        , leftRightTransWidth, 0, 0, 1.00F, 0.00F, transDuration, fadeDuration);

                cancelCaptureBtn.setVisibility(View.GONE);
                approveCaptureBtn.setVisibility(View.GONE);
                cancelCaptureBtn.setEnabled(false);
                approveCaptureBtn.setEnabled(false);

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
     * Camera Methods
     *
     */

    /*
    *
    * Open and Close camera objects
    *
    * */

    private boolean isCameraHardwareAvailable() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
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
                flashModeBtn.setVisibility(View.GONE);
            }
            else if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.v(TAG, "Back facing camera detected");
                flashModeBtn.setVisibility(View.VISIBLE);
            }
        }

        return cameraId;
    }

    private Camera.Size getPreferredPreviewSize(int width, int height, Camera.Parameters parameters) {

        ArrayList<Camera.Size> supportedPreviewSizes = (ArrayList<Camera.Size>) parameters.getSupportedPreviewSizes();

        for(Camera.Size option : supportedPreviewSizes) {
            //Landscape Preview Sizes

            if(width > height) {

                if(option.width > width && option.height > height) {
                    supportedPreviewSizes.add(option);
                }

            }
            else {
                if(option.width > height && option.height > width) {
                    supportedPreviewSizes.add(option);
                }
            }
        }

        if(supportedPreviewSizes.size() > 0) {
            return Collections.max(supportedPreviewSizes, new Comparator<Camera.Size>() {
                @Override
                public int compare(Camera.Size lhs, Camera.Size rhs) {
                    return Long.signum(lhs.width * lhs.height - rhs.width * rhs.height);
                }
            });
        }

        return parameters.getSupportedPreviewSizes().get(0);
    }

    private void switchCameraFacing() {
        // release the camera and stop preview


        // If the camera is facing back, change cameraId to be front
        // else get camera id for back facing camera

        if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            fadeRotateViewAnimation(flashModeBtn, 1.00F, 0.00F, 700L);
            flashModeBtn.setVisibility(View.GONE);
        }
        else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            fadeRotateViewAnimation(flashModeBtn, 0.00F, 1.00F, 700L);
            flashModeBtn.setVisibility(View.VISIBLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void switchCameraIcons(boolean isFrontFacing) {
        Log.v(TAG, "switchCameraIcons() called");
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            isFrontFacing = true;
        }
        else if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
            isFrontFacing = false;
        }

        if(isFrontFacing) {
            fadeRotateViewAnimation(cameraSwitchBtn, 1.00F, 0.00F, 400L);
            cameraSwitchBtn.setVisibility(View.GONE);
            cameraSwitchBtn.setBackground(getResources().getDrawable(R.drawable.ic_camera_rear_white_24dp));
            fadeRotateViewAnimation(cameraSwitchBtn, 0.00F, 1.00F, 400L);
            cameraSwitchBtn.setVisibility(View.VISIBLE);
        }
        else {
            fadeRotateViewAnimation(cameraSwitchBtn, 1.00F, 0.00F, 400L);
            cameraSwitchBtn.setVisibility(View.GONE);
            cameraSwitchBtn.setBackground(getResources().getDrawable(R.drawable.ic_camera_front_white_24dp));
            fadeRotateViewAnimation(cameraSwitchBtn, 0.00F, 1.00F, 400L);
            cameraSwitchBtn.setVisibility(View.VISIBLE);
        }
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
                Log.d(TAG, "Unable to create directory.");
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

    /**
     *
     * SurfaceHolder.Callback Implemented Methods
     *
     */

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(TAG, "surfaceCreated() called");

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(TAG, "surfaceChanged() called");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(TAG, "surfaceDestroyed() called");
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

    private void fadeRotateViewAnimation(View view, float fromAlpha, float toAlpha, long time1) {
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation fadeAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.50F, Animation.RELATIVE_TO_SELF, 0.50F);

        fadeAnimation.setDuration(time1);
        rotateAnimation.setDuration(time1);

        animationSet.addAnimation(fadeAnimation);
        animationSet.addAnimation(rotateAnimation);

        view.startAnimation(animationSet);
    }

    private void quickFadeInOutAnimation(View view, float fromAlpha, float toAlpha, long time1, long time2) {
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation fadeInAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        AlphaAnimation fadeOutAnimation = new AlphaAnimation(toAlpha, fromAlpha);

        fadeInAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeOutAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        fadeInAnimation.setDuration(time1);
        fadeOutAnimation.setDuration(time2);

        animationSet.addAnimation(fadeInAnimation);
        animationSet.addAnimation(fadeOutAnimation);

        view.startAnimation(animationSet);
    }
}
