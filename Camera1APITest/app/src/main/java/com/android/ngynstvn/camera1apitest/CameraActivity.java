package com.android.ngynstvn.camera1apitest;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
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
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {

    /**
     * STEPS TO SETTING UP CAMERA 1
     *
     * 0. Inflate any views. Ensure that the Surface is added to a FrameLayout, not a SurfaceView.
     * This will cause a headache by having the preview freeze after onResume() is called after onPause().
     * 1. Detect if there is any camera hardware
     * 2. If yes, get the Camera Instance
     * 3. Instantiate the SurfaceView. Set Context as this.
     * 4. Add the SurfaceView to the FrameLayout that will display the Surface
     * 5. Get the SurfaceHolder using the SurfaceView
     * 6. Add the SurfaceHolder.Callback listener to the SurfaceHolder.
     * 7. Inside surfaceCreated(), let the camera set the preview display
     * 8. Inside surfaceChanged(), get camera parameters, get appropriate preview sizes, fix orientation,
     * set the preview size based on the optimized method.
     * 9. Set the parameters using camera inside surfaceChanged().
     * 10. Invoke displayPreview() using camera inside surfaceChanged().
     *
     * Note 1: surfaceChanged() is called first when the Surface is created for the very first time.
     * This is called after when the SurfaceHolder has a listener added to it. Basically this listener
     * keeps track of the state of the Surface and what type of changes need to be done when
     * certain scenario occurs in this application.
     *
     */

    private static final String TAG = "(" + CameraActivity.class.getSimpleName() + ") ";
    private static final String CLASS_TAG = CameraActivity.class.getSimpleName();

    private SharedPreferences sharedPreferences;

    /**
     *
     * Camera Variables
     *
     */

    private Camera camera;
    private int currentCameraId = -1;
    private static boolean isFrontCamActive = true;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder; // connection to another object (Surface)
    private Camera.Size previewSize = null;

    private File tempImgFile;

    private CameraThread cameraThread;
    // Handler will be instantiated in run()
    private Handler cameraHandler;

    // Static block initializer. This is called first after when the class is instantiated.
    // Called before a constructor. This is a great place to instantiate any list of static variables.

    private static final SparseIntArray ORIENTATION_FIX = new SparseIntArray();

    static {
        ORIENTATION_FIX.append(Surface.ROTATION_0, 90);
        ORIENTATION_FIX.append(Surface.ROTATION_90, 0);
        ORIENTATION_FIX.append(Surface.ROTATION_180, 270);
        ORIENTATION_FIX.append(Surface.ROTATION_270, 90);
    }

    private static final int ERROR_NO_CAMERA_HARDWARE = 1;
    private static final int ERROR_NO_PHOTO_CAPTURE = 2;
    private static final int ERROR_MISSING_TEMP_FILE = 3;

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

    private RelativeLayout cancelCaptureBtn;
    private RelativeLayout approveCaptureBtn;

    DisplayMetrics displayMetrics = new DisplayMetrics();
    private long transDuration = 200L;
    private long fadeDuration = 600L;

    private String flashMode = Camera.Parameters.FLASH_MODE_OFF;

    private static final String FLASH_MODE_OFF = Camera.Parameters.FLASH_MODE_OFF;
    private static final String FLASH_MODE_ON = Camera.Parameters.FLASH_MODE_ON;
    private static final String FLASH_MODE_AUTO = Camera.Parameters.FLASH_MODE_AUTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.logMethod(CLASS_TAG);

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

                takePhoto();

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
                switchCamera();
            }
        });

        flashModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFlashMode();
            }
        });

        cancelCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                restartCameraOnCancel();

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
                Utils.logMethod(CLASS_TAG, "approveCaptureBtn");
                getTempImgFileUri(tempImgFile);
            }
        });

    }

    @Override
    protected void onStart() {
        Utils.logMethod(CLASS_TAG);
        super.onStart();
    }

    @Override
    protected void onResume() {
        Utils.logMethod(CLASS_TAG);
        super.onResume();
        startCameraThread();
    }

    @Override
    protected void onPause() {
        Utils.logMethod(CLASS_TAG);
        super.onPause();

        cameraHandler.post(new Runnable() {
            @Override
            public void run() {
                camera.stopPreview();
                releaseCamera();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Utils.logMethod(CLASS_TAG);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        Utils.logMethod(CLASS_TAG);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Utils.logMethod(CLASS_TAG);
        super.onDestroy();
    }

    /**
     *
     * Camera Setup Methods
     *
     */

    private boolean isCameraHardwareAvailable() {
        Utils.logMethod(CLASS_TAG);

        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void openCamera(int cameraId) {
        Utils.logMethod(CLASS_TAG);

        try {
            if(isCameraHardwareAvailable()) {
                camera = Camera.open(cameraId);

                if(camera != null) {
                    Log.v(TAG, "Camera instantiated");
                }
                else {
                    Log.v(TAG, "Camera instance is null");
                }
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Camera instantiation error.");
            e.printStackTrace();
        }
    }

    private void releaseCamera() {
        Utils.logMethod(CLASS_TAG);

        if (camera != null) {
            camera.release();
            camera = null;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                previewLayout.removeView(surfaceView);
            }
        });

    }

    private int getCurrentCameraId() {

        Utils.logMethod(CLASS_TAG);

        int numOfCameras = Camera.getNumberOfCameras();

        if(numOfCameras == 0) {
            Log.e(TAG, "There are no cameras on this phone.");
            ErrorDialog.newInstance(ERROR_NO_CAMERA_HARDWARE);
            return -1;
        }

        // Get last known state of camera. If nothing is found, set to back camera as default.

        SharedPreferences sharedPreferences = CameraActivity.this
                .getSharedPreferences(Utils.FILE_NAME, MODE_PRIVATE);

        isFrontCamActive = sharedPreferences.getBoolean(Utils.CAM_STATE, false);

        if(isFrontCamActive) {
            return Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        else {
            return Camera.CameraInfo.CAMERA_FACING_BACK;
        }

    }

    private Camera.Size getPreferredPreviewSize(int width, int height, Camera.Parameters parameters) {

        Utils.logMethod(CLASS_TAG);

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

    private void startCameraThread() {
        Utils.logMethod(CLASS_TAG);
        cameraThread = new CameraThread();
        cameraThread.start();
    }

    /**
     *
     * Camera Action Methods
     *
     */

    private void takePhoto() {
        cameraHandler.post(new Runnable() {
            @Override
            public void run() {
                Utils.logMethod(CLASS_TAG);

                if (camera != null) {
                    camera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            Utils.logMethod(CLASS_TAG, "takePhoto");
                            createTempImgFile(data);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.logMethod(CLASS_TAG, "takePhoto");
                            ErrorDialog.newInstance(ERROR_NO_PHOTO_CAPTURE).show(getFragmentManager(), "no_capture_dialog");
                        }
                    });
                }
            }
        });
    }

    private void restartCameraOnCancel() {

        cameraHandler.post(new Runnable() {
            @Override
            public void run() {
                Utils.logMethod(CLASS_TAG);
                deleteTempImgFile(tempImgFile);
            }
        });

        releaseCamera();

        if(cameraThread != null) {
            cameraThread.interrupt();
            cameraThread = null;
        }

        startCameraThread();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void switchCamera() {
        Utils.logMethod(CLASS_TAG);

        Utils.logMethod(CLASS_TAG, "switchCamera");
        // If the camera is facing back, change currentCameraId to be front
        // else get camera id for back facing camera

        if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {

            cameraHandler.post(new Runnable() {
                @Override
                public void run() {
                    Utils.putSPrefBooleanValue(CameraActivity.this, Utils.FILE_NAME,
                            Utils.CAM_STATE, false);

                    releaseCamera();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startCameraThread();
                        }
                    });
                }
            });

            fadeViewAnimation(flashModeBtn, 0.00F, 1.00F, 700L);
            flashModeBtn.setVisibility(View.VISIBLE);

            // First at back camera icon --> turns to front camera icon
            fadeRotateYAxisViewAnimation(cameraSwitchBtn, 1.00F, 0.00F, 400L);
            cameraSwitchBtn.setVisibility(View.GONE);
            cameraSwitchBtn.setBackground(getResources().getDrawable(R.drawable.ic_camera_front_white_24dp));
            fadeRotateYAxisViewAnimation(cameraSwitchBtn, 0.00F, 1.00F, 400L);
            cameraSwitchBtn.setVisibility(View.VISIBLE);
        }
        else if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {

            cameraHandler.post(new Runnable() {
                @Override
                public void run() {
                    Utils.putSPrefBooleanValue(CameraActivity.this, Utils.FILE_NAME,
                            Utils.CAM_STATE, true);

                    releaseCamera();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startCameraThread();
                        }
                    });
                }
            });

            fadeViewAnimation(flashModeBtn, 1.00F, 0.00F, 700L);
            flashModeBtn.setVisibility(View.GONE);

            // First at front camera icon --> turns to back camera icon
            fadeRotateYAxisViewAnimation(cameraSwitchBtn, 1.00F, 0.00F, 400L);
            cameraSwitchBtn.setVisibility(View.GONE);
            cameraSwitchBtn.setBackground(getResources().getDrawable(R.drawable.ic_camera_rear_white_24dp));
            fadeRotateYAxisViewAnimation(cameraSwitchBtn, 0.00F, 1.00F, 400L);
            cameraSwitchBtn.setVisibility(View.VISIBLE);
        }

        isFrontCamActive = !isFrontCamActive;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void getLatestCamState(int cameraId) {
        // Set the proper flash and camera switch icons

        if(cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cameraSwitchBtn.setBackground(getResources()
                    .getDrawable(R.drawable.ic_camera_rear_white_24dp));
            flashModeBtn.setVisibility(View.GONE);
        }
        else if(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            cameraSwitchBtn.setBackground(getResources()
                    .getDrawable(R.drawable.ic_camera_front_white_24dp));
            flashModeBtn.setVisibility(View.VISIBLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void getFlashMode() {

        SharedPreferences sharedPreferences = getSharedPreferences(Utils.FILE_NAME, MODE_PRIVATE);
        String mode = sharedPreferences.getString(Utils.FLASH_STATE, FLASH_MODE_OFF);

        if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK && camera != null) {

            Camera.Parameters cameraParameters = camera.getParameters();

            if(mode.equalsIgnoreCase(FLASH_MODE_OFF)) {
                cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(cameraParameters);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        flashModeBtn.setBackground(getResources().getDrawable(R.drawable.ic_flash_off_white_24dp));
                        Utils.putSPrefStrValue(CameraActivity.this, Utils.FILE_NAME, Utils.FLASH_STATE, FLASH_MODE_OFF);
                    }
                });

            }
            else if(mode.equalsIgnoreCase(FLASH_MODE_ON)) {
                cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                camera.setParameters(cameraParameters);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        flashModeBtn.setBackground(getResources().getDrawable(R.drawable.ic_flash_on_white_24dp));
                        Utils.putSPrefStrValue(CameraActivity.this, Utils.FILE_NAME, Utils.FLASH_STATE, FLASH_MODE_ON);
                    }
                });
            }
            else if(mode.equalsIgnoreCase(FLASH_MODE_AUTO)) {
                cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                camera.setParameters(cameraParameters);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        flashModeBtn.setBackground(getResources().getDrawable(R.drawable.ic_flash_auto_white_24dp));
                        Utils.putSPrefStrValue(CameraActivity.this, Utils.FILE_NAME, Utils.FLASH_STATE, FLASH_MODE_AUTO);
                    }
                });
            }

        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void switchFlashMode() {

        if(flashMode.equalsIgnoreCase(FLASH_MODE_OFF)) {
            flashMode = FLASH_MODE_ON;
            Utils.putSPrefStrValue(CameraActivity.this, Utils.FILE_NAME, Utils.FLASH_STATE, FLASH_MODE_ON);

            fadeRotateViewAnimation(flashModeBtn, 1.00F, 0.00F, 400L);
            toggleFlashMode(flashModeBtn, 0.00F, 1.00F, 400L, R.drawable.ic_flash_on_white_24dp);
        }
        else if(flashMode.equalsIgnoreCase(FLASH_MODE_ON)) {
            flashMode = FLASH_MODE_AUTO;
            Utils.putSPrefStrValue(CameraActivity.this, Utils.FILE_NAME, Utils.FLASH_STATE, FLASH_MODE_AUTO);
            toggleFlashMode(flashModeBtn, 0.00F, 1.00F, 400L, R.drawable.ic_flash_auto_white_24dp);
        }
        else if(flashMode.equalsIgnoreCase(FLASH_MODE_AUTO)) {
            flashMode = FLASH_MODE_OFF;
            Utils.putSPrefStrValue(CameraActivity.this, Utils.FILE_NAME, Utils.FLASH_STATE, FLASH_MODE_OFF);
            toggleFlashMode(flashModeBtn, 0.00F, 1.00F, 400L, R.drawable.ic_flash_off_white_24dp);
        }
        else {
            // Worst case scenario
            flashMode = FLASH_MODE_OFF;
            Utils.putSPrefStrValue(CameraActivity.this, Utils.FILE_NAME, Utils.FLASH_STATE, FLASH_MODE_OFF);
            toggleFlashMode(flashModeBtn, 0.00F, 1.00F, 400L, R.drawable.ic_flash_off_white_24dp);
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void toggleFlashMode(View view, float fromAlpha, float toAlpha, long time, int drawableRef) {

        // fromAlpha must be less than toAlpha

        fadeRotateViewAnimation(flashModeBtn, toAlpha, fromAlpha, time);
        view.setVisibility(View.GONE);
        view.setBackground(getResources().getDrawable(drawableRef));
        fadeRotateViewAnimation(flashModeBtn, fromAlpha, toAlpha, time);
        view.setVisibility(View.VISIBLE);
    }

    /**
     *
     * Camera File Methods
     *
     */

    private void createTempImgFile(byte[] bytes) {
        Utils.logMethod(CLASS_TAG);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "IMG_" + timeStamp;

        try {
            final File tempFile = File.createTempFile(fileName, ".jpg", getCacheDir());
            Log.v(TAG, "Location of Cache Directory: " + getCacheDir().getAbsolutePath() + " | File Name: " + tempFile.getName());

            // Create a stream to write to a file. Parameter takes a file of interest
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);

            // Begin the writing process
            fileOutputStream.write(bytes);
            fileOutputStream.close();

            tempImgFile = tempFile;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteTempImgFile(File file) {
        if(file != null) {
            file.delete();
            Log.v(TAG, "Temporary File Deleted");
        }
        else {
            Log.e(TAG, "Unable to delete file. Null.");
        }
    }

    private void getTempImgFileUri(final File file) {

        cameraHandler.post(new Runnable() {
            @Override
            public void run() {
                if(file != null) {
                    Log.v(TAG, "tempImage is not null");
                    URI imageUri = file.toURI();
                    Log.v(TAG, "URI: " + imageUri.getPath());
//            Intent intent = new Intent(CameraActivity.this, ImageUploadActivity.class);
//            intent.putExtra("image_uri", imageUri);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
                }
                else {
                    Log.v(TAG, "tempImage is null");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ErrorDialog.newInstance(ERROR_MISSING_TEMP_FILE).show(getFragmentManager(),
                                    "missing_tmp_file");
                        }
                    });

                    restartCameraOnCancel();
                }
            }
        });
    }

//    private static File getOutputMediaFile(int type) {
//        BPUtils.logMethod(CLASS_TAG);
//
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//
//        File storageDirectory = new File(Environment.getExternalStorageDirectory() + "/Blocparty/");
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (! storageDirectory.exists()){
//            if (! storageDirectory.mkdirs()){
//                Log.d(TAG, "Unable to create directory.");
//                return null;
//            }
//        }
//
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File mediaFile;
//        if (type == MEDIA_TYPE_IMAGE){
//            mediaFile = new File(storageDirectory.getPath() + File.separator +
//                    "IMG_"+ timeStamp + ".jpg");
//        }
//        else {
//            return null;
//        }
//
//        return mediaFile;
//    }

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

    private void fadeRotateViewAnimation(View view, float fromAlpha, float toAlpha, long time) {
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation fadeAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.50F, Animation.RELATIVE_TO_SELF, 0.50F);

        fadeAnimation.setDuration(time);
        rotateAnimation.setDuration(time);

        animationSet.addAnimation(fadeAnimation);
        animationSet.addAnimation(rotateAnimation);

        view.startAnimation(animationSet);
    }

    private void fadeRotateYAxisViewAnimation(View view, float fromAlpha, float toAlpha, long time1) {
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation fadeAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotationY", 0.00F, 180.00F);

        fadeAnimation.setDuration(time1);
        objectAnimator.setDuration(time1);

        animationSet.addAnimation(fadeAnimation);

        view.startAnimation(animationSet);
        objectAnimator.start();
    }

    private void fadeViewAnimation(View view, float fromAlpha, float toAlpha, long time) {
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation fadeAnimation = new AlphaAnimation(fromAlpha, toAlpha);

        fadeAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        fadeAnimation.setDuration(time);

        animationSet.addAnimation(fadeAnimation);

        view.startAnimation(animationSet);
    }

    /**
     *
     * Camera Thread
     *
     */

    private class CameraThread extends Thread implements SurfaceHolder.Callback {

        @Override
        public void run() {
            Utils.logMethod(CLASS_TAG, "CameraThread");

            // Prepare the looper. Need this in order to instantiate the handler in this thread.
            Looper.prepare();
            cameraHandler = new Handler();

            if(isCameraHardwareAvailable()) {
                currentCameraId = getCurrentCameraId();
                Log.v(TAG, "Current Camera ID: " + currentCameraId);

                if(currentCameraId != -1) {

                    openCamera(currentCameraId);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            surfaceView = new SurfaceView(CameraActivity.this);
                            previewLayout.addView(surfaceView);
                            surfaceHolder = surfaceView.getHolder();
                            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                            surfaceHolder.addCallback(CameraThread.this);
                            getLatestCamState(currentCameraId);
                        }
                    });
                }
            }
            else {
                Log.e(TAG, "No camera hardware detected in the device.");

                ErrorDialog.newInstance(ERROR_NO_CAMERA_HARDWARE).show(getFragmentManager(), "no_cam_hardware");

                if(!isTaskRoot()) {
                    finish();
                }
            }

            Looper.loop();
        }

        @Override
        public void surfaceCreated(final SurfaceHolder holder) {
            Utils.logMethod(CLASS_TAG);
            // When the Surface is created, set the preview display. Don't start it here.

            cameraHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Utils.logMethod(CLASS_TAG, "surfaceCreated");
                        if(camera != null) {
                            camera.setPreviewDisplay(holder);
                        }
                    }
                    catch (IOException e) {
                        Log.e(TAG, "There was an error setting up the preview display");
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, final int width, final int height) {
            Utils.logMethod(CLASS_TAG);
            // When the Surface is displayed for the first time, it calls this. Start preview here.
            // Tell Surface client how big the drawing area will be (preview size)

            if(camera == null) {
                Log.e(TAG, "Camera is null inside surfaceChanged()");
                return;
            }

            cameraHandler.post(new Runnable() {
                @Override
                public void run() {
                    Camera.Parameters cameraParameters = camera.getParameters();
                    previewSize = getPreferredPreviewSize(width, height, cameraParameters);
                    Log.v(TAG, "Current Preview Size: (" + previewSize.width + ", " + previewSize.height + ")");

                    cameraParameters.setPreviewSize(previewSize.width, previewSize.height);
                    camera.setParameters(cameraParameters);

                    try {
                        int rotation = getWindowManager().getDefaultDisplay().getRotation();
                        camera.setDisplayOrientation(ORIENTATION_FIX.get(rotation));
                        camera.startPreview();
                    } catch (Exception e) {
                        Log.e(TAG, "Camera was unable to start preview");
                        e.printStackTrace();
                        releaseCamera();
                    }
                }
            });
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Utils.logMethod(CLASS_TAG);
            // Handled by onPause()
            // onPause() gets called before this.
        }
    }

    /**
     *
     * ErrorDialog
     *
     */

    public static class ErrorDialog extends DialogFragment {

        int errorCode = 0;
        String errorMessage;

        public static ErrorDialog newInstance(int errorCode) {
            ErrorDialog errorDialog = new ErrorDialog();
            Bundle bundle = new Bundle();
            bundle.putInt("errorCode", errorCode);
            errorDialog.setArguments(bundle);
            return errorDialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            errorCode = getArguments().getInt("errorCode");

            switch (errorCode) {
                case ERROR_NO_CAMERA_HARDWARE:
                    errorMessage = "No camera hardware detected in the camera.";
                    break;
                case ERROR_NO_PHOTO_CAPTURE:
                    errorMessage = "Unable to capture photo.";
                    break;
                case ERROR_MISSING_TEMP_FILE:
                    errorMessage = "There was an issue with the previous capture. Try again.";
                    break;
            }

            return new AlertDialog.Builder(getActivity())
                    .setMessage(errorMessage)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    })
                    .create();
        }
    }
}
