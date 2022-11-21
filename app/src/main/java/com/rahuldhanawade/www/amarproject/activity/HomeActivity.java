package com.rahuldhanawade.www.amarproject.activity;

import static com.rahuldhanawade.www.amarproject.RestClient.RestClient.ROOT_URL;
import static com.rahuldhanawade.www.amarproject.Utils.CommonMethods.DisplayToastError;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import com.nex3z.togglebuttongroup.ToggleButtonGroup;
import com.nex3z.togglebuttongroup.button.LabelToggle;
import com.rahuldhanawade.www.amarproject.Adapters.AgeTeamsAdapter;
import com.rahuldhanawade.www.amarproject.Adapters.AgeTeamsPOJO;
import com.rahuldhanawade.www.amarproject.R;
import com.rahuldhanawade.www.amarproject.Utils.LoadingDialog;
import com.rahuldhanawade.www.amarproject.Utils.MyValidator;
import com.rahuldhanawade.www.amarproject.Utils.UtilitySharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = HomeActivity.class.getSimpleName();
    TextView tv_ver_code;
    FloatingActionButton flt_fliter;
    BottomSheetDialog bottomSheetDialog;
    private LoadingDialog loadingDialog;

    String Str_gender = "", Str_grp_gender = "";
    String Str_token = "", Str_year = "", Str_name = "", Str_username = "", Str_email = "", Str_mobile_no = "";

    TextView tv_teams_empty;
    RecyclerView recyclerView;
    AgeTeamsAdapter ageTeamsAdapter;
    ArrayList<AgeTeamsPOJO> AgeTeamsPOJOS_list = new ArrayList<>();
    AgeTeamsPOJO ageTeamsPOJO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        loadingDialog = new LoadingDialog(HomeActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Welcome...");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toggle.getDrawerArrowDrawable().setColor(getColor(R.color.colorAccent));
        }else{
            toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorAccent));
        }
        toggle.syncState();

        Str_name = UtilitySharedPreferences.getPrefs(getApplicationContext(),"user_name");
        Str_username = UtilitySharedPreferences.getPrefs(getApplicationContext(),"user_username");
        Str_email = UtilitySharedPreferences.getPrefs(getApplicationContext(),"user_email");
        Str_mobile_no = UtilitySharedPreferences.getPrefs(getApplicationContext(),"user_mobile_no");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);

        TextView login_name = (TextView)hView.findViewById(R.id.login_name);

        if(Str_name!=null && !Str_name.equalsIgnoreCase("")) {
            login_name.setText(Str_name);
        }

        tv_ver_code = findViewById(R.id.tv_home_ver_code);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
            String verCode = pInfo.versionName;
//            Log.d("test","verCode"+verCode);
            tv_ver_code.setText("Version : v"+verCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        init();
    }

    private void init(){

        showfilterdialogg();

        flt_fliter = findViewById(R.id.flt_fliter);
        flt_fliter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showfilterdialogg();
            }
        });

        tv_teams_empty = findViewById(R.id.tv_teams_empty);

        recyclerView = findViewById(R.id.recy_age_teams);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        recyclerView.setAdapter(ageTeamsAdapter);
    }

    private void showfilterdialogg() {

        Str_gender = "";
        Str_grp_gender = "";

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.pop_up_filter_dashboard);
        bottomSheetDialog.show();

        SingleSelectToggleGroup group_gender = bottomSheetDialog.findViewById(R.id.group_gender);

        LabelToggle lt_male = bottomSheetDialog.findViewById(R.id.lt_male);
        LabelToggle lt_female = bottomSheetDialog.findViewById(R.id.lt_female);

        TextView tv_cancel = bottomSheetDialog.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        TextView tv_filter = bottomSheetDialog.findViewById(R.id.tv_filter);
        tv_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()){
                    UserLogin();
                }
            }
        });

        group_gender.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                if(R.id.lt_male == checkedId){
                    Str_gender = lt_male.getText().toString();
                    getAgeGroup(lt_male.getText().toString());
                }else if (R.id.lt_female == checkedId){
                    Str_gender = lt_female.getText().toString();
                    getAgeGroup(lt_female.getText().toString());
                }
            }
        });
    }

    private boolean isValid() {
        boolean result = true;

        if (Str_gender == null || Str_gender.equals("") || Str_gender.equals("null")) {
            DisplayToastError(HomeActivity.this,"Please Select Gender");
            result = false;
        }

        if (Str_grp_gender == null || Str_grp_gender.equals("") || Str_grp_gender.equals("null")) {
            DisplayToastError(HomeActivity.this,"Please Select Age Group");
            result = false;
        }

        return result;
    }

    private void getAgeGroup(String str_gender) {

        loadingDialog.startLoadingDialog();

        String AgeGroup_URL = ROOT_URL+"age_group_master";

        Log.d("AgeGroup_URL",""+AgeGroup_URL);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AgeGroup_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingDialog.dismissDialog();
                        Log.d("Response",""+response);

                        try {
                            JSONObject responseObj = new JSONObject(response);
                            String status = responseObj.getString("success");
                            if(status.equals("true")){
                                JSONArray dataArry = responseObj.getJSONArray("data");
                                if(dataArry.length() > 0){
                                    setGenderGroup(dataArry);
                                }
                                Log.d(TAG, "onResponse: "+dataArry.toString());
                            }else{
                                DisplayToastError(HomeActivity.this,"Sorry For the Inconvince please try again later..");
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
                        if (error == null || error.networkResponse == null) {
                            Log.d(TAG, "onErrorResponse: error");
                            return;
                        }

                        String body;
                        //get response body and parse with appropriate encoding
                        try {
                            body = new String(error.networkResponse.data,"UTF-8");
                            Log.d(TAG, "errResponse: "+body);
                        } catch (UnsupportedEncodingException e) {
                            // exception
                            Log.d(TAG, "errResponse: UnsupportedEncodingException");
                            e.printStackTrace();
                        }
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            DisplayToastError(HomeActivity.this,"Server is not connected to internet.");
                        } else if (error instanceof AuthFailureError) {
                            DisplayToastError(HomeActivity.this,"Server couldn't find the authenticated request.");
                        } else if (error instanceof ServerError) {
                            DisplayToastError(HomeActivity.this,"Server is not responding.Please try Again Later");
                        } else if (error instanceof NetworkError) {
                            DisplayToastError(HomeActivity.this,"Your device is not connected to internet.");
                        } else if (error instanceof ParseError) {
                            DisplayToastError(HomeActivity.this,"Parse Error (because of invalid json or xml).");
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("gender", str_gender);
                Log.d("LoginParamas",""+map.toString());
                return map;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + Str_token);
                return headers;
            }
        };

        int socketTimeout = 50000; //30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void setGenderGroup(JSONArray dataArry) throws JSONException {

        SingleSelectToggleGroup rg_group_gender = bottomSheetDialog.findViewById(R.id.rg_group_gender);
        rg_group_gender.removeAllViews();

        RadioGroup.LayoutParams rprms;
        for(int k=0; k< dataArry.length();k++){
            JSONObject status_obj = dataArry.getJSONObject(k);
            String age_group = status_obj.getString("age_group");

            LabelToggle radioButton = new LabelToggle(this);
            radioButton.setElevation(5);
            radioButton.setText(age_group);
            radioButton.setMarkerColor(getResources().getColor(R.color.colorPrimary));

            rprms= new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            rprms.setMargins(10,10,10,10);
            rg_group_gender.addView(radioButton, rprms);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectedId = (String) radioButton.getText();
                    Str_grp_gender = selectedId;
                    Log.d("TAG", "onClick: "+selectedId);
                }
            });
        }
    }

    public void UserLogin(){

        String Str_email = UtilitySharedPreferences.getPrefs(getApplicationContext(),"login_email");
        String Str_password = UtilitySharedPreferences.getPrefs(getApplicationContext(),"login_password");

        loadingDialog.startLoadingDialog();

        String LOGIN_URL = ROOT_URL+"login";

        Log.d("LOGIN_URL",""+LOGIN_URL);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingDialog.dismissDialog();
                        Log.d("Response",""+response);

                        try {
                            JSONObject responseObj = new JSONObject(response);
                            String status = responseObj.getString("success");
                            if(status.equals("true")){
                                String token = responseObj.getString("token");
                                String year = responseObj.getString("year");
                                UtilitySharedPreferences.setPrefs(getApplicationContext(), "token", token);
                                Str_token = token;
                                Str_year = year;
                                getTeamList();
                            }else{
                                DisplayToastError(HomeActivity.this,"Sorry For the Inconvince please try again later..");
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
                            DisplayToastError(HomeActivity.this,"Server is not connected to internet.");
                        } else if (error instanceof AuthFailureError) {
                            DisplayToastError(HomeActivity.this,"Server couldn't find the authenticated request.");
                        } else if (error instanceof ServerError) {
                            DisplayToastError(HomeActivity.this,"Server is not responding.Please try Again Later");
                        } else if (error instanceof NetworkError) {
                            DisplayToastError(HomeActivity.this,"Your device is not connected to internet.");
                        } else if (error instanceof ParseError) {
                            DisplayToastError(HomeActivity.this,"Parse Error (because of invalid json or xml).");
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

    private void getTeamList() {

        AgeTeamsPOJOS_list.clear();

        loadingDialog.startLoadingDialog();

        String GetTeamsURL=ROOT_URL+"get_teams";

        Log.d("urltest",""+GetTeamsURL);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GetTeamsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismissDialog();
                Log.d("Response",""+response);
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonresponse = new JSONObject(response);
                        String status = jsonresponse.getString("success");
                        if (status.trim().contains("true")) {
                            JSONArray data_array = jsonresponse.getJSONArray("data");
                            Log.d(TAG, "onResponse: "+data_array.length());
                            for (int i = 0; i < data_array.length(); i++) {
                                JSONObject Teamsdataobj = data_array.getJSONObject(i);
                                Log.d("Teamsdataobj",""+Teamsdataobj);

                                ageTeamsPOJO = new AgeTeamsPOJO();
                                ageTeamsPOJO.setId(Teamsdataobj.getString("id"));
                                ageTeamsPOJO.setCompetition_year(Teamsdataobj.getString("competition_year"));
                                ageTeamsPOJO.setUniq_id(Teamsdataobj.getString("uniq_id"));
                                ageTeamsPOJO.setTeam_name(Teamsdataobj.getString("team_name"));
                                ageTeamsPOJO.setEmail_institude(Teamsdataobj.getString("email_institude"));
                                ageTeamsPOJO.setMobile_institude(Teamsdataobj.getString("mobile_institude"));
                                ageTeamsPOJO.setCoach_name(Teamsdataobj.getString("coach_name"));
                                ageTeamsPOJO.setEmail(Teamsdataobj.getString("email"));
                                ageTeamsPOJO.setMobile(Teamsdataobj.getString("mobile"));
                                ageTeamsPOJO.setGender(Teamsdataobj.getString("gender"));
                                ageTeamsPOJO.setAge_group(Teamsdataobj.getString("age_group"));
                                AgeTeamsPOJOS_list.add(ageTeamsPOJO);

                            }

                            ageTeamsAdapter = new AgeTeamsAdapter(HomeActivity.this, AgeTeamsPOJOS_list);
                            recyclerView.setAdapter(ageTeamsAdapter);

                            ageTeamsAdapter.notifyDataSetChanged();

                            if(AgeTeamsPOJOS_list.isEmpty())
                            {
                                recyclerView.setVisibility(View.GONE);
                                tv_teams_empty.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                recyclerView.setVisibility(View.VISIBLE);
                                tv_teams_empty.setVisibility(View.GONE);
                            }

                        }
                        else {
                            recyclerView.setVisibility(View.GONE);
                            tv_teams_empty.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else {
                    DisplayToastError(HomeActivity.this,"Something goes wrong. Please try again");
                }
                bottomSheetDialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismissDialog();
                if (error == null || error.networkResponse == null) {
                    Log.d(TAG, "onErrorResponse: error");
                    return;
                }

                String body;
                //get response body and parse with appropriate encoding
                try {
                    body = new String(error.networkResponse.data,"UTF-8");
                    Log.d(TAG, "errResponse: "+body);

                } catch (UnsupportedEncodingException e) {
                    // exception
                    Log.d(TAG, "errResponse: UnsupportedEncodingException");
                    e.printStackTrace();
                }
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    DisplayToastError(HomeActivity.this,"Server is not connected to internet.");
                } else if (error instanceof AuthFailureError) {
                    DisplayToastError(HomeActivity.this,"Server couldn't find the authenticated request.");
                } else if (error instanceof ServerError) {
                    DisplayToastError(HomeActivity.this,"Server is not responding.Please try Again Later");
                } else if (error instanceof NetworkError) {
                    DisplayToastError(HomeActivity.this,"Your device is not connected to internet.");
                } else if (error instanceof ParseError) {
                    DisplayToastError(HomeActivity.this,"Parse Error (because of invalid json or xml).");
                }
            }
        }){
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> map = new HashMap<String, String>();
                map.put("gender", Str_gender);
                map.put("age_group", Str_grp_gender);
                map.put("competition_year", Str_year);
                Log.d("Getdata",""+map.toString());
                return map;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + Str_token);
                return headers;
            }
        } ;

        RetryPolicy policy = new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue requestQueue = Volley.newRequestQueue(HomeActivity.this);
        requestQueue.add(stringRequest);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(a);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        // int id = item.getItemId();

        switch(item.getItemId()){
            case R.id.nav_about_us:
                Intent newVehicleIntent = new Intent(HomeActivity.this, TeamDetails.class);
                startActivity(newVehicleIntent);
                break;
            case R.id.nav_logout:
                UtilitySharedPreferences.clearPref(HomeActivity.this);
                Intent i = new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
                break;

            default:
        }

        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}