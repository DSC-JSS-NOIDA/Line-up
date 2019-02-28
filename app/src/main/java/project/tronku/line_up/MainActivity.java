package project.tronku.line_up;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import me.ibrahimsn.particle.ParticleView;
import project.tronku.line_up.login.LoginButton;
import project.tronku.line_up.login.LoginFragment;
import project.tronku.line_up.login.SignUpFragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static project.tronku.line_up.FlexibleFrameLayout.ORDER_LOGIN_STATE;
import static project.tronku.line_up.FlexibleFrameLayout.ORDER_SIGN_UP_STATE;

public class MainActivity extends AppCompatActivity {

    private FrameLayout loginFragment, signUpFragment;
    private FlexibleFrameLayout wrapper;
    private LoginButton button;
    private boolean isLogin = true;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = findViewById(android.R.id.content);
        loginFragment = findViewById(R.id.login_fragment);
        signUpFragment = findViewById(R.id.sign_up_fragment);
        wrapper = findViewById(R.id.wrapper);
        button = findViewById(R.id.button);

        LoginFragment topLoginFragment = new LoginFragment();
        SignUpFragment topSignUpFragment = new SignUpFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_fragment, topLoginFragment)
                .replace(R.id.sign_up_fragment, topSignUpFragment)
                .commit();

        loginFragment.setRotation(-90);

        button.setOnSignUpListener(topSignUpFragment);
        button.setOnLoginListener(topLoginFragment);

        button.setOnButtonSwitched(new OnButtonSwitchedListener() {
            @Override
            public void onButtonSwitched(boolean isLogin) {
                view.setBackgroundColor(ContextCompat.getColor(MainActivity.this,
                        isLogin ? R.color.firstPage : R.color.secondPage));
            }
        });

        loginFragment.setVisibility(INVISIBLE);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        loginFragment.setPivotX(loginFragment.getWidth() / 2);
        loginFragment.setPivotY(loginFragment.getHeight());
        signUpFragment.setPivotX(signUpFragment.getWidth() / 2);
        signUpFragment.setPivotY(signUpFragment.getHeight());
    }

    public void switchFragment(View v) {
        if (isLogin) {
            loginFragment.setVisibility(VISIBLE);
            loginFragment.animate().rotation(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    signUpFragment.setVisibility(INVISIBLE);
                    signUpFragment.setRotation(90);
                    wrapper.setDrawOrder(ORDER_LOGIN_STATE);
                }
            });
        } else {
            signUpFragment.setVisibility(VISIBLE);
            signUpFragment.animate().rotation(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    loginFragment.setVisibility(INVISIBLE);
                    loginFragment.setRotation(-90);
                    wrapper.setDrawOrder(ORDER_SIGN_UP_STATE);
                }
            });
        }

        isLogin = !isLogin;
        button.startAnimation();
    }

}
