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
                .description("Description........")
                .image(R.drawable.ic_security)
                .background(R.color.grant)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("QR Code")
                .description("Description........")
                .image(R.drawable.ic_qr_big)
                .background(R.color.qr)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Location")
                .description("Description........")
                .image(R.drawable.ic_route)
                .background(R.color.location)
                .scrollable(false)
                .build());

    }

}
