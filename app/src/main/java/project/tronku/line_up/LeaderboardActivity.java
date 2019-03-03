package project.tronku.line_up;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import org.json.JSONObject;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {

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

        LineUpApplication.getInstance().getRequestQueue().addRequestFinishedListener(new RequestQueue.RequestFinishedListener<JSONObject>() {
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
        });

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
