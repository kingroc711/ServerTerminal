package club.com.serverterminal;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.io.IOException;

public class MainService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private Handler mActivityHandler = null;
    private HttpServer mHttpServer = null;
    private String mControlIP = null;

    public final static int SEND_BROADCAST_TO_CENTER = 10000;
    public final static int CHECK_CENTER_CONTROL     = 10001;
    public final static int UPDATE_MSG               = 10002;

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        MainService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MainService.this;
        }
    }

    public Handler getActivityHandler() {
        return mActivityHandler;
    }

    /**
     * method for clients
     */
    public void setActivityHandler(Handler handler) {
        mActivityHandler = handler;
        CNTrace.d("set handler : " + handler);
    }


    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            CNTrace.d("handleMessage : " + msg.what);
            switch (msg.what) {
                case SEND_BROADCAST_TO_CENTER:
                    NetWorkTools.sendBroadCastToCenter();
                    mServiceHandler.sendEmptyMessageDelayed(CHECK_CENTER_CONTROL, 2000L);
                    break;

                case CHECK_CENTER_CONTROL:
                    if(mControlIP == null) {
                        Handler h = App.getHandler();
                        if (h != null) {
                            h.sendEmptyMessage(0);
                        }
                    }
                    break;

                case UPDATE_MSG:
                    mControlIP = ((Intent)msg.obj).getStringExtra("ip");
                    CNTrace.d("mControlIP : " + mControlIP);
                    break;
            }
        }
    }

    private void createNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(100, mBuilder.build());
    }

    private void createHttpServer() {
        mHttpServer = new HttpServer(8092);
        try {
            mHttpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        createNotification();
        createHttpServer();

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        mServiceHandler.sendEmptyMessage(SEND_BROADCAST_TO_CENTER);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        //Message msg = mServiceHandler.obtainMessage();
        //msg.arg1 = startId;
        //msg.obj = intent;
        //mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart

        Message msg = mServiceHandler.obtainMessage();
        msg.what = intent.getIntExtra("id", -1);
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        CNTrace.d(intent.toString());
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if(mHttpServer != null)
            mHttpServer.stop();
    }
}
