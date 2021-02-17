package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class SignupFragment extends Fragment {

    private EditText txtEmail, txtPassword, txtContactNo, txtFname, txtLname;
    private Spinner spinnerClub;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (ViewGroup) layoutInflater.inflate(R.layout.fragment_signup, container, false);
        txtEmail = view.findViewById(R.id.txtEditEmailSignup);
        txtPassword = view.findViewById(R.id.txtEditPasswordSignup);
        txtContactNo = view.findViewById(R.id.txtEditContactNo);
        txtFname = view.findViewById(R.id.txtEditFname);
        txtLname = view.findViewById(R.id.txtEditLname);
        spinnerClub = view.findViewById(R.id.spinnerClub);
        Button btnSignup = view.findViewById(R.id.btnSignup);
        getClubList();
        btnSignup.setOnClickListener(v -> {
            createUser();
        });
        return view;
    }

    protected void getClubList() {
        clubRequest clubRequest = new clubRequest();
        clubRequest.execute();
    }

    protected void createUser() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String contactno = txtContactNo.getText().toString().trim();
        String fname = txtFname.getText().toString().trim();
        String lname = txtLname.getText().toString().trim();
        String clubname = spinnerClub.getSelectedItem().toString().trim();
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("contactno", contactno);
        params.put("fname", fname);
        params.put("lname", lname);
        params.put("clubname", clubname);
        SignupRequest signupRequest = new SignupRequest(params);
        signupRequest.execute();
    }

    private class clubRequest extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject object = new JSONObject(s);
                JSONArray arr = object.getJSONArray("clubs");
                ArrayList<String> clubs = new ArrayList<>();
                for (int i = 0; i < arr.length(); ++i) {
                    clubs.add(arr.getString(i));
                }
                ArrayAdapter<String> clubAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, clubs);
                spinnerClub.setAdapter(clubAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.sendGetRequest(API.URL_GET_CLUBS);
        }
    }

    private class SignupRequest extends AsyncTask<Void, Void, String> {

        HashMap<String, String> params;

        SignupRequest(HashMap<String, String> params) {
            this.params = params;
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject object = new JSONObject(s);
                Toast.makeText(getActivity().getApplicationContext(), object.getString("message"), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.sendPostRequest(API.URL_CREATE_PLAYER, params);
        }
    }
}
