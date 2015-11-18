package com.android.ngynstvn.camera1apitest;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Ngynstvn on 9/23/15.
 */

public class Utils {

    // ----- Static Variables ----- //

    public static final String FILE_NAME = "log_states";
    public static final String CAM_STATE = "cam_state";
    public static final String FLASH_STATE = "flash_state";

    // Logging methods

    public static void logMethod(String className) {
        final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String classTag = "(" + className + ")";
        String message = stackTraceElements[3].getMethodName() + "() called | Thread: " + Thread.currentThread().getName();
        Log.e(classTag, message);
    }

    public static void logMethod(String className, String additional) {
        final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String classTag = "(" + className + ")";
        String message = additional + "'s " + stackTraceElements[3].getMethodName() + "() called | Thread: " + Thread.currentThread().getName();
        Log.e(classTag, message);
    }

    // SharedPreferences Methods

    public static void putSPrefBooleanValue(Context context, String fileName, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void putSPrefStrValue(Context context, String fileName, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
