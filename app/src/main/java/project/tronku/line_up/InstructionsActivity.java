package project.tronku.line_up;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

import java.util.Random;

import project.tronku.line_up.timer.CountDownTimerActivity;


public class InstructionsActivity extends IntroActivity {

    private NetworkReceiver receiver;
    private String uniqueCode, name, teamCount;
    private SharedPreferences pref;
    private PlayerPOJO currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullscreen(true);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        receiver = new NetworkReceiver();

        setRandomPokemon();

        addSlide(new SimpleSlide.Builder()
                .title("Grant Permission")
                .permissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA})
                .description("The application wants to access your laction and other data. Don't worry it will be safe with us")
                .image(R.drawable.ic_security)
                .background(R.color.grant)
                .scrollable(false)
                .canGoBackward(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Location")
                .description("The application will show the distance of four nearest players. Go and check if they are your teammates or not.")
                .image(R.drawable.route_intro)
                .background(R.color.location)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("QR Code")
                .description("You have to scan the QR code of the players. If you find one of your teammates, your points will increase otherwise keep on finding your teammates.")
                .image(R.drawable.ic_qr_big)
                .background(R.color.qr)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Alert")
                .description("Sometimes, the app may show approximate location instead of the accurate one. So, kindly re-centre your location using Google Maps for more accuracy.")
                .image(R.drawable.alert)
                .background(R.color.alert)
                .scrollable(false)
                .build());

    }

    private void setRandomPokemon() {
        Random random = new Random();
        int i = random.nextInt(6);
        switch (i) {
            case 0 : pref.edit().putString("avatar", "a").apply();break;
            case 1 : pref.edit().putString("avatar", "b").apply();break;
            case 2 : pref.edit().putString("avatar", "c").apply();break;
            case 3 : pref.edit().putString("avatar", "d").apply();break;
            case 4 : pref.edit().putString("avatar", "e").apply();break;
            case 5 : pref.edit().putString("avatar", "f").apply();break;
        }
        Log.e("pokemon", "setRandomPokemon: " + i);
    }

    private void updateUniqueCode() {
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
                            currentUser = Helper.getPlayerFromJsonString(response);
                            uniqueCode = currentUser.getUniqueCode();
                            name = currentUser.getName();
                            teamCount = String.valueOf(currentUser.getTeamCount());
                            pref.edit().putString("uniqueCode", uniqueCode).apply();
                            Log.e("CODE", "onSuccess: " + uniqueCode);
                            pref.edit().putString("name", name).apply();
                            pref.edit().putString("teamCount", teamCount).apply();

                        } else{
                            Toast.makeText(InstructionsActivity.this, "Error fetching data, Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(int status, String error) {

                        if(status == HttpStatus.PRECONDITION_REQUIRED.value()){
                            startActivity(new Intent(InstructionsActivity.this, CountDownTimerActivity.class));
                        }else if(status == HttpStatus.UNAUTHORIZED.value()){
                            pref.edit().clear().apply();
                            Toast.makeText(InstructionsActivity.this, "Please login to perform this action.", Toast.LENGTH_SHORT).show();
                            LineUpApplication.getInstance().getDefaultSharedPreferences().edit().clear().apply();
                            Intent login = new Intent(InstructionsActivity.this, MainActivity.class);
                            finishAffinity();
                            startActivity(login);
                        } else{
                            Toast.makeText(InstructionsActivity.this, Constants.ERROR_FETCHING_DATA, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(InstructionsActivity.this, MainActivity.class));
                        }
                    }
                });
            } else {
                Toast.makeText(this, "No internet!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
        updateUniqueCode();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }
}
