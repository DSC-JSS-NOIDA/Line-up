package project.tronku.line_up;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.jetbrains.annotations.NotNull;

public class QRCodeActivity extends AppCompatActivity {

    private ImageView myQRCode;
    private String uniqueCode;
    private View view;
    private SharedPreferences pref;
    private CardView scanQR, locate, leaderboard, route, logout;
    private NetworkReceiver receiver;
    public static final String TAG = "QRCodeActivty";
    private static final int CAMERA_PERMISSION_CODE = 2;
    public static final int GPS_PERMISSION_CODE = 3;
    private Intent service;

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
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        service = new Intent(this, LocationFinderService.class);

        uniqueCode = pref.getString("uniqueCode", "Data missing");
        receiver = new NetworkReceiver();

        startLocationService();

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(uniqueCode, BarcodeFormat.QR_CODE,1000,1000);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            myQRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QRCodeActivity.this, LeaderboardActivity.class));
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
                stopService(service);
                finishAffinity();
                startActivity(start);
            }
        });
    }

    private void startLocationService() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, GPS_PERMISSION_CODE);
        }
        else
            ContextCompat.startForegroundService(this, service);
    }

    public void Location(View view){
        Intent i = new Intent(QRCodeActivity.this,LocationRadarActivity.class);
        startActivity(i);
    }

    public void ContactUs(View view){
        Intent i=new Intent(QRCodeActivity.this,ContactUs.class);
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
                    startLocationService();
                } else {
                    Toast.makeText(this, "Sorry, permission is not granted", Toast.LENGTH_SHORT).show();
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
        snackbarView.setBackgroundColor(getResources().getColor(R.color.qr));
        snackbar.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
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
}
