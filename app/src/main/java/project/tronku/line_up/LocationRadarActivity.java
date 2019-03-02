package project.tronku.line_up;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class LocationRadarActivity extends AppCompatActivity {

    private ImageView player1, player2, player3, player4;
    private TextView dis1, dis2, dis3, dis4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_radar);

        player1 = findViewById(R.id.player1_img);
        player2 = findViewById(R.id.player2_img);
        player3 = findViewById(R.id.player3_img);
        player4 = findViewById(R.id.player4_img);

        dis1 = findViewById(R.id.player1_distance);
        dis2 = findViewById(R.id.player2_distance);
        dis3 = findViewById(R.id.player3_distance);
        dis4 = findViewById(R.id.player4_distance);

    }

}
