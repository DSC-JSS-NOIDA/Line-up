package project.tronku.line_up;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LeaderboardActivity extends AppCompatActivity {

    private static final String TAG = "LeaderboardActivity";
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View layer;
    private ProgressBar loader;
    private NetworkReceiver receiver;
    private ArrayList<PlayerPOJO> players = new ArrayList<>();
    private LeaderboardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        recyclerView = findViewById(R.id.leaderboard_recyclerview);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        layer = findViewById(R.id.layer_leaderboard);
        loader = findViewById(R.id.loader_leaderboard);
        receiver = new NetworkReceiver();

        adapter = new LeaderboardAdapter(this, players);
        updateList();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (receiver.isConnected()) {
                    updateList();
                }
                else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(LeaderboardActivity.this, "No internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateList() {
        layer.setVisibility(View.VISIBLE);
        loader.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setEnabled(false);

        players.clear();

        //call API here

        updateLeaderboard();



       /* LineUpApplication.getInstance().getRequestQueue().addRequestFinishedListener(new RequestQueue.RequestFinishedListener<JSONObject>() {
            @Override
            public void onRequestFinished(Request<JSONObject> request) {
                adapter.updateList(players);
                layer.setVisibility(View.INVISIBLE);
                loader.setVisibility(View.INVISIBLE);
                recyclerView.setLayoutManager(new LinearLayoutManager(LeaderboardActivity.this));
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(true);
            }
        });*/

    }

    private void updateLeaderboard() {
        String accessToken = LineUpApplication.getInstance().getAccessToken();
        if(accessToken == null){
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);
        } else{
            if (receiver.isConnected()) {
                fetchLeaderboard(accessToken, new VolleyCallback(){
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(String response) {
                        if(response != null){
                            players = new ArrayList<>(getPlayersFromResponse(response));
                        } else{
                            Toast.makeText(getApplicationContext(), "Error fetching data, Please try again.", Toast.LENGTH_SHORT).show();
                        }
                        layer.setVisibility(View.INVISIBLE);
                        loader.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(int status, String error) {
                        if(status == HttpStatus.UNAUTHORIZED.value()){
                            Toast.makeText(getApplicationContext(), "Please login to perform this action.", Toast.LENGTH_SHORT).show();
                            LineUpApplication.getInstance().getDefaultSharedPreferences().edit().clear().apply();
                            Intent login = new Intent(getApplicationContext(), MainActivity.class);
                            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(login);
                        } else{
                            Toast.makeText(getApplicationContext(), "Error fetching data, Please try again.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    }
                });
            } else {
                Toast.makeText(this, "No internet!", Toast.LENGTH_SHORT).show();
                layer.setVisibility(View.INVISIBLE);
                loader.setVisibility(View.INVISIBLE);
            }
        }

    }

    private List<PlayerPOJO> getPlayersFromResponse(String response) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(response).getAsJsonObject();
        JsonArray jsonArray = jsonObject.get("users").getAsJsonArray();
        List<PlayerPOJO> playerPOJOList = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++){
            JsonObject user = jsonArray.get(i).getAsJsonObject();

            PlayerPOJO playerPOJO = new PlayerPOJO();
            playerPOJO.setName(user.get("firstName") instanceof JsonNull ? ""  : user.get("firstName").getAsString());
            playerPOJO.setScore(user.get("score").getAsString());
            playerPOJO.setPosition(user.get("position").getAsString());
            playerPOJO.setZealId(user.get("zeal_id").getAsString());
            long millis = user.get("totalTimeTaken").getAsLong() / 1000;
            playerPOJO.setTimeTaken(String.format(Locale.UK, "%d:%02d:%02d", millis / 3600, (millis % 3600) / 60, (millis % 60)));

            playerPOJOList.add(playerPOJO);
        }
        return playerPOJOList;
    }

    private void fetchLeaderboard(final String accessToken, final VolleyCallback volleyCallback) {
        String url = API.BASE + API.LEADERBOARD ;
        StringRequest sr = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, " Leaderboard Activity onResponse: " + response);
                volleyCallback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " Leaderboard Activity onErrorResponse: " + error.toString());
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
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }
}
