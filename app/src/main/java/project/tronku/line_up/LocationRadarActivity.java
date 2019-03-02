package project.tronku.line_up;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.skyfishjy.library.RippleBackground;

public class LocationRadarActivity extends AppCompatActivity {

    private static final String TAG = "LocationRadarActivity";
    private ImageView player1, player2, player3, player4;
    private TextView dis1, dis2, dis3, dis4;
    private RippleBackground rippleBackground;
    private SharedPreferences pref;
    private CardView refresh;
    private double lat, lng;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_radar);

        rippleBackground = findViewById(R.id.ripple);
        refresh = findViewById(R.id.refresh);
        rippleBackground.startRippleAnimation();
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        player1 = findViewById(R.id.player1_img);
        player2 = findViewById(R.id.player2_img);
        player3 = findViewById(R.id.player3_img);
        player4 = findViewById(R.id.player4_img);

        dis1 = findViewById(R.id.player1_distance);
        dis2 = findViewById(R.id.player2_distance);
        dis3 = findViewById(R.id.player3_distance);
        dis4 = findViewById(R.id.player4_distance);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private class Participant {

        private String username;
        private int distance;
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (pref.contains("token")) {
            accessToken = pref.getString("token", "");
            final List<TextView> textViews = Arrays.asList(dis1, dis2, dis3, dis4);

            lat = Double.parseDouble(pref.getString("latitude", "28.4245"));
            lng = Double.parseDouble(pref.getString("longitude", "28.4245"));

            fetchNearestParticipants(getApplicationContext(), accessToken, lat, lng, new VolleyCallback(){
                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(String response) {
                    if(response != null){
                        List<Participant> participants = getParticipantsFromResponse(response);
                        for(int i = 0; i < Math.min(participants.size(), textViews.size()); i++){
                            TextView textView = textViews.get(i);
                            Participant participant = participants.get(i);
                            textView.setText(participant.distance + "m");
                        }
                    } else{
                        Toast.makeText(getApplicationContext(), "Error fetching data, Please try again.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }

                @Override
                public void onError(int status, String error) {
                    if(status == HttpStatus.UNAUTHORIZED.value()){
                        Toast.makeText(getApplicationContext(), "Please login to perform this action.", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(getApplicationContext(), "Error fetching data, Please try again.", Toast.LENGTH_SHORT).show();
                    }
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            });
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
            participant.distance = (int)Math.round(distance);
            participants.add(participant);
        }
        return participants;
    }


    private void fetchNearestParticipants(Context applicationContext, final String accessToken, final double lat, final double lng, final VolleyCallback volleyCallback) {

        RequestQueue queue = Volley.newRequestQueue(applicationContext);

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
        queue.add(sr);
    }


}
