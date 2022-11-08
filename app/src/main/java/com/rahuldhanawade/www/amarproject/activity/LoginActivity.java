package com.rahuldhanawade.www.amarproject.activity;

import static com.rahuldhanawade.www.amarproject.RestClient.RestClient.ROOT_URL;
import static com.rahuldhanawade.www.amarproject.Utils.CommonMethods.DisplayToastError;
import static com.rahuldhanawade.www.amarproject.Utils.CommonMethods.DisplayToastSuccess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rahuldhanawade.www.amarproject.R;
import com.rahuldhanawade.www.amarproject.Utils.LoadingDialog;
import com.rahuldhanawade.www.amarproject.Utils.MyValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    TextView cirLoginButton;
    EditText Edt_Email,EdtPassword;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadingDialog = new LoadingDialog(LoginActivity.this);

        init();
    }

    public void init(){

        Edt_Email = findViewById(R.id.Edt_Email);
        EdtPassword = findViewById(R.id.EdtPassword);

        Edt_Email.setText("judge@gmail.com");
        EdtPassword.setText("test1234");

        cirLoginButton = findViewById(R.id.cirLoginButton);
        cirLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()){
                    UserLogin();
                }
            }
        });

    }

    private boolean isValid() {
        boolean result = true;

        if (!MyValidator.isValidEmail(Edt_Email)) {
            Edt_Email.requestFocus();
            DisplayToastError(LoginActivity.this,"Please Enter Valid Email Id");
            result = false;
        }

        if (!MyValidator.isValidNormalPassword(EdtPassword)) {
            EdtPassword.requestFocus();
            DisplayToastError(LoginActivity.this,"Please Enter Valid Password");
            result = false;
        }

        return result;
    }

    public void UserLogin(){

        String Str_email = Edt_Email.getText().toString();
        String Str_password = EdtPassword.getText().toString();

        loadingDialog.startLoadingDialog();

        String LOGIN_URL = ROOT_URL+"login";

        Log.d("LOGIN_URL",""+LOGIN_URL);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingDialog.dismissDialog();
                        Log.d("Response",""+response);

                        DisplayToastSuccess(LoginActivity.this,"Login Successful");
//                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
//                        startActivity(i);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingDialog.dismissDialog();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            DisplayToastError(LoginActivity.this,"Server is not connected to internet.");
                        } else if (error instanceof AuthFailureError) {
                            DisplayToastError(LoginActivity.this,"Server couldn't find the authenticated request.");
                        } else if (error instanceof ServerError) {
                            DisplayToastError(LoginActivity.this,"Server is not responding.Please try Again Later");
                        } else if (error instanceof NetworkError) {
                            DisplayToastError(LoginActivity.this,"Your device is not connected to internet.");
                        } else if (error instanceof ParseError) {
                            DisplayToastError(LoginActivity.this,"Parse Error (because of invalid json or xml).");
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("email", Str_email);
                map.put("password", Str_password);
                Log.d("LoginParamas",""+map.toString());
                return map;
            }
        };

        int socketTimeout = 50000; //30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}