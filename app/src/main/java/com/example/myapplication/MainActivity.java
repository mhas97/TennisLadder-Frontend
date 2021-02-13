package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import java.util.HashMap;

// [REF: https://www.simplifiedcoding.net/android-mysql-tutorial-to-perform-basic-crud-operation/#Android-MySQL-Tutorial]

public class MainActivity extends AppCompatActivity {
    EditText editTextEmail, editTextPassword;
    Button buttonLogin;
    boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = findViewById(R.id.email_input_login);
        editTextPassword = findViewById(R.id.password_input_login);
        buttonLogin = findViewById(R.id.login_button);

        buttonLogin.setOnClickListener(view -> {
            if (isUpdating) {
            }
            else {
                login();
            }
        });
    }

    private void login()  {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        NetworkRequest req = new NetworkRequest(this, API.URL_LOGIN, params, API.REQ_TYPE_POST);
        req.execute();
    }
}