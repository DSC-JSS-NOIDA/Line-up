package project.tronku.line_up.login;

import android.app.Dialog;
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

import project.tronku.line_up.API;
import project.tronku.line_up.MainActivity;
import project.tronku.line_up.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class SignUpFragment extends Fragment implements OnSignUpListener {
    private static final String TAG = "SignUpFragment";
    private View inflate;
    private EditText nameEditText, phoneEditText, zealIdEditText, passwordEditText;
    private String name, phone, zealid, password;

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

        return inflate;
    }

    @Override
    public void signUp() {
        name = nameEditText.getText().toString();
        phone = phoneEditText.getText().toString();
        zealid = zealIdEditText.getText().toString();
        password = passwordEditText.getText().toString();

        if (name.isEmpty() || phone.isEmpty() || zealid.isEmpty() || password.isEmpty()) {
            Snackbar snackbar = Snackbar.make(inflate, "Enter details.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
            snackbar.show();
        }
        else if (!Patterns.PHONE.matcher(phone).matches()) {
            Snackbar snackbar = Snackbar.make(inflate, "Enter valid number.", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
            snackbar.show();
        }
        else {
            signUpUser(name, phone, zealid, password);
        }
    }

    private void signUpUser(String name, String phone, String zealid, String password) {
        RequestQueue login;
        JSONObject credentials = new JSONObject();
        try{
            credentials.put("username", name);
            credentials.put("password", password);
            credentials.put("matchingPassword", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest loginreq = new JsonObjectRequest(Request.Method.POST, API.BASE + API.SIGN_UP, credentials,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse: " + response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse!=null && error.networkResponse.statusCode==404){
                    String json = new String(error.networkResponse.data);
                    try {
                        JSONObject jsonError = new JSONObject(json);

                        Log.e(TAG, "onErrorResponse: " + json);
//                        if(jsonError.has("error")){
//                            String errorString = jsonError.get("error").toString();
//
//                            final Dialog dialog = new Dialog(getActivity());
//                            dialog.setContentView(R.layout.dialog_layout);
//                            ImageView close = dialog.findViewById(R.id.close);
//                            close.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    dialog.dismiss();
//                                }
//                            });
//
//                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                @Override
//                                public void onDismiss(DialogInterface dialogInterface) {
//
//                                }
//                            });
//
//                            TextView errorView = dialog.findViewById(R.id.errorText);
//                            errorView.setText(errorString);
//                            dialog.show();

                        //}
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        login = Volley.newRequestQueue(getContext());
        login.add(loginreq);
    }

}
