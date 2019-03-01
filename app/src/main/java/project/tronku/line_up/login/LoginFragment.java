package project.tronku.line_up.login;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import project.tronku.line_up.InstructionsActivity;
import project.tronku.line_up.QRCodeActivity;
import project.tronku.line_up.R;

import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

public class LoginFragment extends Fragment implements project.tronku.line_up.login.OnLoginListener {
    private static final String TAG = "LoginFragment";
    private static final int REQUEST_CODE_INTRO = 1;
    private boolean firstTime;
    private EditText zealIdEditText, passwordEditText;
    private String zealid, password;
    private View inflate;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_login, container, false);

        firstTime = PreferenceManager.getDefaultSharedPreferences(getActivity())
        .getBoolean("intro", true);

        zealIdEditText = inflate.findViewById(R.id.zeal_id_login);
        passwordEditText = inflate.findViewById(R.id.password_login);
        return inflate;
    }

    @Override
    public void login() {

        zealid = zealIdEditText.getText().toString();
        password = passwordEditText.getText().toString();

        if (zealid.isEmpty() || password.isEmpty()) {
            Snackbar snackbar = Snackbar.make(inflate, "Enter details.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
            snackbar.show();
        }
        else {
            if (firstTime) {
                Intent intro = new Intent(getActivity(), InstructionsActivity.class);
                startActivityForResult(intro, REQUEST_CODE_INTRO);
            }
            else {
                Intent qrcode = new Intent(getActivity(), QRCodeActivity.class);
                startActivity(qrcode);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getActivity(), "Welcome!", Toast.LENGTH_SHORT).show();
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("intro", false).apply();
            } else {
                //Toast
            }
        }
    }
}
