package project.tronku.line_up;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class ContactUs extends AppCompatActivity {

    private View layout1,layout2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        layout1=findViewById(R.id.layout);
//        layout2=findViewById(R.id.layout_2);
//        layout2.setVisibility(layout2.INVISIBLE);
//


//        AlphaAnimation end=new AlphaAnimation(0.0f,1.0f);
//        end.setStartTime(1000);
//        end.setDuration(0);
//        end.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                layout2.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        layout2.startAnimation(end);



        ObjectAnimator animation_x1=ObjectAnimator.ofFloat(layout1,"translationX",-5000f, 0f);
        animation_x1.setDuration(1000);

        animation_x1.start();
//
//        ObjectAnimator animation_x2=ObjectAnimator.ofFloat(layout2,"translationX",-5000f, 0f);
//        animation_x2.getStartDelay(1000)
//        animation_x2.setDuration(1000);
//
//        animation_x2.start();
//

    }
    public void call_shubham(View view) {

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:9643761192"));
        startActivity(intent);
    }
    public void mail_shubham(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto: shubham.pathak2000@gmail.com"));
        intent.putExtra(Intent.EXTRA_EMAIL, "shubham.payhak2000@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Queries");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    public void call_dheeraj(View view) {

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:8604609572"));
        startActivity(intent);
    }
    public void mail_dheeraj(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto: dheeraj.kotwani41@gmail.com"));
        intent.putExtra(Intent.EXTRA_EMAIL, "dheeraj.kotwani41@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Queries");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
