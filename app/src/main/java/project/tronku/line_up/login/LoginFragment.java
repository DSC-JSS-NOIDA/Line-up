package project.tronku.line_up.login;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import project.tronku.line_up.API;
import project.tronku.line_up.InstructionsActivity;
import project.tronku.line_up.LineUpApplication;
import project.tronku.line_up.LocationFinder;
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
    private View layer;
    private ProgressBar loader;
    private SharedPreferences pref;
    private LocationFinder locationFinder;
    private LineUpApplication application;

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
        layer = inflate.findViewById(R.id.login_layer);
        loader = inflate.findViewById(R.id.login_loader);
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        locationFinder = new LocationFinder(getActivity());
        application = new LineUpApplication(locationFinder);
        return inflate;
    }

    @Override
    public void login() {

        zealid = zealIdEditText.getText().toString();
        password = passwordEditText.getText().toString();

        hideKeyboard();

        if (zealid.isEmpty() || password.isEmpty()) {
            Snackbar snackbar = Snackbar.make(inflate, "Enter details.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
            snackbar.show();
        }
        else {
            layer.setVisibility(View.VISIBLE);
            loader.setVisibility(View.VISIBLE);
            zealIdEditText.setEnabled(false);
            passwordEditText.setEnabled(false);

            loginUser(zealid, password);
        }
    }


    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    private void loginUser(final String zealid, final String password) {
        RequestQueue login;
//        JSONObject credentials = new JSONObject();
//        try{
//            credentials.put("username", zealid);
//            credentials.put("password", password);
//            credentials.put("grant_type", "password");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        JsonObjectRequest loginReq = new JsonObjectRequest(Request.Method.POST, API.BASE + API.LOGIN, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse: " + response.toString());
                        try {
                            String token = response.getString("access_token");
                            Log.e(TAG, "token" + token);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("token", token);
                            editor.apply();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse!=null){
                    String json = new String(error.networkResponse.data);
                    Log.e(TAG, "onErrorResponse: " + json);
                    try {
                        JSONObject jsonError = new JSONObject(json);
                        String errorString = jsonError.get("username").toString();
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
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                String credentials = API.USERNAME + ":" + API.PASSWORD;
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                Log.e(TAG, "getHeaders: " + auth);
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", zealid);
                params.put("password", password);
                params.put("grant_type", "password");
                return params;
            }
        };

        login = Volley.newRequestQueue(getContext());
        login.add(loginReq);
        login.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<JSONObject>() {
            @Override
            public void onRequestFinished(Request<JSONObject> request) {
                layer.setVisibility(View.INVISIBLE);
                loader.setVisibility(View.INVISIBLE);
                zealIdEditText.setEnabled(true);
                passwordEditText.setEnabled(true);

                if (firstTime) {
                    Intent intro = new Intent(getActivity(), InstructionsActivity.class);
                    startActivityForResult(intro, REQUEST_CODE_INTRO);
                }
                else {
                    Intent qrcode = new Intent(getActivity(), QRCodeActivity.class);
                    startActivity(qrcode);
                }
            }
        });
    }

}
