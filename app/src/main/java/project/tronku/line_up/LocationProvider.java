package project.tronku.line_up;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LocationProvider {

    public static final String TAG = "LocationProvider";
    private Context context;
    private boolean isConnected;
    private LocationRequest locationRequest;
    private SharedPreferences pref;
    private FusedLocationProviderClient fusedLocationClient;

    public LocationProvider(Context context, LocationRequest request) {
        this.context = context;
        pref = PreferenceManager.getDefaultSharedPreferences(context);

        locationRequest = request;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public boolean getLocation() {

        Log.e(TAG, "getLocation: ");
        isConnected = Boolean.parseBoolean(pref.getString("connected", "true"));

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                pref.edit().putString("latitude", String.valueOf(latitude)).apply();
                pref.edit().putString("longitude", String.valueOf(longitude)).apply();
                Log.e(TAG, "lat: " + latitude + "; long: " + longitude);

//                if (isConnected)
//                    sendLocation(latitude, longitude);
            }
        },null);

        return false;
    }

    private void sendLocation(final double latitude, final double longitude) {
        final String token = PreferenceManager.getDefaultSharedPreferences(context).getString("token", "token_no");
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
