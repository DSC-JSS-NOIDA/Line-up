package project.tronku.line_up.login;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import project.tronku.line_up.API;
import project.tronku.line_up.InstructionsActivity;
import project.tronku.line_up.MainActivity;
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
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("intro", false).apply();
                Intent qrcode = new Intent(getActivity(), QRCodeActivity.class);
                startActivity(qrcode);
            } else {
                //Toast
            }
        }
    }



}
