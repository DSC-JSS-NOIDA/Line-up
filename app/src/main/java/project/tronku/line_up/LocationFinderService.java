package project.tronku.line_up;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static project.tronku.line_up.LineUpApplication.NOTIF_CHANNEL_ID;

public class LocationFinderService extends Service {

    private FusedLocationProviderClient fusedLocationClient;
    private SharedPreferences pref;
    private static final String TAG = "LocationFinder";
    private double latitude, longitude;
    private Handler handler;
    private int delay = 10000;
    private boolean isConnected;
    private LocationRequest request;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        request = new LocationRequest();
        request.setInterval(delay);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");
        getLocation();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getLocation();
                handler.postDelayed(this, delay);
            }
        }, delay);

        Intent startActivity = new Intent(this, QRCodeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, startActivity, 0);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), NOTIF_CHANNEL_ID)
                .setContentTitle("Line-up")
                .setContentText("GPS location is getting used.")
                .setSmallIcon(R.drawable.location_player)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    private void getLocation() {
        Log.e(TAG, "getLocation: ");
        isConnected = Boolean.parseBoolean(pref.getString("connected", "true"));

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.e(TAG, "lat: " + latitude + "; long: " + longitude);

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("latitude", String.valueOf(latitude));
                editor.putString("longitude", String.valueOf(longitude));
                editor.apply();

                if (isConnected) {
                    sendLocation(latitude, longitude);
                }
            }
        });

    }

    public void sendLocation(final double latitude, final double longitude) {
        final String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("token", "token_no");
        StringRequest locationReq = new StringRequest(Request.Method.PUT,API.BASE + API.LOCATION_SEND, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, " onResponse: " + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " onErrorResponse: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("lat", String.valueOf(latitude));
                params.put("lng", String.valueOf(longitude));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("Authorization", "Bearer " + token);

                return params;
            }
        };

        LineUpApplication.getInstance().addToRequestQueue(locationReq);
    }

}
