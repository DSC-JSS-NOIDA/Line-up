package project.tronku.line_up;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import project.tronku.line_up.login.LoginButton;
import project.tronku.line_up.login.LoginFragment;
import project.tronku.line_up.login.SignUpFragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;

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
    private EditText password;
    private ImageView visible, invisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = findViewById(android.R.id.content);
        loginFragment = findViewById(R.id.login_fragment);
        signUpFragment = findViewById(R.id.sign_up_fragment);
        wrapper = findViewById(R.id.wrapper);
        button = findViewById(R.id.button);
        visible = findViewById(R.id.visible);
        invisible = findViewById(R.id.invisible);
        password=findViewById(R.id.password_login);
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

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
                view.setBackgroundColor(ContextCompat.getColor(
                        MainActivity.this,
                        isLogin ? R.color.colorPrimary : R.color.secondPage));
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

//    public void view_invisible(View view){
//        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//        findViewById(R.id.invisible).setVisibility(INVISIBLE);
//        findViewById(R.id.visible).setVisibility(VISIBLE);
//    }
//    public void view_visible(View view){
//      password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
//        findViewById(R.id.visible).setVisibility(INVISIBLE);
//        findViewById(R.id.invisible).setVisibility(VISIBLE);
//    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (pref.contains("token")) {
            Intent qrcode = new Intent(this, QRCodeActivity.class);
            startActivity(qrcode);
        }
    }



    @Override
    public void onBackPressed() {
        final Snackbar snackbar = Snackbar.make(view, "Are you sure to exit?", Snackbar.LENGTH_LONG);
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
