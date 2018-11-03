package giphy.videosearch.httprequest;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import giphy.videosearch.dto.MyObjectBox;
import io.objectbox.BoxStore;


public class MyApplication extends Application implements Utils {

    public static Context context;
    private static MyApplication mInstance;
    private BoxStore mBoxStore;
    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
        mInstance = this;
        mBoxStore = MyObjectBox.builder().androidContext(MyApplication.this).build();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public static MyApplication getApp() {
        return mInstance;
    }
    public BoxStore getBoxStore() {
        return mBoxStore;
    }
}
