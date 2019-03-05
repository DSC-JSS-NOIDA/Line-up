package project.tronku.line_up;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import me.ibrahimsn.particle.ParticleView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Map;

public class YourRouteActivity extends AppCompatActivity {

    private static final String TAG = "YourRouteActivity";
    private View layer;
    private ProgressBar loader, progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private NetworkReceiver receiver;
    private RouteAdapter adapter;
    private TextView noMembers;
    private SharedPreferences pref;
    private TextView score, position, name, zealid;
    private int myScore, myPosition, progress;
    private String myName, myZealId, teamCount;
    private ImageView pokemon;
    private ParticleView particleView;

    private ArrayList<PlayerPOJO> teammatesFound = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        layer = findViewById(R.id.layer_route);
        loader = findViewById(R.id.loader_route);
        swipeRefreshLayout = findViewById(R.id.swipe_route);
        recyclerView = findViewById(R.id.route_recyclerview);
        noMembers  = findViewById(R.id.no_members);
        score = findViewById(R.id.score);
        position = findViewById(R.id.position);
        name = findViewById(R.id.player_name);
        zealid = findViewById(R.id.player_zealid);
        progressBar = findViewById(R.id.progress_member);
        pokemon = findViewById(R.id.pokemon_img);
        particleView = findViewById(R.id.particleView);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        receiver = new NetworkReceiver();
        adapter = new RouteAdapter(this, teammatesFound);

        updateRoute();
        updatePokemon();

        myScore = pref.getInt("score", 0);
        myPosition = pref.getInt("position", 0);
        myName = pref.getString("name", "Player Name");
        myZealId = pref.getString("zealid", "Zeal id");
        teamCount = pref.getString("teamCount", "0");
        progress = pref.getInt("progress", 0);

        score.setText("" + myScore);
        position.setText("" + myPosition);
        name.setText(myName);
        zealid.setText(myZealId);
        progressBar.setProgress(progress* 100 / Integer.parseInt(teamCount));


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

    private void updatePokemon() {
        String pokemonCode = pref.getString("avatar", "a");
        switch (pokemonCode) {
            case "a" : pokemon.setImageResource(R.drawable.pokemona);break;
            case "b" : pokemon.setImageResource(R.drawable.pokemonb);break;
            case "c" : pokemon.setImageResource(R.drawable.pokemonc);break;
            case "d" : pokemon.setImageResource(R.drawable.pokemond);break;
            case "e" : pokemon.setImageResource(R.drawable.pokemone);break;
            case "f" : pokemon.setImageResource(R.drawable.pokemonf);break;
        }
        Log.e(TAG, "updatePokemon: " + pokemonCode);
    }

    private void updateRoute() {
        layer.setVisibility(View.VISIBLE);
        loader.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setEnabled(false);
        recyclerView.setVisibility(View.INVISIBLE);

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
                            myName = responseMap.get("firstName").toString();
                            myZealId = responseMap.get("zeal_id").toString();

                            Log.e(TAG, "onSuccess: " + myScore + " " + myPosition);
                            pref.edit().putInt("score", 0).apply();
                            pref.edit().putInt("position", 0).apply();
                            pref.edit().putString("name", "Player Name").apply();
                            pref.edit().putString("zealid", "Zeal id").apply();

                            score.setText("" + myScore);
                            position.setText("" + myPosition);
                            name.setText(myName);
                            zealid.setText(myZealId);

                            teammatesFound = new ArrayList<>(Helper.getPlayersFromResponse(response));
                            progress = teammatesFound.size();
                            pref.edit().putInt("progress", progress).apply();
                            progressBar.setProgress(progress*(100/ Integer.parseInt(teamCount)));

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
                        recyclerView.setVisibility(View.VISIBLE);
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

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                particleView.resume();
            }
        }, 4000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        particleView.pause();
    }
}
