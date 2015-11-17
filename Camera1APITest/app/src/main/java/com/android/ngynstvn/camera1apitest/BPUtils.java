package com.android.ngynstvn.camera1apitest;

import android.util.Log;

/**
 * Created by Ngynstvn on 9/23/15.
 */

public class BPUtils {

//    // ----- Static Variables ----- //
//
//    public static final String FILE_NAME = "log_states";
//    public static final String CHECKED_STATE = "checked_states";
//    public static final String SN_UPLOAD_STATES = "checked_states";
//    public static final String COLLECTION_MODE = "collection_mode";
//    public static final String CURRENT_COLLECTION = "current_collection";
//
//    // Upload Variables
//
//    public static final String FB_UPLOAD = "fb_upload";
//    public static final String TW_UPLOAD = "tw_upload";
//    public static final String IG_UPLOAD = "ig_upload";
//
//    // Facebook Variables
//
//    public static final String FB_LOGIN = "isFBLoggedIn";
//    public static final String FB_POSITION = "fbAdapterPosition";
//    public static final String FB_OBJECT = "fb_object";
//
//    // Twitter Variables
//
//    public static final String TW_LOGIN = "isTWLoggedIn";
//    public static final String TW_POSITION = "twAdapterPosition";
//    public static final String TW_ACCESS_TOKEN = "twAccessToken";
//    public static final String TW_ACCESS_TOKEN_SECRET = "twAccessTokenSecret";
//    public static final String TW_CONSUMER_KEY = "twConsumerKey";
//    public static final String TW_CONSUMER_SECRET = "twConsumerSecret";
//    public static final String IS_TW_ACCT_REG = "is_tw_account_registered";
//    public static final String TW_POST_IMG_URL = "post_image_url";
//
//    // Instagram Variables
//
//    public static final String IG_LOGIN = "isIGLoggedIn";
//    public static final String IG_POSITION ="igAdapterPosition";
//    public static final String IG_TOKEN = "ig_token";
//    public static final String IG_AUTH_CODE = "ig_auth_code";
//    public static final String IG_OBJECT = "ig_object";
//
//    // Table Related
//
//    public static final String DB_NAME = "blocparty_db";
//    public static final String POST_ITEM_TABLE = "post_item_table";
//    public static final String COLLECTION_TABLE = "collection_table";
//    public static final String USER_TABLE = "user_table";
//    public static final String USER_PROFILE_ID = "user_profile_id";
//    public static final String COLLECTION_NAME = "collection_name";
//    public static final String OP_PROFILE_ID = "op_profile_id";
//    public static final String POST_ID = "post_id";
//    public static final String PUBLISH_DATE = "publish_date";
//    public static final String USER_SOCIAL_NETWORK = "user_social_network";
//    public static final String USER_FULL_NAME = "user_full_name";
//
//    // ----- Static Methods ----- //
//
//    public static String classTag(Class className) {
//        return "(" + className.getSimpleName() + "): ";
//    }
//
//    // SharedPreferences
//
//    public static SharedPreferences newSPrefInstance(String name) {
//        return BlocpartyApplication.getSharedInstance().getSharedPreferences(name, Context.MODE_PRIVATE);
//    }
//
//    public static void putSPrefLoginValue(SharedPreferences sharedPreferences, String fileName, String posKey,
//                                          int adapterPosition, String stateKeyName, boolean state) {
//        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt(posKey, adapterPosition);
//        editor.putBoolean(stateKeyName, state);
//        editor.apply();
//    }
//
//    public static void putSPrefIntValue(SharedPreferences sharedPreferences, String fileName, String key, int value) {
//        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt(key, value);
//        editor.apply();
//    }
//
//    public static void putSPrefStrValue(SharedPreferences sharedPreferences, String fileName, String key, String value) {
//        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(key, value);
//        editor.apply();
//    }
//
//    public static void putSPrefBooleanValue(SharedPreferences sharedPreferences, String fileName, String key, boolean value) {
//        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean(key, value);
//        editor.apply();
//    }
//
//    public static void putSPrefFloatValue(SharedPreferences sharedPreferences, String fileName, String key, float value) {
//        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putFloat(key, value);
//        editor.apply();
//    }
//
//    public static void putSPrefLongValue(SharedPreferences sharedPreferences, String fileName, String key, long value) {
//        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putLong(key, value);
//        editor.apply();
//    }
//
//    public static void putSPrefObject(SharedPreferences sharedPreferences, String fileName, String key, Object objectValue) {
//        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(objectValue);
//        editor.putString(key, json);
//        editor.apply();
//    }
//
//    public static <T> T getSPrefObject(SharedPreferences sharedPreferences, Class<T> tClass, String key) {
//        Gson gson = new Gson();
//        String json = sharedPreferences.getString(key, "");
//        return gson.fromJson(json, tClass);
//    }
//
//    public static void delSPrefValue(SharedPreferences sharedPreferences, String fileName, String key) {
//        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.remove(key);
//        editor.apply();
//    }
//
//    public static void clearSPrefTable(SharedPreferences sharedPreferences, String fileName) {
//        sharedPreferences = BlocpartyApplication.getSharedInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear();
//        editor.apply();
//    }
//
//    public static String dateConverter(Long rawDate) {
//        DateFormat dateFormat = new SimpleDateFormat("h:mm a 'on' MM/dd/yy", Locale.ENGLISH);
//        Date date = new Date(rawDate);
//        return dateFormat.format(date);
//    }
//
//    // Time converter specifically for Facebook
//
//    public static long dateConverter(String rfcDate) {
//        try {
//            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
//            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//            Date date = dateFormat.parse(rfcDate.substring(0, 19) + ".000" + rfcDate.substring(19, rfcDate.length()));
//            return date.getTime();
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return 0L;
//        }
//    }
//
//    public static File getImageDirectory() {
//        return new File(Environment.getExternalStorageDirectory() + "/Blocparty/");
//    }

    // Logging methods

    public static void logMethod(String className) {
        final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String classTag = "(" + className + ")";
        String message = stackTraceElements[3].getMethodName() + "() called | Thread: " + Thread.currentThread().getName();
        Log.e(classTag, message);
    }
}
