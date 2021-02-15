package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
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

        LoginRequest req = new LoginRequest(params);
        req.execute();
    }

    private class LoginRequest extends AsyncTask<Void, Void, String> {
        HashMap<String, String> params;

        LoginRequest(HashMap<String, String> params)
        {
            this.params = params;
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(String s)
        {
            try
            {
                JSONObject object = new JSONObject(s);
                Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids)
        {
            RequestHandler rqh = new RequestHandler();
            return rqh.sendPostRequest(API.URL_LOGIN, params);
        }
    }
}