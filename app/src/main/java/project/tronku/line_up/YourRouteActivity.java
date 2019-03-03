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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class YourRouteActivity extends AppCompatActivity {

    private static final String TAG = "YourRouteActivity";
    private View layer;
    private ProgressBar loader;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private NetworkReceiver receiver;

    private List<PlayerPOJO> teammatesFound = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_route);

        layer = findViewById(R.id.layer_route);
        loader = findViewById(R.id.loader_route);
        swipeRefreshLayout = findViewById(R.id.swipe_route);
        recyclerView = findViewById(R.id.route_recyclerview);
        receiver = new NetworkReceiver();

        updateRoute();

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
                            teammatesFound = new ArrayList<>(Helper.getPlayersFromResponse(response));
                            recyclerView.setLayoutManager(new LinearLayoutManager(YourRouteActivity.this));
                            //recyclerView.setAdapter(adapter);
                        } else{
                            Toast.makeText(getApplicationContext(), "Error fetching data, Please try again.", Toast.LENGTH_SHORT).show();
                        }
                        layer.setVisibility(View.INVISIBLE);
                        loader.setVisibility(View.INVISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshLayout.setEnabled(true);
                    }

                    @Override
                    public void onError(int status, String error) {
                        if(status == HttpStatus.UNAUTHORIZED.value()){
                            Toast.makeText(getApplicationContext(), "Please login to perform this action.", Toast.LENGTH_SHORT).show();
                            LineUpApplication.getInstance().getDefaultSharedPreferences().edit().clear().apply();
                            Intent login = new Intent(getApplicationContext(), MainActivity.class);
                            finishAffinity();
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
