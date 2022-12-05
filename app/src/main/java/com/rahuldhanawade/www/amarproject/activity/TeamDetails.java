package com.rahuldhanawade.www.amarproject.activity;

import static com.rahuldhanawade.www.amarproject.RestClient.RestClient.ROOT_URL;
import static com.rahuldhanawade.www.amarproject.Utils.CommonMethods.DisplayToastError;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
    String Str_token = "", Str_year = "", str_team_id = "", str_team_name = "", str_team_gender = "", str_team_group = "";

    SwipeRefreshLayout refreshLayout;
    TextView tv_players_empty,tv_plyr_count,tv_plyr_gender,tv_plyr_group;
    RecyclerView recyclerView;
    TeamPlayersAdapter teamPlayersAdapter;
    ArrayList<TeamPlayersPOJO> teamPlayersPOJOArrayList = new ArrayList<>();
    TeamPlayersPOJO teamPlayersPOJO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_details);

        str_team_id = getIntent().getStringExtra("team_id");
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

        init();
    }

    public void init() {

        tv_players_empty = findViewById(R.id.tv_teams_player_empty);

        tv_plyr_count = findViewById(R.id.tv_plyr_count);
        tv_plyr_gender = findViewById(R.id.tv_plyr_gender);
        tv_plyr_group = findViewById(R.id.tv_plyr_group);

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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        teamPlayersPOJOArrayList.clear();
                        UserLogin();
                        teamPlayersAdapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                    }
                },1000);
            }
        });

        UserLogin();
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
                                UtilitySharedPreferences.setPrefs(getApplicationContext(), "year", year);
                                Str_token = token;
                                Str_year = year;
                                getTeamPlayerList();
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

}