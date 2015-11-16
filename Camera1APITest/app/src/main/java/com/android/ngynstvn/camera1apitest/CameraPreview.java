package com.android.ngynstvn.camera1apitest;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Ngynstvn on 11/15/15.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "(" + CameraPreview.class.getSimpleName() + ")";

    private SurfaceHolder surfaceHolder;
    private Camera camera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Display the preview

        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            Log.e(TAG, "There was an issue displaying the preview: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(surfaceHolder.getSurface() == null) {
            Log.e(TAG, "Preview Surface does not exist");
            return;
        }

        // Stop this preview before making changes such as rotation

        try {
            camera.stopPreview();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Whatever preview size is here

        camera.setDisplayOrientation(90);

        // Then display changes
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Being taken care of by releaseCamera();
    }
}
