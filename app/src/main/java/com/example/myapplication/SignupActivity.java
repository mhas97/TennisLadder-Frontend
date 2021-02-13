package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    EditText editTextEmail, editTextPassword, editTextContactno, editTextFname, editTextLname;
    Spinner spinnerClub;
    Button buttonSignup;
    boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        editTextEmail = findViewById(R.id.email_input_signup);
        editTextPassword = findViewById(R.id.password_input_signup);
        editTextContactno = findViewById(R.id.contactno_input_signup);
        editTextFname = findViewById(R.id.fname_input_signup);
        editTextLname = findViewById(R.id.lname_input_signup);
        spinnerClub = findViewById(R.id.spinner_club_signup);
        buttonSignup = findViewById(R.id.signup_button);

        buttonSignup.setOnClickListener(view -> {
            if (isUpdating) {
            } else {
                createUser();
            }
        });
    }

    private void createUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String contactno = editTextContactno.getText().toString().trim();
        String fname = editTextFname.getText().toString().trim();
        String lname = editTextLname.getText().toString().trim();
        String clubname = spinnerClub.getSelectedItem().toString().trim();

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("contactno", contactno);
        params.put("fname", fname);
        params.put("lname", lname);
        params.put("clubname", clubname);

        NetworkRequest req = new NetworkRequest(this, API.URL_CREATE_PLAYER, params, API.REQ_TYPE_POST);
        req.execute();
    }
}