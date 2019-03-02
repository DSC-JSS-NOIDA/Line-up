package project.tronku.line_up.login;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import project.tronku.line_up.API;
import project.tronku.line_up.InstructionsActivity;
import project.tronku.line_up.MainActivity;
import project.tronku.line_up.QRCodeActivity;
import project.tronku.line_up.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class SignUpFragment extends Fragment implements OnSignUpListener {
    private static final String TAG = "SignUpFragment";
    private View inflate;
    private EditText nameEditText, phoneEditText, zealIdEditText, passwordEditText;
    private String name, phone, zealid, password;
    private View layer;
    private ProgressBar loader;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_signup, container, false);

        nameEditText = inflate.findViewById(R.id.name_signup);
        phoneEditText = inflate.findViewById(R.id.phone_signup);
        zealIdEditText = inflate.findViewById(R.id.zeal_id_signup);
        passwordEditText = inflate.findViewById(R.id.password_signup);
        layer = inflate.findViewById(R.id.signup_layer);
        loader = inflate.findViewById(R.id.signup_loader);

        return inflate;
    }

    @Override
    public void signUp() {
        name = nameEditText.getText().toString();
        phone = phoneEditText.getText().toString();
        zealid = zealIdEditText.getText().toString();
        password = passwordEditText.getText().toString();

        hideKeyboard();

        if (name.isEmpty() || phone.isEmpty() || zealid.isEmpty() || password.isEmpty()) {
            Snackbar snackbar = Snackbar.make(inflate, "Enter details.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.qr));
            snackbar.show();
        }
        else if (!Patterns.PHONE.matcher(phone).matches()) {
            Snackbar snackbar = Snackbar.make(inflate, "Enter valid number.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.qr));
            snackbar.show();
        }
        else if (password.length() < 6 || password.length() > 18) {
            Snackbar snackbar = Snackbar.make(inflate, "Password length should be between 6 and 18.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.qr));
            snackbar.show();
        }
        else {
            layer.setVisibility(View.VISIBLE);
            loader.setVisibility(View.VISIBLE);
            zealIdEditText.setEnabled(false);
            passwordEditText.setEnabled(false);
            nameEditText.setEnabled(false);
            phoneEditText.setEnabled(false);
            signUpUser(name, phone, zealid, password);
        }
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void signUpUser(String name, final String phone, String zealid, String password) {
        RequestQueue signUp;
        JSONObject credentials = new JSONObject();
        try{
            credentials.put("username", zealid);
            credentials.put("password", password);
            credentials.put("matchingPassword", password);
            credentials.put("firstName", name);
            credentials.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest signUpReq = new JsonObjectRequest(Request.Method.POST, API.BASE + API.SIGN_UP, credentials,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse: " + response.toString());
                        Snackbar snackbar = Snackbar.make(inflate, "Registered successfully! Please log in.", Snackbar.LENGTH_SHORT);
                        View snackbarView = snackbar.getView();
                        snackbarView.setBackgroundColor(getResources().getColor(R.color.green));
                        snackbar.show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse!=null){
                    String json = new String(error.networkResponse.data);
                    Log.e(TAG, "onErrorResponse: " + json);
                    try {
                        JSONObject jsonError = new JSONObject(json);
                        String errorString = jsonError.get("username").toString() + jsonError.get("password").toString();
                        final Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.dialog_layout);
                        ImageView close = dialog.findViewById(R.id.close);
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        TextView errorView = dialog.findViewById(R.id.errorText);
                        errorView.setText(errorString);
                        dialog.show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        signUp = Volley.newRequestQueue(getContext());
        signUp.add(signUpReq);
        signUp.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<JSONObject>() {
            @Override
            public void onRequestFinished(Request<JSONObject> request) {
                layer.setVisibility(View.INVISIBLE);
                loader.setVisibility(View.INVISIBLE);
                zealIdEditText.setEnabled(true);
                passwordEditText.setEnabled(true);
                phoneEditText.setEnabled(true);
                nameEditText.setEnabled(true);
            }
        });
    }

}
