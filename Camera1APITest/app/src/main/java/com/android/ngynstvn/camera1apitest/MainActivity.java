package com.android.ngynstvn.camera1apitest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "(" + MainActivity.class.getSimpleName() + "): ";

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

        // SurfaceView stuff here
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

        exitCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}
