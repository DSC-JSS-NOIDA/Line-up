package project.tronku.line_up.timer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import project.tronku.line_up.R;

public class CountDownTimerActivity extends AppCompatActivity {

    private TextView daysView;
    private TextView hoursView;
    private TextView minsView;
    private TextView secondsView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown_timer);

        daysView = findViewById(R.id.days);
        hoursView = findViewById(R.id.hours);
        minsView = findViewById(R.id.mins);
        secondsView = findViewById(R.id.seconds);


        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                int[] time = getTimeRemaining(millisUntilFinished);
                daysView.setText(String.valueOf(time[0]));
                hoursView.setText(String.valueOf(time[1]));
                minsView.setText(String.valueOf(time[2]));
                secondsView.setText(String.valueOf(time[3]));
            }

            public void onFinish() {

            }
        }.start();


    }

    private int[] getTimeRemaining(long timeInMillis) {
       int [] time = new int[4];
        time[3] = (int) Math.floor((timeInMillis / 1000.0) % 60);
        time[2] = (int) Math.floor((timeInMillis / 1000.0 / 60) % 60);
        time[1] = (int) Math.floor((timeInMillis / (1000.0 * 60 * 60)) % 24);
        time[0] = (int) Math.floor(timeInMillis / (1000.0 * 60 * 60 * 24));
        return time;
    }


}
