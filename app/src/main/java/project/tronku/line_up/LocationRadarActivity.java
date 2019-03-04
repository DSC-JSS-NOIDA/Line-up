package project.tronku.line_up;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.skyfishjy.library.RippleBackground;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class LocationRadarActivity extends AppCompatActivity {

    private static final String TAG = "LocationRadarActivity";
    private static final int REQUEST_CHECK_SETTINGS = 101;
    private static final int GPS_PERMISSION_CODE = 3;

    private ImageView player1, player2, player3, player4;
    private TextView dis1, dis2, dis3, dis4;
    private RippleBackground rippleBackground;
    private SharedPreferences pref;
    private CardView refresh;
    private double lat, lng;
    private String accessToken;
    private View layer;
    private TextView loader;
    private NetworkReceiver receiver;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private boolean needPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_radar);

        rippleBackground = findViewById(R.id.ripple);
        refresh = findViewById(R.id.refresh);
        receiver = new NetworkReceiver();
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        player1 = findViewById(R.id.player1_img);
        player2 = findViewById(R.id.player2_img);
        player3 = findViewById(R.id.player3_img);
        player4 = findViewById(R.id.player4_img);

        dis1 = findViewById(R.id.player1_distance);
        dis2 = findViewById(R.id.player2_distance);
        dis3 = findViewById(R.id.player3_distance);
        dis4 = findViewById(R.id.player4_distance);

        layer = findViewById(R.id.layer);
        loader = findViewById(R.id.loader);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLatestLocation();
            }
        });
    }

    private class Participant {

        private String username;
        private String distance;
    }

    private void updatePositions() {
        if (pref.contains("token")) {
            accessToken = pref.getString("token", "");
            layer.setVisibility(View.VISIBLE);
            loader.setVisibility(View.VISIBLE);
            refresh.setEnabled(false);

            final List<TextView> textViews = Arrays.asList(dis1, dis2, dis3, dis4);

            if (receiver.isConnected()) {
                fetchNearestParticipants(accessToken, new VolleyCallback(){
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(String response) {
                        if(response != null){
                            List<Participant> participants = getParticipantsFromResponse(response);
                            for(int i = 0; i < Math.min(participants.size(), textViews.size()); i++){
                                TextView textView = textViews.get(i);
                                Participant participant = participants.get(i);
                                textView.setText(participant.distance.substring(0, participant.distance.length()-2));
                            }

                        } else{
                            Toast.makeText(LocationRadarActivity.this, "Error fetching data, Please try again.", Toast.LENGTH_SHORT).show();
                        }
                        layer.setVisibility(View.INVISIBLE);
                        loader.setVisibility(View.INVISIBLE);
                        refresh.setEnabled(true);
                        rippleBackground.startRippleAnimation();
                        player1.setVisibility(View.VISIBLE);
                        player2.setVisibility(View.VISIBLE);
                        player3.setVisibility(View.VISIBLE);
                        player4.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onError(int status, String error) {
                        if(status == HttpStatus.UNAUTHORIZED.value()){
                            Toast.makeText(LocationRadarActivity.this, "Please login to perform this action.", Toast.LENGTH_SHORT).show();
                            pref.edit().clear().apply();
                            Intent login = new Intent(LocationRadarActivity.this, MainActivity.class);
                            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(login);
                        } else{
                            Toast.makeText(LocationRadarActivity.this, "Error fetching data, Please try again.", Toast.LENGTH_SHORT).show();
                        }
                        startActivity(new Intent(LocationRadarActivity.this, QRCodeActivity.class));
                    }
                });
            }
            else {
                Toast.makeText(this, "No internet!", Toast.LENGTH_SHORT).show();
                layer.setVisibility(View.INVISIBLE);
                loader.setVisibility(View.INVISIBLE);
                refresh.setEnabled(true);
                rippleBackground.stopRippleAnimation();
            }

        }
    }

    private List<Participant> getParticipantsFromResponse(String response) {
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(response).getAsJsonArray();
        List<Participant> participants = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++){
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            Participant participant = new Participant();
            double distance = jsonObject.get("distance").getAsDouble();
            String direction = jsonObject.get("direction").getAsString();
            participant.distance = (int)Math.round(distance) + " m " + direction;
            participants.add(participant);
        }
        return participants;
    }


    private void fetchNearestParticipants(final String accessToken, final VolleyCallback volleyCallback) {

        lat = Double.parseDouble(pref.getString("latitude", "28.5325"));
        lng = Double.parseDouble(pref.getString("longitude", "77.364"));

        String url = API.BASE + API.NEAREST_NEIGHBOUR + "?lat=" + lat + "&lng=" + lng;
        StringRequest sr = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, " Location Radar Activity onResponse: " + response);
                volleyCallback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " Location Radar Activity onErrorResponse: " + error.toString());
                if(error.networkResponse != null)
                    volleyCallback.onError(error.networkResponse.statusCode, new String(error.networkResponse.data));
                else
                    volleyCallback.onError(HttpStatus.SERVICE_UNAVAILABLE.value(), "");
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + accessToken);
                return params;
            }
        };

        LineUpApplication.getInstance().addToRequestQueue(sr);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLatestLocation();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    private void getLocation() {
        Log.e(TAG, "getLocation: ");
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.e(TAG, "lat: " + latitude + "; long: " + longitude);

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("latitude", String.valueOf(latitude));
                    editor.putString("longitude", String.valueOf(longitude));
                    lng = longitude;
                    lat = latitude;
                    editor.apply();
                    //sendLocation(lat, lng);
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NotNull String permissions[], @NotNull int[] grantResults) {
        switch (requestCode) {

            case GPS_PERMISSION_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLatestLocation();
                } else {
                    Toast.makeText(this, "Sorry, permission is not granted", Toast.LENGTH_SHORT).show();
                }
            }

            case REQUEST_CHECK_SETTINGS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationProvider provider = new LocationProvider(LocationRadarActivity.this, locationRequest);
                    provider.getLocation();
                    needPermission = provider.getLocation();
                    if (needPermission)
                        askPermission();
                } else {
                    Toast.makeText(this, "Sorry, location is not accurate!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

//    public void sendLocation(final double latitude, final double longitude) {
//        final String token = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("token", "token_no");
//
//        StringRequest locationReq = new StringRequest(Request.Method.PUT,API.BASE + API.LOCATION_SEND, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e(TAG, " onResponse: " + response);
//                updatePositions();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, " onErrorResponse: " + error.toString());
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams(){
//                Map<String,String> params = new HashMap<>();
//                params.put("lat", String.valueOf(latitude));
//                params.put("lng", String.valueOf(longitude));
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> params = new HashMap<>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
//                params.put("Authorization", "Bearer " + token);
//
//                return params;
//            }
//        };
//
//        LineUpApplication.getInstance().addToRequestQueue(locationReq);
//    }

    private void askPermission() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, GPS_PERMISSION_CODE);
        }
        else
            getLatestLocation();
    }

    private void getLatestLocation() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                LocationProvider provider = new LocationProvider(LocationRadarActivity.this, locationRequest);
                needPermission = provider.getLocation();
                if (needPermission)
                    askPermission();
                else
                    updatePositions();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {

                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(LocationRadarActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });


    }

}
