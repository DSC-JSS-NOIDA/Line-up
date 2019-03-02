package project.tronku.line_up;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class SplashScreenActivity extends AppCompatActivity {

    private View lineb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final Intent i=new Intent(SplashScreenActivity.this,MainActivity.class);
        Thread timer=new Thread()
        {
            public void run()
            {
                try{
                    sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    startActivity(i);
                    finish();
                }
            }
        };
        timer.start();

        lineb = findViewById(R.id.line_b);
        ObjectAnimator animation_x1=ObjectAnimator.ofFloat(lineb,"translationX",500f, 0f);
        animation_x1.setInterpolator(new AccelerateInterpolator());
        animation_x1.setDuration(400);
        animation_x1.start();
        ObjectAnimator animation_x2=ObjectAnimator.ofFloat(findViewById(R.id.line_d),"translationX",-500f, 0f);
        animation_x2.setInterpolator(new AccelerateInterpolator());
        animation_x2.setDuration(400);
        animation_x2.start();
        ObjectAnimator animation_y1=ObjectAnimator.ofFloat(findViewById(R.id.line_a),"translationY",-500f, 0f);
        animation_y1.setInterpolator(new AccelerateInterpolator());
        animation_y1.setDuration(400);
        animation_y1.start();
        ObjectAnimator animation_y2=ObjectAnimator.ofFloat(findViewById(R.id.line_c),"translationY",500f, 0f);
        animation_y2.setInterpolator(new AccelerateInterpolator());
        animation_y2.setDuration(400);
        animation_y2.start();


        AlphaAnimation line=new AlphaAnimation(0.0f,1.0f);
        line.setStartOffset(50);
        line.setDuration(1500);
        line.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                findViewById(R.id.line).setVisibility(View.INVISIBLE);
                findViewById(R.id.up).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.line).setVisibility(View.VISIBLE);
                findViewById(R.id.up).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        findViewById(R.id.line).startAnimation(line);
        findViewById(R.id.up).startAnimation(line);


        lineb = findViewById(R.id.line_b);
        ObjectAnimator animation__x1=ObjectAnimator.ofFloat(lineb,"translationX",0f, 5500f);

        animation__x1.setStartDelay(1500);
        animation__x1.setDuration(1000);
        animation__x1.start();
        ObjectAnimator animation__x2=ObjectAnimator.ofFloat(findViewById(R.id.line_d),"translationX", 0f,-5500f);

        animation__x2.setDuration(1000);
        animation__x2.setStartDelay(1500);
        animation__x2.start();
        ObjectAnimator animation__y1=ObjectAnimator.ofFloat(findViewById(R.id.line_a),"translationY", 0f,-5500f);

        animation__y1.setDuration(1000);
        animation__y1.setStartDelay(1500);
        animation__y1.start();
        ObjectAnimator animation__y2=ObjectAnimator.ofFloat(findViewById(R.id.line_c),"translationY", 0f,5500f);

        animation__y2.setDuration(1000);
        animation__y2.setStartDelay(1500);
        animation__y2.start();


            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


//        AlphaAnimation end=new AlphaAnimation(1.0f,0.0f);
//        end.setStartTime(1700);
//        end.setDuration(1300);
//        end.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                findViewById(R.id.line).setVisibility(View.INVISIBLE);
//                findViewById(R.id.up).setVisibility(View.INVISIBLE);
//                findViewById(R.id.line_a).setVisibility(View.INVISIBLE);
//                findViewById(R.id.line_b).setVisibility(View.INVISIBLE);
//                findViewById(R.id.line_c).setVisibility(View.INVISIBLE);
//                findViewById(R.id.line_d).setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        findViewById(R.id.line).startAnimation(end);
//        findViewById(R.id.up).startAnimation(end);
//        findViewById(R.id.line_a).startAnimation(end);
//        findViewById(R.id.line_b).startAnimation(end);
//        findViewById(R.id.line_c).startAnimation(end);
//        findViewById(R.id.line_d).startAnimation(end);

    }
}
