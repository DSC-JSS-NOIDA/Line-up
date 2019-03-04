package project.tronku.line_up;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import me.ibrahimsn.particle.ParticleView;
import project.tronku.line_up.timer.CountDownTimerActivity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class QRCodeActivity extends AppCompatActivity {

    private ImageView myQRCode;
    private String uniqueCode;
    private View view;
    private SharedPreferences pref;
    private CardView scanQR, locate, leaderboard, route, logout;
    private NetworkReceiver receiver;

    public static final String TAG = "QRCodeActivty";
    private static final int REQUEST_CHECK_SETTINGS = 101;
    private static final int CAMERA_PERMISSION_CODE = 2;
    private static final int GPS_PERMISSION_CODE = 3;

    private PlayerPOJO currentUser;
    private ProgressBar loader;
    private LocationRequest locationRequest;
    private boolean needPermission = false;
//    private ImageView poke_top,poke_down;
    private ParticleView particleView;
//    private View layer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        view = findViewById(android.R.id.content);
        myQRCode = findViewById(R.id.my_qr);
        scanQR = findViewById(R.id.scan_qr);
        locate = findViewById(R.id.locate);
        route = findViewById(R.id.route);
        logout = findViewById(R.id.logout);
        leaderboard = findViewById(R.id.leaderboard);
        loader = findViewById(R.id.loader_qr);
        particleView = findViewById(R.id.particleView);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        receiver = new NetworkReceiver();

        askPermission();
        getLatestLocation();
        startLocationService();

        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QRCodeActivity.this, LeaderboardActivity.class));
//                Intent countDown = new Intent(QRCodeActivity.this, CountDownTimerActivity.class);
//                startActivity(countDown);

            }
        });

        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(QRCodeActivity.this, new String[] {Manifest.permission.CAMERA} , CAMERA_PERMISSION_CODE);
                }
                else
                    startActivity(new Intent(QRCodeActivity.this, QRCodeScanActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pref.edit().clear().apply();
                Intent start = new Intent(QRCodeActivity.this, MainActivity.class);
                finishAffinity();
                startActivity(start);
            }
        });

        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QRCodeActivity.this, YourRouteActivity.class));
            }
        });

    }

    private void startLocationService() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, GPS_PERMISSION_CODE);
        }
        else
            ContextCompat.startForegroundService(this, new Intent(this, LocationFinderService.class));
    }

    private void updateUniqueCode() {
        String accessToken = LineUpApplication.getInstance().getAccessToken();
        if(accessToken == null){
            Intent mainActivity = new Intent(this, MainActivity.class);
            LineUpApplication.getInstance().getDefaultSharedPreferences().edit().clear().apply();
            finishAffinity();
            startActivity(mainActivity);
        } else{
            if (receiver.isConnected()) {
                Helper.fetchUserInfo(accessToken, new VolleyCallback(){
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(String response) {
                        if(response != null){
                            currentUser = Helper.getPlayerFromJsonString(response);
                            uniqueCode = currentUser.getUniqueCode();
                            pref.edit().putString("uniqueCode", uniqueCode).apply();
                            updateQR();
                        } else{
                            Toast.makeText(QRCodeActivity.this, "Error fetching data, Please try again.", Toast.LENGTH_SHORT).show();
                        }
                        /*layer.setVisibility(View.INVISIBLE);
                        loader.setVisibility(View.INVISIBLE);*/
                    }

                    @Override
                    public void onError(int status, String error) {

                        if(status == HttpStatus.PRECONDITION_REQUIRED.value()){
                            startActivity(new Intent(QRCodeActivity.this, CountDownTimerActivity.class));
                        }else if(status == HttpStatus.UNAUTHORIZED.value()){
                            Toast.makeText(QRCodeActivity.this, "Please login to perform this action.", Toast.LENGTH_SHORT).show();
                            LineUpApplication.getInstance().getDefaultSharedPreferences().edit().clear().apply();
                            Intent login = new Intent(QRCodeActivity.this, MainActivity.class);
                            finishAffinity();
                            startActivity(login);
                        } else{
                            Toast.makeText(QRCodeActivity.this, "Error fetching data, Please try again.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(QRCodeActivity.this, MainActivity.class));
                        }
                    }
                });
            } else {
                Toast.makeText(this, "No internet!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void askPermission() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, GPS_PERMISSION_CODE);
        }

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
                LocationProvider provider = new LocationProvider(QRCodeActivity.this, locationRequest);
                needPermission = provider.getLocation();
                if (needPermission)
                    askPermission();
                else
                    sendLocation();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {

                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(QRCodeActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });


    }

    public void Location(View view){
        Intent i = new Intent(QRCodeActivity.this,LocationRadarActivity.class);
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NotNull String permissions[], @NotNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(QRCodeActivity.this, QRCodeScanActivity.class));
                } else {
                    Toast.makeText(this, "Sorry, permission is not granted", Toast.LENGTH_SHORT).show();
                }
            }

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
                    LocationProvider provider = new LocationProvider(QRCodeActivity.this, locationRequest);
                    provider.getLocation();
                    needPermission = provider.getLocation();
                    if (needPermission)
                        askPermission();
                    else
                        sendLocation();
                } else {
                    Toast.makeText(this, "Sorry, location is not accurate!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        final Snackbar snackbar = Snackbar.make(view, "Are you sure to exit?", Snackbar.LENGTH_LONG);
        snackbar.setAction("YES", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.route));
        snackbar.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);

        if(pref.contains("uniqueCode")){
            uniqueCode = pref.getString("uniqueCode", "Data missing");
            updateQR();
        } else{
            loader.setVisibility(View.VISIBLE);
            updateUniqueCode();
        }
    }


    private void updateQR(){
        loader.setVisibility(View.INVISIBLE);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(uniqueCode, BarcodeFormat.QR_CODE,1000,1000);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            myQRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    public void call_shubham(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:9643761192"));
        startActivity(intent);
    }
    public void mail_dsc(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto: dscjssnoida@gmail.com"));
        intent.putExtra(Intent.EXTRA_EMAIL, "dscjssnoida@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Queries");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    public void facebook(View view)
    {

        Uri uri = Uri.parse("http://facebook.com/dscjssnoida");
        Intent i= new Intent(Intent.ACTION_VIEW,uri);

        i.setPackage("com.facebook.katana");

        try{
            i=new Intent(Intent.ACTION_VIEW,Uri.parse("dscjssnoida"));
            startActivity(i);
        } catch (ActivityNotFoundException e) {

            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://facebook.com/dscjssnoida")));
        }

    }

    private void sendLocation() {
        final double latitude = Double.parseDouble(pref.getString("latitude", "28.3525"));
        final double longitude = Double.parseDouble(pref.getString("longitude", "77.35453"));
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

    @Override
    protected void onResume() {
        super.onResume();
        particleView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        particleView.pause();
    }

}
