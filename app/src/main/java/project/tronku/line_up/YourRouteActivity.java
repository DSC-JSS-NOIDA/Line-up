package project.tronku.line_up;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class YourRouteActivity extends AppCompatActivity {

    private View layer;
    private ProgressBar loader;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_route);

        layer = findViewById(R.id.layer_route);
        loader = findViewById(R.id.loader_route);
        swipeRefreshLayout = findViewById(R.id.swipe_route);
        recyclerView = findViewById(R.id.route_recyclerview);


    }
}
