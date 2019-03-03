package project.tronku.line_up;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
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
    private String zealid;
    private View view;
    private SharedPreferences pref;
    private CardView scanQR, locate, leaderboard, route, logout;
    private NetworkReceiver receiver;
    public static final String TAG = "QRCodeActivty";
    private static final int CAMERA_PERMISSION_CODE = 2;

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

        zealid = pref.getString("zealid", "zo1241");
        receiver = new NetworkReceiver();

        Intent service = new Intent(this, LocationFinderService.class);
        ContextCompat.startForegroundService(this, service);
        Log.e(TAG, "onCreate: ");

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(zealid, BarcodeFormat.QR_CODE,1000,1000);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            myQRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

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
}
