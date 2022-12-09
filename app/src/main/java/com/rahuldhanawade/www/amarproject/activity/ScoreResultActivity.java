package com.rahuldhanawade.www.amarproject.activity;

import static com.rahuldhanawade.www.amarproject.RestClient.RestClient.ROOT_URL;
import static com.rahuldhanawade.www.amarproject.Utils.CommonMethods.DisplayPopUp;
import static com.rahuldhanawade.www.amarproject.Utils.CommonMethods.DisplayToastError;
import static com.rahuldhanawade.www.amarproject.Utils.CommonMethods.checkNullExcHandler;
import static com.rahuldhanawade.www.amarproject.Utils.CommonMethods.getCapsSentences;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rahuldhanawade.www.amarproject.R;
import com.rahuldhanawade.www.amarproject.Utils.LoadingDialog;
import com.rahuldhanawade.www.amarproject.Utils.UtilitySharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class ScoreResultActivity extends AppCompatActivity {

    private LoadingDialog loadingDialog;
    private static final String TAG = ScoreForm.class.getSimpleName();
    String Str_judgetype = "",Str_token = "",Str_year = "",set_comb = "";
    String score_id = "",player_id = "",player_name = "",team_id = "",team_name = "",player_age = "",player_gender = "",player_group = "";
    String UnitAValue = "",UnitBValue = "",UnitCValue = "";
    boolean is_visible = true;

    ImageView iv_refesh_scoreform;
    CardView card_judge_score;
    BottomSheetDialog bottomSheetDialog;
    LinearLayout linear_all_judge,linear_questions;
    TextView tv_edit_score,tv_sr_plyr_name,tv_sr_plyr_age,tv_sr_plyr_goup,tv_sr_plyr_gender;
    TextView tv_sr_a_marks,tv_sr_b_marks,tv_sr_c_marks,tv_sr_comb,tv_sr_diff_comb,tv_sr_exc,tv_sr_orig,tv_sr_final_value,tv_sr_comment;
    TextView tv_sr_a,tv_sr_b,tv_sr_c;
    JSONArray questinListArry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_result);

        loadingDialog = new LoadingDialog(ScoreResultActivity.this);

        Str_judgetype = UtilitySharedPreferences.getPrefs(getApplicationContext(),"user_judge_type");

        score_id = getIntent().getStringExtra("score_id");
        player_id = getIntent().getStringExtra("player_id");
        player_name = getIntent().getStringExtra("player_name");
        team_id = getIntent().getStringExtra("team_id");
        team_name = getIntent().getStringExtra("team_name");
        player_age = getIntent().getStringExtra("player_age");
        player_gender = getIntent().getStringExtra("player_gender");
        player_group = getIntent().getStringExtra("player_group");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_score_result);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(checkNullExcHandler(team_name));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Init();
    }

    private void Init() {

        iv_refesh_scoreform = findViewById(R.id.iv_refesh_scoreform);
        iv_refesh_scoreform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linear_all_judge.removeAllViews();
                linear_questions.removeAllViews();
                UserLogin();
            }
        });
        tv_edit_score = findViewById(R.id.tv_edit_score);
        tv_edit_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ScoreResultActivity.this,ScoreForm.class);
                i.putExtra("score_id",score_id);
                i.putExtra("player_id",player_id);
                i.putExtra("player_name",player_name);
                i.putExtra("team_name",team_name);
                i.putExtra("team_id",team_id);
                i.putExtra("player_age",player_age);
                i.putExtra("player_gender",player_gender);
                i.putExtra("player_group",player_group);
                startActivity(i);
            }
        });
        tv_sr_plyr_name = findViewById(R.id.tv_sr_plyr_name);
        tv_sr_plyr_age = findViewById(R.id.tv_sr_plyr_age);
        tv_sr_plyr_goup = findViewById(R.id.tv_sr_plyr_goup);
        tv_sr_plyr_gender = findViewById(R.id.tv_sr_plyr_gender);
        tv_sr_a_marks = findViewById(R.id.tv_sr_a_marks);
        tv_sr_b_marks = findViewById(R.id.tv_sr_b_marks);
        tv_sr_c_marks = findViewById(R.id.tv_sr_c_marks);
        tv_sr_a = findViewById(R.id.tv_sr_a);
        tv_sr_b = findViewById(R.id.tv_sr_b);
        tv_sr_c = findViewById(R.id.tv_sr_c);
        tv_sr_comb = findViewById(R.id.tv_sr_comb);
        tv_sr_diff_comb = findViewById(R.id.tv_sr_diff_comb);
        tv_sr_exc = findViewById(R.id.tv_sr_exc);
        tv_sr_orig = findViewById(R.id.tv_sr_orig);
        tv_sr_final_value = findViewById(R.id.tv_sr_final_value);
        tv_sr_comment = findViewById(R.id.tv_sr_comment);

        linear_questions = findViewById(R.id.linear_questions);
        card_judge_score = findViewById(R.id.card_judge_score);
        linear_all_judge = findViewById(R.id.linear_all_judge);

        tv_sr_plyr_name.setText(getCapsSentences(player_name));
        tv_sr_plyr_age.setText(player_age);
        tv_sr_plyr_goup.setText(getCapsSentences(player_group));
        tv_sr_plyr_gender.setText(getCapsSentences(player_gender));

        UserLogin();
    }

    private void getPlayerScore() {

        loadingDialog.startLoadingDialog();

        String GETPLYSCR_URL = ROOT_URL+"get_player_score";

        Log.d("GETPLYSCR_URL",""+GETPLYSCR_URL);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GETPLYSCR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingDialog.dismissDialog();
//                        Log.d("GETPLYSCRResponse",""+response);
                        try {
                            JSONObject questionlistObj = new JSONObject(response);
                            String status = questionlistObj.getString("success");
                            String message = questionlistObj.getString("message");
                            if(status.equals("true")){
                                JSONArray data = questionlistObj.getJSONArray("data");
                                for(int i=0;i < data.length();i++){
                                    JSONObject data_obj = data.getJSONObject(i);
                                    String ele_A = data_obj.getString("element_A");
                                    String ele_B = data_obj.getString("element_B");
                                    String ele_C = data_obj.getString("element_C");
                                    String combination_json = data_obj.getString("combination_json");
                                    String comb = data_obj.getString("combination");
                                    String diff = data_obj.getString("difficulty");
                                    String exec = data_obj.getString("execution");
                                    String orig = data_obj.getString("originality");
                                    String comment = data_obj.getString("comment");
                                    String total_score = data_obj.getString("total_score");
                                    String update_recall = data_obj.getString("update_recall");
                                    int final_score_id = data_obj.optInt("final_score_id");

                                    if(!Str_judgetype.equals("Senior Judge")){
                                        if(update_recall.equals("0")){
                                            tv_edit_score.setVisibility(View.VISIBLE);
                                        }else{
                                            tv_edit_score.setVisibility(View.GONE);
                                        }
                                    }


                                    if(final_score_id > 0){
                                        is_visible = false;
                                        tv_edit_score.setVisibility(View.GONE);
                                    }

                                    combination_json = combination_json.replace("{","[");
                                    combination_json = combination_json.replace("}","]");
                                    set_comb = combination_json;

                                    tv_sr_a.setText(ele_A);
                                    tv_sr_b.setText(ele_B);
                                    tv_sr_c.setText(ele_C);
                                    tv_sr_comb.setText(comb);
                                    tv_sr_diff_comb.setText(diff);
                                    tv_sr_exc.setText(exec);
                                    tv_sr_orig.setText(orig);
                                    tv_sr_final_value.setText(total_score);
                                    tv_sr_comment.setText(checkNullExcHandler(comment));
                                }
                                addQuestionsList();
                            }else{
                                DisplayToastError(ScoreResultActivity.this,message);
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
                            DisplayToastError(ScoreResultActivity.this,"Server is not connected to internet.");
                        } else if (error instanceof AuthFailureError) {
                            DisplayToastError(ScoreResultActivity.this,"Server couldn't find the authenticated request.");
                        } else if (error instanceof ServerError) {
                            DisplayToastError(ScoreResultActivity.this,"Server is not responding.Please try Again Later");
                        } else if (error instanceof NetworkError) {
                            DisplayToastError(ScoreResultActivity.this,"Your device is not connected to internet.");
                        } else if (error instanceof ParseError) {
                            DisplayToastError(ScoreResultActivity.this,"Parse Error (because of invalid json or xml).");
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("score_id", score_id);
                map.put("team_id", team_id);
                map.put("player_id", player_id);
                map.put("judge_id", UtilitySharedPreferences.getPrefs(getApplicationContext(),"user_id"));
                map.put("competition_year", Str_year);
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

    private void GetAllJudgeScore() {

//        loadingDialog.startLoadingDialog();

        String GETAllPYRSCR_URL = ROOT_URL+"get_judge_player_score";

        Log.d("GETAllPYRSCR_URL",""+GETAllPYRSCR_URL);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GETAllPYRSCR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        loadingDialog.dismissDialog();
                        Log.d("Response", "" + response);

                        try {
                            JSONObject responseObj = new JSONObject(response);
                            String status = responseObj.getString("success");
                            String message = responseObj.getString("message");
                            if(status.equals("true")){
                                JSONObject alertObj = responseObj.getJSONObject("alert");
                                String AlrtStatus = alertObj.getString("status");
                                if(AlrtStatus.equals("true")){
                                    String Alrtvmessage = alertObj.getString("message");
                                    DisplayPopUp(ScoreResultActivity.this,Alrtvmessage);
                                }
                                JSONArray dataArry = responseObj.getJSONArray("data");
                                if(dataArry.length() > 0){
                                    card_judge_score.setVisibility(View.VISIBLE);
                                }else{
                                    card_judge_score.setVisibility(View.GONE);
                                }
                                for(int i=0; i< dataArry.length();i++){
                                    JSONObject data_obj = dataArry.getJSONObject(i);
                                    String judge_type = data_obj.getString("judgetype");
                                    String judge_no = data_obj.getString("judge_no");
                                    String name = data_obj.getString("name");
                                    String total_score = data_obj.getString("total_score");
                                    String execution = data_obj.getString("execution");
                                    String difficulty = data_obj.getString("difficulty");
                                    String combination = data_obj.getString("combination");
                                    String combination_color = data_obj.getString("combination_color");
                                    String difficulty_color = data_obj.getString("difficulty_color");

                                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View rowView = inflater.inflate(R.layout.judge_score_list_layout, null);

                                    LinearLayout linear_main = rowView.findViewById(R.id.linear_main);
                                    LinearLayout linear_comb = rowView.findViewById(R.id.linear_comb);
                                    LinearLayout linear_diff = rowView.findViewById(R.id.linear_diff);
                                    TextView tv_jdg_name = rowView.findViewById(R.id.tv_jdg_name);
                                    TextView tv_jdg_tol = rowView.findViewById(R.id.tv_jdg_tol);
                                    TextView tv_jdg_exc = rowView.findViewById(R.id.tv_jdg_exc);
                                    TextView tv_jdg_comb = rowView.findViewById(R.id.tv_jdg_comb);
                                    TextView tv_jdg_diff = rowView.findViewById(R.id.tv_jdg_diff);

                                    setColorLinearBackground(linear_comb,combination_color);
                                    setColorLinearBackground(linear_diff,difficulty_color);

                                    if(judge_type.equals("Senior Judge")){
                                        tv_jdg_name.setText(judge_no+". Self");
                                    }else {
                                        tv_jdg_name.setText(judge_no+". "+name);
                                    }
                                    tv_jdg_tol.setText(total_score);
                                    tv_jdg_exc.setText(execution);
                                    tv_jdg_diff.setText(difficulty);
                                    tv_jdg_comb.setText(combination);

                                    linear_main.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            try {
                                                BottomSheetAllJudgeScore(tv_jdg_name.getText().toString(),data_obj);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    linear_all_judge.addView(rowView);
                                }
                            }else{
                                card_judge_score.setVisibility(View.GONE);
                                DisplayToastError(ScoreResultActivity.this,message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        loadingDialog.dismissDialog();
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
                            DisplayToastError(ScoreResultActivity.this,"Server is not connected to internet.");
                        } else if (error instanceof AuthFailureError) {
                            DisplayToastError(ScoreResultActivity.this,"Server couldn't find the authenticated request.");
                        } else if (error instanceof ServerError) {
                            DisplayToastError(ScoreResultActivity.this,"Server is not responding.Please try Again Later");
                        } else if (error instanceof NetworkError) {
                            DisplayToastError(ScoreResultActivity.this,"Your device is not connected to internet.");
                        } else if (error instanceof ParseError) {
                            DisplayToastError(ScoreResultActivity.this,"Parse Error (because of invalid json or xml).");
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("team_id", team_id);
                map.put("player_id", player_id);
                map.put("competition_year", Str_year);
                Log.d("GETAllPYRSCR_Param",""+map.toString());
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

    private void setColorLinearBackground(LinearLayout linearLayout, String bcakground_color) {
        if(bcakground_color.equals("green")){
            linearLayout.setBackground(getDrawable(R.color.green));
        }else{
            linearLayout.setBackground(getDrawable(R.color.red_pink));
        }
    }

    private void addQuestionsList() {

        loadingDialog.startLoadingDialog();

        String GETQUE_URL = ROOT_URL+"get_questions";

        Log.d("GETQUE_URL",""+GETQUE_URL);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GETQUE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingDialog.dismissDialog();
                        Log.d("Response",""+response);
                        try {
                            JSONObject questionlistObj = new JSONObject(response);
                            String status = questionlistObj.getString("success");
                            String message = questionlistObj.getString("message");
                            if(status.equals("true")){
                                questinListArry = questionlistObj.getJSONArray("data");
                                for(int i=0; i< questinListArry.length();i++){
                                    JSONObject quedata_obj = questinListArry.getJSONObject(i);
                                    String que_question = quedata_obj.getString("question");
                                    String que_marks = quedata_obj.getString("marks");

                                    JSONArray set_combArry = new JSONArray(set_comb);
                                    String value = set_combArry.getString(i);

                                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View rowView = inflater.inflate(R.layout.botom_question_list_layout, null);

                                    TextView tv_botom_que_name = rowView.findViewById(R.id.tv_botom_que_name);
                                    TextView tv_botom_tot_marks = rowView.findViewById(R.id.tv_botom_tot_marks);
                                    TextView tv_botom_obt_marks = rowView.findViewById(R.id.tv_botom_obt_marks);

                                    tv_botom_que_name.setText(que_question);
                                    tv_botom_tot_marks.setText(que_marks);

                                    BigDecimal bd = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
                                    tv_botom_obt_marks.setText(bd.toString());

                                    linear_questions.addView(rowView);
                                }

                                JSONArray elementArry = questionlistObj.getJSONArray("element");
                                for(int k=0; k< elementArry.length();k++){
                                    JSONObject elementObj = elementArry.getJSONObject(k);
                                    String element_name = elementObj.getString("element_name");
                                    String marks = elementObj.getString("marks");

                                    marks = marks.replace("0.","");

                                    if(element_name.equals("A")){
                                        tv_sr_a_marks.setText("0."+marks+"*");
                                    }else if(element_name.equals("B")){
                                        tv_sr_b_marks.setText("0."+marks+"*");
                                    }else if(element_name.equals("C")){
                                        tv_sr_c_marks.setText("0."+marks+"*");
                                    }
                                }
                            }else{
                                DisplayToastError(ScoreResultActivity.this,message);
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
                            DisplayToastError(ScoreResultActivity.this,"Server is not connected to internet.");
                        } else if (error instanceof AuthFailureError) {
                            DisplayToastError(ScoreResultActivity.this,"Server couldn't find the authenticated request.");
                        } else if (error instanceof ServerError) {
                            DisplayToastError(ScoreResultActivity.this,"Server is not responding.Please try Again Later");
                        } else if (error instanceof NetworkError) {
                            DisplayToastError(ScoreResultActivity.this,"Your device is not connected to internet.");
                        } else if (error instanceof ParseError) {
                            DisplayToastError(ScoreResultActivity.this,"Parse Error (because of invalid json or xml).");
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("gender", player_gender);
                map.put("age_group", player_group);
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

    private void BottomSheetAllJudgeScore(String name, JSONObject data_obj) throws JSONException {

        bottomSheetDialog = new BottomSheetDialog(ScoreResultActivity.this);
        bottomSheetDialog.setContentView(R.layout.bottomsheet_judge_score);
        bottomSheetDialog.show();

        TextView tv_bt_all_judge = bottomSheetDialog.findViewById(R.id.tv_bt_all_judge);
        TextView tv_botom_a = bottomSheetDialog.findViewById(R.id.tv_botom_a);
        TextView tv_botom_b = bottomSheetDialog.findViewById(R.id.tv_botom_b);
        TextView tv_botom_c = bottomSheetDialog.findViewById(R.id.tv_botom_c);
        TextView tv_botom_a_marks = bottomSheetDialog.findViewById(R.id.tv_botom_a_marks);
        TextView tv_botom_b_marks = bottomSheetDialog.findViewById(R.id.tv_botom_b_marks);
        TextView tv_botom_c_marks = bottomSheetDialog.findViewById(R.id.tv_botom_c_marks);
        LinearLayout linear_questions = bottomSheetDialog.findViewById(R.id.linear_questions);
        TextView tv_botom_comb = bottomSheetDialog.findViewById(R.id.tv_botom_comb);
        TextView tv_botom_diff_comb = bottomSheetDialog.findViewById(R.id.tv_botom_diff_comb);
        TextView tv_botom_exc = bottomSheetDialog.findViewById(R.id.tv_botom_exc);
        TextView tv_botom_orig = bottomSheetDialog.findViewById(R.id.tv_botom_orig);
        TextView tv_botom_final_value = bottomSheetDialog.findViewById(R.id.tv_botom_final_value);
        TextView tv_botom_comment = bottomSheetDialog.findViewById(R.id.tv_botom_comment);
        TextView tv_botom_cancel = bottomSheetDialog.findViewById(R.id.tv_botom_cancel);
        TextView tv_botom_Edit = bottomSheetDialog.findViewById(R.id.tv_botom_Edit);
        LinearLayout linear_bottomnav = bottomSheetDialog.findViewById(R.id.linear_bottomnav);

        if(!is_visible){
            linear_bottomnav.setVisibility(View.GONE);
        }

        tv_bt_all_judge.setText(name);
        tv_botom_a.setText(data_obj.getString("element_A"));
        tv_botom_b.setText(data_obj.getString("element_B"));
        tv_botom_c.setText(data_obj.getString("element_C"));
        tv_botom_a_marks.setText("0."+UnitAValue);
        tv_botom_b_marks.setText("0."+UnitBValue);
        tv_botom_c_marks.setText("0."+UnitCValue);
        tv_botom_comb.setText(data_obj.getString("combination"));
        tv_botom_diff_comb.setText(data_obj.getString("difficulty"));
        tv_botom_exc.setText(data_obj.getString("execution"));
        tv_botom_orig.setText(data_obj.getString("originality"));
        tv_botom_final_value.setText(data_obj.getString("total_score"));
        tv_botom_comment.setText(checkNullExcHandler(data_obj.getString("comment")));

        tv_botom_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        tv_botom_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
               Intent i = new Intent(ScoreResultActivity.this,ScoreForm.class);
                try {
                    i.putExtra("score_id",data_obj.getString("score_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                i.putExtra("player_id",player_id);
                i.putExtra("player_name",player_name);
                i.putExtra("team_name",team_name);
                i.putExtra("team_id",team_id);
                i.putExtra("player_age",player_age);
                i.putExtra("player_gender",player_gender);
                i.putExtra("player_group",player_group);
               startActivity(i);
            }
        });

        String combination_json = data_obj.getString("combination_json");

        combination_json = combination_json.replace("{","[");
        combination_json = combination_json.replace("}","]");

        if(questinListArry != null && questinListArry.length() > 0){
            for(int i=0; i< questinListArry.length();i++){
                JSONObject quedata_obj = questinListArry.getJSONObject(i);
                String que_question = quedata_obj.getString("question");
                String que_marks = quedata_obj.getString("marks");

                JSONArray set_combArry = new JSONArray(combination_json);
                String value = set_combArry.getString(i);

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.botom_question_list_layout, null);

                TextView tv_botom_que_name = rowView.findViewById(R.id.tv_botom_que_name);
                TextView tv_botom_tot_marks = rowView.findViewById(R.id.tv_botom_tot_marks);
                TextView tv_botom_obt_marks = rowView.findViewById(R.id.tv_botom_obt_marks);

                tv_botom_que_name.setText(que_question);
                tv_botom_tot_marks.setText(que_marks);

                BigDecimal bd = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
                tv_botom_obt_marks.setText(bd.toString());

                linear_questions.addView(rowView);
            }
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
                                getPlayerScore();
                                if(Str_judgetype.equals("Senior Judge")){
                                    GetAllJudgeScore();
                                }
                            }else{
                                DisplayToastError(ScoreResultActivity.this,"Sorry For the Inconvince please try again later..");
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
                            DisplayToastError(ScoreResultActivity.this,"Server is not connected to internet.");
                        } else if (error instanceof AuthFailureError) {
                            DisplayToastError(ScoreResultActivity.this,"Server couldn't find the authenticated request.");
                        } else if (error instanceof ServerError) {
                            DisplayToastError(ScoreResultActivity.this,"Server is not responding.Please try Again Later");
                        } else if (error instanceof NetworkError) {
                            DisplayToastError(ScoreResultActivity.this,"Your device is not connected to internet.");
                        } else if (error instanceof ParseError) {
                            DisplayToastError(ScoreResultActivity.this,"Parse Error (because of invalid json or xml).");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ScoreResultActivity.this, TeamDetails.class);
        i.putExtra("team_id",team_id);
        i.putExtra("team_name",team_name);
        i.putExtra("team_gender",player_gender);
        i.putExtra("team_group",player_group);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}