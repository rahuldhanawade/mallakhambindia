package com.rahuldhanawade.www.amarproject.activity;

import static com.rahuldhanawade.www.amarproject.RestClient.RestClient.ROOT_URL;
import static com.rahuldhanawade.www.amarproject.Utils.CommonMethods.DisplayToastError;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

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
import com.rahuldhanawade.www.amarproject.Utils.UtilitySharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    ImageView iv_logo;
    String Str_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        iv_logo = findViewById(R.id.iv_logo);

        iv_logo.animate().translationY(-2500).setDuration(1000).setStartDelay(4000);

        Str_id = UtilitySharedPreferences.getPrefs(getApplicationContext(),"user_id");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(Str_id!=null && !Str_id.equalsIgnoreCase("") && !Str_id.equals("undefined")) {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        },5000);
    }
}