package club.com.serverterminal;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * Created by kingroc on 16-8-11.
 */

public class App extends Application {
    private static Context mContext;
    private static Handler mHandler = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static void setHandler (Handler handler){
        mHandler = handler;
    }

    public static Handler getHandler(){
        return mHandler;
    }

    public static Context getContext(){
        return mContext;
    }
}
