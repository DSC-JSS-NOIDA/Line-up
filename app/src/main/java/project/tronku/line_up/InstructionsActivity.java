package project.tronku.line_up;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

import android.os.Bundle;

public class InstructionsActivity extends MaterialIntroActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.grant)
                .image(R.drawable.permission)
                .title("Grant access")
                .description("Grant access to app to get location")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.location)
                .image(R.drawable.location)
                .title("GPS Access")
                .description("Grant access to app to get location")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.qr)
                .image(R.drawable.qr_code)
                .title("QR Code scan")
                .description("Grant access to app to get location")
                .build());

    }

}
