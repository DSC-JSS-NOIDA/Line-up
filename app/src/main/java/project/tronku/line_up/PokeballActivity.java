package project.tronku.line_up;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

public class PokeballActivity extends AppCompatActivity {

    private ImageView top, bottom;
//    private View layer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokeball);

        top = findViewById(R.id.top_ball);
        bottom = findViewById(R.id.bottom_ball);
//        layer = findViewById(R.id.layer_pokemon);

        ObjectAnimator animation_y1=ObjectAnimator.ofFloat(top,"translationY",0f, -2500f);
        animation_y1.setStartDelay(700);
        animation_y1.setDuration(1200);
        animation_y1.start();

        ObjectAnimator animation_y2=ObjectAnimator.ofFloat(bottom,"translationY",0, 2500f);
        animation_y2.setStartDelay(700);
        animation_y2.setDuration(1200);
        animation_y2.start();

//        AlphaAnimation back=new AlphaAnimation(1.0f,0.0f);
//        back.setStartTime(500);
//        back.setDuration(2000);
//
//        back.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                layer.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                layer.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//
//        layer.startAnimation(back);

        final Intent qrcode = new Intent(this, QRCodeActivity.class);
        qrcode.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(qrcode);
                }
            }
        });
        thread.start();
    }
}
