package com.rahuldhanawade.www.amarproject.activity;

import static com.rahuldhanawade.www.amarproject.RestClient.RestClient.ROOT_URL;
import static com.rahuldhanawade.www.amarproject.Utils.CommonMethods.DisplayToastError;
import static com.rahuldhanawade.www.amarproject.Utils.CommonMethods.DisplayToastSuccess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.rahuldhanawade.www.amarproject.Utils.UtilitySharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    TextView cirLoginButton,tv_ver_code;
    EditText Edt_Email,EdtPassword;
    Spinner Spnlocation;
    List<String> locationsList = new ArrayList<>();
    private LoadingDialog loadingDialog;
    String Str_selectedLocation = "";
    private String TAG = "LoginActivity" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadingDialog = new LoadingDialog(LoginActivity.this);

        init();
    }

    public void init(){

        locationsList.add("Select Location");

        Edt_Email = findViewById(R.id.Edt_Email);
        EdtPassword = findViewById(R.id.EdtPassword);
        Spnlocation = findViewById(R.id.spn_location);

//        Edt_Email.setText("umhsr@gmail.com");
//        EdtPassword.setText("test1234");

        cirLoginButton = findViewById(R.id.cirLoginButton);
        cirLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()){
                    UserLogin();
                }
            }
        });

        tv_ver_code = findViewById(R.id.tv_ver_code);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            String verCode = pInfo.versionName;
//            Log.d("test","verCode"+verCode);
            tv_ver_code.setText("Version : v"+verCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }



        Spnlocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Str_selectedLocation = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case where no item is selected
            }
        });

        GetLocationList();

    }

    private boolean isValid() {
        boolean result = true;

        if (!MyValidator.isValidField(Edt_Email)) {
            Edt_Email.requestFocus();
            DisplayToastError(LoginActivity.this,"Please Enter Valid User Id");
            result = false;
        }

        if (!MyValidator.isValidNormalPassword(EdtPassword)) {
            EdtPassword.requestFocus();
            DisplayToastError(LoginActivity.this,"Please Enter Valid Password");
            result = false;
        }

        if (!MyValidator.isValidSpinner(Spnlocation)) {
            DisplayToastError(LoginActivity.this,"Please Select Location");
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
                        Log.d("LOGIN_URLResponse",""+response);

                        try {
                            JSONObject responseObj = new JSONObject(response);
                            String status = responseObj.getString("success");
                            if(status.equals("true")){
                                String token = responseObj.getString("token");
                                String year = responseObj.getString("year");
                                UtilitySharedPreferences.setPrefs(getApplicationContext(), "login_email", Str_email);
                                UtilitySharedPreferences.setPrefs(getApplicationContext(), "login_password", Str_password);
                                UtilitySharedPreferences.setPrefs(getApplicationContext(), "token", token);
                                UtilitySharedPreferences.setPrefs(getApplicationContext(), "year", year);
                                GetUserDetails(token);
                            }else{
                                DisplayToastError(LoginActivity.this,"Sorry For the Inconvince please try again later..");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
                map.put("location", Str_selectedLocation);
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

    private void GetUserDetails(String token) {

        Log.d(TAG, "GetUserDetails: "+token);

        String GetUser_URL = ROOT_URL+"get_user?token="+token;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GetUser_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingDialog.dismissDialog();
                        Log.d("Response",""+response);

                        try {
                            JSONObject responseObj = new JSONObject(response);
                            if(responseObj.toString().contains("status")){
                                String status = responseObj.getString("status");
                                DisplayToastError(LoginActivity.this,status);
                            }else if(responseObj.toString().contains("judge")){
                                JSONObject judgeObj = responseObj.getJSONObject("judge");
                                String id = judgeObj.getString("id");
                                String judge_no = judgeObj.getString("judge_no");
                                String judgetype = judgeObj.getString("judgetype");
                                String name = judgeObj.getString("name");
                                String username = judgeObj.getString("username");
                                String email = judgeObj.getString("email");
                                String mobile_no = judgeObj.getString("mobile_no");

                                UtilitySharedPreferences.setPrefs(getApplicationContext(), "user_id", id);
                                UtilitySharedPreferences.setPrefs(getApplicationContext(), "user_judge_no", judge_no);
                                UtilitySharedPreferences.setPrefs(getApplicationContext(), "user_judge_type", judgetype);
                                UtilitySharedPreferences.setPrefs(getApplicationContext(), "user_name", name);
                                UtilitySharedPreferences.setPrefs(getApplicationContext(), "user_username", username);
                                UtilitySharedPreferences.setPrefs(getApplicationContext(), "user_email", email);
                                UtilitySharedPreferences.setPrefs(getApplicationContext(), "user_mobile_no", mobile_no);
                                UtilitySharedPreferences.setPrefs(getApplicationContext(), "user_location", Str_selectedLocation);

                                DisplayToastSuccess(LoginActivity.this,"Login Successful");

                                Intent i = new Intent(LoginActivity.this,HomeActivity.class);
                                startActivity(i);
                                finish();

                            }else{
                                DisplayToastError(LoginActivity.this,"Sorry For the Inconvince please try again later..");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
                });

        int socketTimeout = 50000; //30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void GetLocationList() {

        String GetLocationList_URL = ROOT_URL+"location";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GetLocationList_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response",response);

                        try {
                            JSONObject responseObj = new JSONObject(response);
                            if(responseObj.toString().contains("status")){
                                String status = responseObj.getString("status");
                                DisplayToastError(LoginActivity.this,status);
                            }else if(responseObj.toString().contains("data")){
                                JSONArray locatAry = responseObj.getJSONArray("data");
                                for(int i = 0; i < locatAry.length(); i++){
                                    String loca = locatAry.get(i).toString();
                                    locationsList.add(loca);
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_spinner_item, locationsList);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Spnlocation.setAdapter(adapter);

                            }else{
                                DisplayToastError(LoginActivity.this,"Sorry For the Inconvince please try again later..");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                });

        int socketTimeout = 50000; //30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}