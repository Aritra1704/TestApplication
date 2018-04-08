package com.zippr.testapplication.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.util.SparseArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zippr.testapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by preetambhosle on 30/09/15.
 */
public class Utils {
    static public void hideSoftKeyBoard(Context context, View currentFocus) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText() && currentFocus != null)
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
    }

    /**
     * Checks if external storage is available for read and write
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if external storage is available to at least read
     */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static Map<String, Object> convertBundleToMap(Bundle data) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (String key : data.keySet()) {
            Object val = data.get(key);
            if (val instanceof Bundle) {
                map.put(key, convertBundleToMap((Bundle) val));
            } else {
                if (val instanceof String) {
                    map.put(key, String.valueOf(val));
                } else {
                    map.put(key, val);
                }
            }
        }
        return map;
    }

    public static String getThumbnailUrl(String profileImageUrl) {
        if (TextUtils.isEmpty(profileImageUrl))
            return "";
        return new StringBuilder(profileImageUrl).insert(profileImageUrl.lastIndexOf('.'), "_thumbnail").toString();
    }

    public static String getScaledImageUrl(String profileImageUrl) {
        if (TextUtils.isEmpty(profileImageUrl))
            return "";
        return new StringBuilder(profileImageUrl).insert(profileImageUrl.lastIndexOf('.'), "_scaled").toString();
    }

    /**
     * Converts a map where all the values will either be {@link String} or JSONObject
     *
     * @param map the supplied map
     * @return the map containing values as {@link String} or {@link JSONObject}
     */
    public static Map<String, Object> convertMapForJSONObject(Map<String, Object> map) {
        Map<String, Object> m = new HashMap<>();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            String key = e.getKey();
            Object value = e.getValue();
            if (value instanceof String) {
                m.put(key, value);
            } else if (value instanceof Map) {
                m.put(key, new JSONObject((Map) value));
            } else if (value instanceof ArrayList) {
                try {
                    m.put(key, new JSONArray(new Gson().toJson(value, new TypeToken<ArrayList<Object>>() {
                    }.getType())));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            } else {
                // It is a JSONObject
                m.put(key, value);
            }
        }
        return m;
    }

    public static String getTime(int totalSecs) {
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static ProgressDialog createProgressDialog(Context context) {
        ProgressDialog dlg = new ProgressDialog(context);
        dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dlg.setMessage("Loading");

        dlg.setCancelable(false);
        dlg.setIndeterminate(true);
        return dlg;
    }

    public static void saveBitmapToFile(Bitmap bitmap, String fileName) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getRowBytes() * bitmap.getHeight();
        } else {
            return bitmap.getByteCount();
        }
    }

    public static boolean isValidEmail(String email) {
        return TextUtils.isEmpty(email) ? false : Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

//    public static HashMap<Character, Integer> getCharacterToColorMap(Context context) {
//        //char,color
//        HashMap<Character, Integer> charColorMap = new HashMap<>();
//        String[] arr = context.getResources().getStringArray(R.array.colors_for_alphabets);
//        charColorMap = new HashMap<>();
//        for (String s : arr) {
//            String[] sr = s.split(";");
//            charColorMap.put(sr[0].charAt(0), Color.parseColor(sr[1]));
//        }
//        return charColorMap;
//    }

    public static boolean isActivityDestroyed(Context context) {
        if (context == null)
            return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (((Activity) context).isDestroyed()) {
                return true;
            }
        } else {
            if (context == null || (context != null && ((Activity) context).isFinishing())) {
                return true;
            }
        }
        return false;
    }

    public static String get12HoursTime(String militaryTime) throws ParseException {
        if (militaryTime.length() != 4)
            throw new IllegalArgumentException("Invalid time format");
        // Heres your military time int like in your code sample
        int milTime = Integer.parseInt(militaryTime);
        // Convert the int to a string ensuring its 4 characters wide, parse as a date
        Date date = new SimpleDateFormat("hhmm").parse(String.format("%04d", milTime));
        // Set format: print the hours and minutes of the date, with AM or PM at the end
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mma", Locale.US);
        // Print the date!
        // Output: 12:56 AM
        return sdf.format(date);
    }

    /**
     * Get the day of week starting from monday
     *
     * @param day day of week, from monday to sunday (0-6)
     * @return the day
     */
    public static String getDayOfWeek(int day) {
        SparseArray arr = new SparseArray();
        arr.put(0, "Monday");
        arr.put(1, "Tuesday");
        arr.put(2, "Wednesday");
        arr.put(3, "Thursday");
        arr.put(4, "Friday");
        arr.put(5, "Saturday");
        arr.put(6, "Sunday");

        return (String) arr.get(day);
    }

    /**
     * Method checks if the app is in background or not
     *
     * @param context
     * @return true/false
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }


    /**
     * ∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆
     * ∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆ AES-256 encryption to generate OTP secret ∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆
     * ∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆∆
     * Use this library in app.gradle "useLibrary 'org.apache.http.legacy'"
     */
//    private static final int pswdIterations = 10;
//    private static final int keySize = 128;
//
//    public static String encrypt(String phoneNumber) throws NoSuchAlgorithmException,
//            NoSuchPaddingException, DecoderException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException,
//            IllegalBlockSizeException, BadPaddingException {
//
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//
//        KeySpec spec = new PBEKeySpec(AppConstant.PUBLIC_KEY.toCharArray(), Hex.decodeHex(AppConstant.SALT.toCharArray()), pswdIterations, keySize);
//        SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
//
//        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(Hex.decodeHex(AppConstant.INITIAL_VECTOR.toCharArray())));
//
//        byte[] encryptedTextBytes = cipher.doFinal(phoneNumber.getBytes());
//        return Base64.encodeToString(encryptedTextBytes, Base64.DEFAULT);
//    }
}
