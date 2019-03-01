package project.tronku.line_up.login;

import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

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

        }
    }
}
