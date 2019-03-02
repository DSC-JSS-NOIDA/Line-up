package project.tronku.line_up;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.os.Build;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class LineUpApplication extends Application {

    public static final String TAG = LineUpApplication.class.getSimpleName();
    public static final String NOTIF_CHANNEL_NAME = "Location Service Channel";
    public static final String NOTIF_CHANNEL_ID = "LocationServiceId";

    private RequestQueue mRequestQueue;
    private static LineUpApplication instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIF_CHANNEL_ID,
                    NOTIF_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public static synchronized LineUpApplication getInstance() {
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public void cancelRequest(Object tag) {
        if (mRequestQueue != null)
            mRequestQueue.cancelAll(tag);
    }

}
