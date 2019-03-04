package project.tronku.line_up.timer;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonParser;
import com.google.zxing.qrcode.QRCodeReader;

import java.text.ParseException;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown_timer);

        daysView = findViewById(R.id.days);
        hoursView = findViewById(R.id.hours);
        minsView = findViewById(R.id.mins);
        secondsView = findViewById(R.id.seconds);
        //heading = findViewById(R.id.heading);


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

                    String headingText = "Starts in:";
                    long timeRemaining = 0L;

                    if(eventDetails.getStartTime().after(now)){
                        headingText = "Event starts in: ";
                        timeRemaining = eventDetails.getStartTime().getTime() - now.getTime();
                    } else if(eventDetails.getSignUpStartTime().after(now)){
                        headingText = "Sign Up starts in: ";
                        timeRemaining = eventDetails.getSignUpStartTime().getTime() - now.getTime();
                    }
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
                            Toast.makeText(getApplicationContext(), "Event has started.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CountDownTimerActivity.this, QRCodeActivity.class));
                        }
                    }.start();
                } catch (ParseException e) {
                    Toast.makeText(CountDownTimerActivity.this, "Error fetching data, Please try again.", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onError(int status, String error) {
                Toast.makeText(getApplicationContext(), "Error fetching data, Please try again.", Toast.LENGTH_SHORT).show();
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


}
