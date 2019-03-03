package project.tronku.line_up;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class YourRouteActivity extends AppCompatActivity {

    private View layer;
    private ProgressBar loader;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private NetworkReceiver receiver;


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
