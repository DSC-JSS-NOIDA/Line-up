package project.tronku.line_up;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;


public class InstructionsActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullscreen(true);

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
                .description("Sometimes, the app may show approximate location instead of the accurate one. So, kindly recentre your location using Google Maps for more accuracy.")
                .image(R.drawable.alert)
                .background(R.color.alert)
                .scrollable(false)
                .build());

    }

}
