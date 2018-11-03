package giphy.videosearch.httprequest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
public class ConnectionDetector {
    Context mContext = null;

    public boolean isOnline() {
        mContext = MyApplication.context;
        ConnectivityManager MyConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo MyNetworkInfo = MyConnectivityManager.getActiveNetworkInfo();
        return (MyNetworkInfo != null && MyNetworkInfo.isConnected());
    }
}
