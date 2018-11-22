package tw.com.edu.ntue.toybabysitter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

public final class Global {
    public static final String ACTION_RECEIVED_GCM_MESSAGE = Global.class.getPackage() + ".action.RECEIVED_GCM_MESSAGE";
    private static String DeviceType;

    public static String getDeviceType() {
        return DeviceType;
    }

    public static void setDeviceType(String deviceType) {
        DeviceType = deviceType;
    }

    public static Context currentContext = null;

    public static void handlerPost(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public static void handlerPostDelay(Runnable runnable, int delayMillis) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delayMillis);
    }

    public static void startActivity(Context context, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Intent intent, int flag) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP | flag);
        context.startActivity(intent);
    }

    public static boolean isEmptyString(String value) {
        return (value == null || value.length() < 1);
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager  = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    /*
     *將utf-8轉換成unicode格式
     */
    public static String stringToUnicode(String string) {

        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            unicode.append("\\u" + Integer.toHexString(c));
        }
        String str = unicode.toString();

        return str.replaceAll("\\\\", "0x");
    }

    /*
     * 將unicode轉換成utf-8
     */
    public static String unicodeToString(String unicode) {

        String str = unicode.replace("0x", "\\");

        StringBuffer string = new StringBuffer();
        String[] hex = str.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int data = Integer.parseInt(hex[i], 16);
            string.append((char) data);
        }
        return string.toString();
    }
}