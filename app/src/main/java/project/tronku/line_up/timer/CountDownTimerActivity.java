package project.tronku.line_up.timer;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonParser;
import com.google.zxing.qrcode.QRCodeReader;

import java.text.ParseException;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import project.tronku.line_up.Constants;
import project.tronku.line_up.EventDetails;
import project.tronku.line_up.Helper;
import project.tronku.line_up.MainActivity;
import project.tronku.line_up.QRCodeActivity;
import project.tronku.line_up.QRCodeScanActivity;
import project.tronku.line_up.R;
import project.tronku.line_up.VolleyCallback;

public class CountDownTimerActivity extends AppCompatActivity {

    private TextView daysView;
    private TextView hoursView;
    private TextView minsView;
    private TextView secondsView;
    private TextView heading;

    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown_timer);

        view = findViewById(android.R.id.content);


        daysView = findViewById(R.id.days);
        hoursView = findViewById(R.id.hours);
        minsView = findViewById(R.id.mins);
        secondsView = findViewById(R.id.seconds);
        heading = findViewById(R.id.heading);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Helper.fetchEventDetails(new VolleyCallback() {
            @Override
            public void onSuccess(String response) {

                try {
                    EventDetails eventDetails = Helper.getEventDetailsFromJsonResponse(response);
                    Date now = new Date();

                    long timeRemaining = 0L;
                    String headingText = "Game starts in: ";
                    int temp = -1;
                    if(eventDetails.getSignUpStartTime().after(now)){
                        temp = 1;
                        headingText = "Sign Up starts in: ";
                        timeRemaining = eventDetails.getSignUpStartTime().getTime() - now.getTime();
                    } else if(eventDetails.getStartTime().after(now)){
                        temp = 0;
                        headingText = "Game starts in: ";
                        timeRemaining = eventDetails.getStartTime().getTime() - now.getTime();
                    }
                    final int flag = temp;
                    heading.setText(String.valueOf(headingText));
                    new CountDownTimer(timeRemaining, 1000) {

                        public void onTick(long millisUntilFinished) {
                            int[] time = getTimeRemaining(millisUntilFinished);
                            daysView.setText(String.valueOf(time[0]));
                            hoursView.setText(String.valueOf(time[1]));
                            minsView.setText(String.valueOf(time[2]));
                            secondsView.setText(String.valueOf(time[3]));
                        }

                        public void onFinish() {
                            if(flag == 1){
                                Toast.makeText(getApplicationContext(), "Sign Ups have started.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CountDownTimerActivity.this, MainActivity.class));
                            } else if(flag == 0){
                                Toast.makeText(getApplicationContext(), "Game has started.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CountDownTimerActivity.this, QRCodeActivity.class));
                            } else{
                                Toast.makeText(getApplicationContext(), Constants.ERROR_FETCHING_DATA, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CountDownTimerActivity.this, MainActivity.class));
                            }

                        }
                    }.start();
                } catch (ParseException e) {
                    Toast.makeText(CountDownTimerActivity.this, Constants.ERROR_FETCHING_DATA, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CountDownTimerActivity.this, MainActivity.class));
                }


            }

            @Override
            public void onError(int status, String error) {
                Toast.makeText(getApplicationContext(), Constants.ERROR_FETCHING_DATA, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CountDownTimerActivity.this, MainActivity.class));
            }
        });

    }

    private int[] getTimeRemaining(long timeInMillis) {
       int [] time = new int[4];
        time[3] = (int) Math.floor((timeInMillis / 1000.0) % 60);
        time[2] = (int) Math.floor((timeInMillis / 1000.0 / 60) % 60);
        time[1] = (int) Math.floor((timeInMillis / (1000.0 * 60 * 60)) % 24);
        time[0] = (int) Math.floor(timeInMillis / (1000.0 * 60 * 60 * 24));
        return time;
    }

    @Override
    public void onBackPressed() {
        final Snackbar snackbar = Snackbar.make(view, "Are you sure you want to exit?", Snackbar.LENGTH_LONG);
        snackbar.setAction("YES", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.route));
        snackbar.show();
    }

}
