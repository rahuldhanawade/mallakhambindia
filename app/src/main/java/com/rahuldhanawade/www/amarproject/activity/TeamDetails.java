package com.rahuldhanawade.www.amarproject.activity;

import static com.rahuldhanawade.www.amarproject.RestClient.RestClient.ROOT_URL;
import static com.rahuldhanawade.www.amarproject.Utils.CommonMethods.DisplayToastError;
import static com.rahuldhanawade.www.amarproject.Utils.CommonMethods.DisplayToastSuccess;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import com.rahuldhanawade.www.amarproject.Adapters.TeamPlayersAdapter;
import com.rahuldhanawade.www.amarproject.Adapters.TeamPlayersPOJO;
import com.rahuldhanawade.www.amarproject.R;
import com.rahuldhanawade.www.amarproject.Utils.CommonMethods;
import com.rahuldhanawade.www.amarproject.Utils.LoadingDialog;
import com.rahuldhanawade.www.amarproject.Utils.UtilitySharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TeamDetails extends AppCompatActivity {

    private static final String TAG = TeamDetails.class.getSimpleName();
    LoadingDialog loadingDialog;
    String Str_token = "", Str_user_location = "", Str_year = "", str_team_id = "", str_team_status_id = "", str_team_name = "", str_team_gender = "", str_team_group = "";
    boolean is_back = true;

    SwipeRefreshLayout refreshLayout;
    TextView tv_players_empty,tv_plyr_count,tv_plyr_gender,tv_plyr_group;
    LinearLayout linear_complete;
    RecyclerView recyclerView;
    TeamPlayersAdapter teamPlayersAdapter;
    ArrayList<TeamPlayersPOJO> teamPlayersPOJOArrayList = new ArrayList<>();
    TeamPlayersPOJO teamPlayersPOJO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_details);

        str_team_id = getIntent().getStringExtra("team_id");
        str_team_status_id = getIntent().getStringExtra("team_status_id");
        str_team_name = getIntent().getStringExtra("team_name");
        str_team_gender = getIntent().getStringExtra("team_gender");
        str_team_group = getIntent().getStringExtra("team_group");

        loadingDialog = new LoadingDialog(TeamDetails.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_team_details);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(str_team_name);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Str_token = UtilitySharedPreferences.getPrefs(getApplicationContext(),"token");
        Str_user_location = UtilitySharedPreferences.getPrefs(getApplicationContext(),"user_location");
        init();
    }

    public void init() {

        tv_players_empty = findViewById(R.id.tv_teams_player_empty);

        tv_plyr_count = findViewById(R.id.tv_plyr_count);
        tv_plyr_gender = findViewById(R.id.tv_plyr_gender);
        tv_plyr_group = findViewById(R.id.tv_plyr_group);

        linear_complete = findViewById(R.id.linear_complete);

        if(str_team_status_id == null || str_team_status_id.isEmpty()){
            str_team_status_id = "1";
        }

        if(str_team_status_id.equals("0")){
            linear_complete.setVisibility(View.VISIBLE);
        }else{
            linear_complete.setVisibility(View.GONE);
        }

        linear_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();
            }
        });

        tv_plyr_gender.setText(CommonMethods.getCapsSentences(str_team_gender));
        tv_plyr_group.setText(str_team_group);

        recyclerView = findViewById(R.id.recy_team_player);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(TeamDetails.this));
        recyclerView.setAdapter(teamPlayersAdapter);


        refreshLayout = findViewById(R.id.swref_team_details);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                is_back = false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        teamPlayersPOJOArrayList.clear();
                        UserLogin(0);
                        teamPlayersAdapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                    }
                },1000);
            }
        });

        UserLogin(0);
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to proceed?")
                .setTitle("Confirmation")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        UserLogin(1);
                        dialog.dismiss();
                    }
                })

                // Set "No" button
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                       dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void UserLogin(int ApiCall){

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
                                UtilitySharedPreferences.setPrefs(getApplicationContext(), "year", year);
                                Str_token = token;
                                Str_year = year;
                                if(ApiCall == 1){
                                    setTeamScoreStatus();
                                }else{
                                    getTeamPlayerList();
                                }
                            }else{
                                DisplayToastError(TeamDetails.this,"Sorry For the Inconvince please try again later..");
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
                            DisplayToastError(TeamDetails.this,"Server is not connected to internet.");
                        } else if (error instanceof AuthFailureError) {
                            DisplayToastError(TeamDetails.this,"Server couldn't find the authenticated request.");
                        } else if (error instanceof ServerError) {
                            DisplayToastError(TeamDetails.this,"Server is not responding.Please try Again Later");
                        } else if (error instanceof NetworkError) {
                            DisplayToastError(TeamDetails.this,"Your device is not connected to internet.");
                        } else if (error instanceof ParseError) {
                            DisplayToastError(TeamDetails.this,"Parse Error (because of invalid json or xml).");
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("email", Str_email);
                map.put("password", Str_password);
                map.put("location", Str_user_location);
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

    private void getTeamPlayerList() {

        teamPlayersPOJOArrayList.clear();

        loadingDialog.startLoadingDialog();

        String GetTeamPlayersURL=ROOT_URL+"get_team_players";

        Log.d("urltest",""+GetTeamPlayersURL);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GetTeamPlayersURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                is_back = true;
                loadingDialog.dismissDialog();
                Log.d("Response",""+response);
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonresponse = new JSONObject(response);
                        String status = jsonresponse.getString("success");
                        if (status.trim().contains("true")) {
                            JSONArray data_array = jsonresponse.getJSONArray("data");
                            Log.d(TAG, "onResponse: "+data_array.length());
                            tv_plyr_count.setText(""+data_array.length());
                            for (int i = 0; i < data_array.length(); i++) {
                                JSONObject Playersdataobj = data_array.getJSONObject(i);
                                Log.d("Teamsdataobj",""+Playersdataobj);

                                teamPlayersPOJO = new TeamPlayersPOJO();
                                teamPlayersPOJO.setId(Playersdataobj.getString("player_id"));
                                teamPlayersPOJO.setScore_id(Playersdataobj.getString("score_id"));
                                teamPlayersPOJO.setScore_flag(Playersdataobj.getString("score_flag"));
                                teamPlayersPOJO.setParticipant_name(Playersdataobj.getString("participant_name"));
                                teamPlayersPOJO.setDob(Playersdataobj.getString("dob"));
                                teamPlayersPOJO.setAge(Playersdataobj.getString("age"));
                                teamPlayersPOJO.setSr_judge_score(Playersdataobj.optString("sr_judge_score"));
                                teamPlayersPOJO.setJ1_score(Playersdataobj.optString("j1_score"));
                                teamPlayersPOJO.setJ2_score(Playersdataobj.optString("j2_score"));
                                teamPlayersPOJO.setJ3_score(Playersdataobj.optString("j3_score"));
                                teamPlayersPOJO.setJ4_score(Playersdataobj.optString("j4_score"));
                                teamPlayersPOJO.setTeam_name(str_team_name);
                                teamPlayersPOJO.setGender(str_team_gender);
                                teamPlayersPOJO.setGroup(str_team_group);
                                teamPlayersPOJO.setTeam_id(str_team_id);
                                teamPlayersPOJOArrayList.add(teamPlayersPOJO);

                            }

                            teamPlayersAdapter = new TeamPlayersAdapter(TeamDetails.this, teamPlayersPOJOArrayList);
                            recyclerView.setAdapter(teamPlayersAdapter);

                            teamPlayersAdapter.notifyDataSetChanged();

                            if(teamPlayersPOJOArrayList.isEmpty())
                            {
                                recyclerView.setVisibility(View.GONE);
                                tv_players_empty.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                recyclerView.setVisibility(View.VISIBLE);
                                tv_players_empty.setVisibility(View.GONE);
                            }

                        }
                        else {
                            recyclerView.setVisibility(View.GONE);
                            tv_players_empty.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else {
                    DisplayToastError(TeamDetails.this,"Something goes wrong. Please try again");
                }

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
                    DisplayToastError(TeamDetails.this,"Server is not connected to internet.");
                } else if (error instanceof AuthFailureError) {
                    DisplayToastError(TeamDetails.this,"Server couldn't find the authenticated request.");
                } else if (error instanceof ServerError) {
                    DisplayToastError(TeamDetails.this,"Server is not responding.Please try Again Later");
                } else if (error instanceof NetworkError) {
                    DisplayToastError(TeamDetails.this,"Your device is not connected to internet.");
                } else if (error instanceof ParseError) {
                    DisplayToastError(TeamDetails.this,"Parse Error (because of invalid json or xml).");
                }
            }
        }){
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> map = new HashMap<String, String>();
                map.put("judge_id", UtilitySharedPreferences.getPrefs(getApplicationContext(),"user_id"));
                map.put("team_id", str_team_id);
                map.put("competition_year", Str_year);
                map.put("location", Str_user_location);
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
        RequestQueue requestQueue = Volley.newRequestQueue(TeamDetails.this);
        requestQueue.add(stringRequest);
    }

    private void setTeamScoreStatus() {

        loadingDialog.startLoadingDialog();

        String setTeamScoreStatusURL=ROOT_URL+"update_team_status";

        Log.d("urltest",""+setTeamScoreStatusURL);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, setTeamScoreStatusURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                is_back = true;
                loadingDialog.dismissDialog();
                Log.d("Response",""+response);
                if (response != null && response.length() > 0) {
                    try {
                        JSONObject jsonresponse = new JSONObject(response);
                        String status = jsonresponse.getString("success");
                        if(status.equals("")){
                            String message = jsonresponse.getString("message");
                            DisplayToastSuccess(TeamDetails.this,message);
                            linear_complete.setVisibility(View.GONE);
                            str_team_status_id = "1";
                        }else {
                            DisplayToastError(TeamDetails.this,"Something goes wrong. Please try again");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    DisplayToastError(TeamDetails.this,"Something goes wrong. Please try again");
                }

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
                    DisplayToastError(TeamDetails.this,"Server is not connected to internet.");
                } else if (error instanceof AuthFailureError) {
                    DisplayToastError(TeamDetails.this,"Server couldn't find the authenticated request.");
                } else if (error instanceof ServerError) {
                    DisplayToastError(TeamDetails.this,"Server is not responding.Please try Again Later");
                } else if (error instanceof NetworkError) {
                    DisplayToastError(TeamDetails.this,"Your device is not connected to internet.");
                } else if (error instanceof ParseError) {
                    DisplayToastError(TeamDetails.this,"Parse Error (because of invalid json or xml).");
                }
            }
        }){
            @Override
            protected Map<String, String> getParams () {
                Map<String, String> map = new HashMap<String, String>();
                map.put("status", "1");
                map.put("team_id", str_team_id);
                map.put("competition_year", Str_year);
                map.put("location", Str_user_location);
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
        RequestQueue requestQueue = Volley.newRequestQueue(TeamDetails.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        if(is_back){
            super.onBackPressed();
        }
    }
}