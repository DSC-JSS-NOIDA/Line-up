package project.tronku.line_up;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;

import java.util.HashMap;
import java.util.Map;

public class QRCodeScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;

    private static final String TAG = "QRScanActivity";
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }


    @Override
    public void handleResult(final Result rawResult) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        String accessToken = getAccessToken();
        if(accessToken == null){
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);
        }

        double lat, lng;
        lat = Double.parseDouble(pref.getString("latitude", "28.4245"));
        lng = Double.parseDouble(pref.getString("longitude", "28.4245"));

        validateQR(getApplicationContext(), rawResult.getText(), accessToken, lat, lng, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                Map<String, String> responseMap = new Gson().fromJson(response, new TypeToken<Map<String, String>>() {}.getType());
                boolean valid = Boolean.valueOf(responseMap.get("valid"));
                String msg = responseMap.get("message");
                builder.setMessage(msg);
                AlertDialog alert1 = builder.create();
                alert1.show();
            }

            @Override
            public void onError(int status, String error) {
                if(status == HttpStatus.UNAUTHORIZED.value()){
                    builder.setMessage("Please login to continue.");
                    pref.edit().clear();
                    Intent login = new Intent(QRCodeScanActivity.this, MainActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(login);
                } else {
                    builder.setMessage("Error scanning the code, please try again.");
                }
                AlertDialog alert1 = builder.create();
                alert1.show();
            }
        });
    }

    private void validateQR(Context applicationContext, final String scannedCode, final String accessToken, final double lat, final double lng, final VolleyCallback volleyCallback) {
        RequestQueue queue = Volley.newRequestQueue(applicationContext);
        StringRequest sr = new StringRequest(Request.Method.POST,API.BASE + API.VALIDATE_QR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, " QRScan Activity onResponse: " + response);
                volleyCallback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " QRScan Activity onErrorResponse: " + error.toString());
                if(error.networkResponse != null)
                    volleyCallback.onError(error.networkResponse.statusCode, new String(error.networkResponse.data));
                else
                    volleyCallback.onError(HttpStatus.SERVICE_UNAVAILABLE.value(), "");
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("code", scannedCode);
                params.put("lat", String.valueOf(lat));
                params.put("lng", String.valueOf(lng));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + accessToken);
                return params;
            }
        };
        queue.add(sr);

    }

    private String getAccessToken() {
        String token = null;
        if (pref.contains("token")) {
            token = pref.getString("token", "");
        }
        return token;
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }
}
