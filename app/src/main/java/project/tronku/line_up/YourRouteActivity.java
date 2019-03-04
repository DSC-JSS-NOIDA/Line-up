package project.tronku.line_up;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YourRouteActivity extends AppCompatActivity {

    private static final String TAG = "YourRouteActivity";
    private View layer;
    private ProgressBar loader;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private NetworkReceiver receiver;
    private RouteAdapter adapter;
    private TextView noMembers;
    private SharedPreferences pref;
    private TextView score;
    private int myScore, myPosition;

    private ArrayList<PlayerPOJO> teammatesFound = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_route);

        layer = findViewById(R.id.layer_route);
        loader = findViewById(R.id.loader_route);
        swipeRefreshLayout = findViewById(R.id.swipe_route);
        recyclerView = findViewById(R.id.route_recyclerview);
        noMembers  = findViewById(R.id.no_members);
        score = findViewById(R.id.score_pos);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        receiver = new NetworkReceiver();
        adapter = new RouteAdapter(this, teammatesFound);

        updateRoute();

        myScore = pref.getInt("score", 0);
        myPosition = pref.getInt("position", 0);

        score.setText(getString(R.string.scorepos, myScore, myPosition));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (receiver.isConnected()) {
                    updateRoute();
                }
                else {
                    Toast.makeText(YourRouteActivity.this, "No internet!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void updateRoute() {
        layer.setVisibility(View.VISIBLE);
        loader.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setEnabled(false);

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
                            Map<String, Object> responseMap = new Gson().fromJson(response, new TypeToken<Map<String, Object>>() {}.getType());
                            myScore = (int) Double.parseDouble(responseMap.get("score").toString());
                            myPosition = (int) Double.parseDouble(responseMap.get("position").toString());
                            Log.e(TAG, "onSuccess: " + myScore + " " + myPosition);
                            pref.edit().putInt("score", 0).apply();
                            pref.edit().putInt("position", 0).apply();
                            score.setText(getString(R.string.scorepos, myScore, myPosition));
                            teammatesFound = new ArrayList<>(Helper.getPlayersFromResponse(response));
                            if (teammatesFound.size() == 0) {
                                noMembers.setVisibility(View.VISIBLE);
                                Toast.makeText(YourRouteActivity.this, "No teammates found!", Toast.LENGTH_SHORT).show();
                            } else {
                                noMembers.setVisibility(View.INVISIBLE);
                                adapter.updateList(teammatesFound);
                                recyclerView.setLayoutManager(new LinearLayoutManager(YourRouteActivity.this));
                                recyclerView.setAdapter(adapter);
                            }
                        } else{
                            Toast.makeText(YourRouteActivity.this, "Error fetching data, Please try again.", Toast.LENGTH_SHORT).show();
                        }
                        layer.setVisibility(View.INVISIBLE);
                        loader.setVisibility(View.INVISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshLayout.setEnabled(true);
                    }

                    @Override
                    public void onError(int status, String error) {
                        if(status == HttpStatus.UNAUTHORIZED.value()){
                            Toast.makeText(YourRouteActivity.this, "Please login to perform this action.", Toast.LENGTH_SHORT).show();
                            LineUpApplication.getInstance().getDefaultSharedPreferences().edit().clear().apply();
                            Intent login = new Intent(YourRouteActivity.this, MainActivity.class);
                            finishAffinity();
                            startActivity(login);
                        } else{
                            Toast.makeText(YourRouteActivity.this, "Error fetching data, Please try again.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(YourRouteActivity.this, MainActivity.class));
                        }
                    }
                });
            } else {
                Toast.makeText(this, "No internet!", Toast.LENGTH_SHORT).show();
                layer.setVisibility(View.INVISIBLE);
                loader.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setEnabled(true);
            }
        }

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
