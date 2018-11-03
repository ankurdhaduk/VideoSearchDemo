package giphy.videosearch.httprequest;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import giphy.videosearch.R;


public class ShowLogToast {
    public ShowLogToast() {

    }

    public static void ShowLog(String message) {
        Log.d("VIDEOSEARCH", "" + message);
    }

    public static void ShowLog(String tag, String message) {
        Log.d(tag, message);
    }

    public static void ShowLog(Context context, String message) {
        String TAG = context.getResources().getString(R.string.app_name);
        Log.d(TAG, message);
    }

    public static void ShowToast(Context context, String message, int time) {
        if (time == 0) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
}
