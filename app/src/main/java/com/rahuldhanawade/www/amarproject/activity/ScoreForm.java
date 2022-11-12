package com.rahuldhanawade.www.amarproject.activity;

import static com.rahuldhanawade.www.amarproject.RestClient.RestClient.ROOT_URL;
import static com.rahuldhanawade.www.amarproject.Utils.CommonMethods.DisplayToastError;
import static com.rahuldhanawade.www.amarproject.Utils.CommonMethods.DisplayToastSuccess;
import static com.rahuldhanawade.www.amarproject.Utils.CommonMethods.checkNullExcHandler;
import static com.rahuldhanawade.www.amarproject.Utils.CommonMethods.getCapsSentences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.rahuldhanawade.www.amarproject.R;
import com.rahuldhanawade.www.amarproject.Utils.LoadingDialog;
import com.rahuldhanawade.www.amarproject.Utils.MyValidator;
import com.rahuldhanawade.www.amarproject.Utils.UtilitySharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ScoreForm extends AppCompatActivity {

    private LoadingDialog loadingDialog;
    private static final String TAG = ScoreForm.class.getSimpleName();
    String Str_token = "",Str_year = "",player_id = "",player_name = "",team_id = "",team_name = "",player_age = "",player_gender = "",player_group = "";
    EditText edt_exec,edt_orig,edt_comments;
    TextView tv_plyr_name,tv_plyr_age,tv_plyr_goup,tv_plyr_gender;
    TextView tv_diff_comb,tv_comb,tv_final_value,tv_a,tv_b,tv_c,tv_submit;
    TextView tv_a_marks,tv_b_marks,tv_c_marks;
    ImageView iv_a_minus,iv_a_add,iv_b_minus,iv_b_add,iv_c_minus,iv_c_add,iv_record;
    boolean isUserNew = true;
    Dialog dialogRecogAud;
    int CombValue = 0, OtherValue = 0;
    int MinAValue = 0,MinBValue = 0,MinCValue = 0;
    int MaxAValue = 0,MaxBValue = 0,MaxCValue = 0;
    int UnitAValue = 0,UnitBValue = 0,UnitCValue = 0;

    LinearLayout linear_questions;
    ArrayList<String> marklist = new ArrayList<>();

    public static final Integer RecordAudioRequestCode = 1;

    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_form);

        loadingDialog = new LoadingDialog(ScoreForm.this);

        player_id = getIntent().getStringExtra("player_id");
        player_name = getIntent().getStringExtra("player_name");
        team_id = getIntent().getStringExtra("team_id");
        team_name = getIntent().getStringExtra("team_name");
        player_age = getIntent().getStringExtra("player_age");
        player_gender = getIntent().getStringExtra("player_gender");
        player_group = getIntent().getStringExtra("player_group");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_score_form);
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

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
                showfilterdialogg();
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                iv_record.setImageResource(R.drawable.ic_mic);
                ArrayList<String> arrayList = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                edt_comments.setText(arrayList.get(0));
                dialogRecogAud.dismiss();
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(){

        tv_plyr_name = findViewById(R.id.tv_plyr_name);
        tv_plyr_age = findViewById(R.id.tv_plyr_age);
        tv_plyr_goup = findViewById(R.id.tv_plyr_goup);
        tv_plyr_gender = findViewById(R.id.tv_plyr_gender);

        tv_a_marks = findViewById(R.id.tv_a_marks);
        tv_b_marks = findViewById(R.id.tv_b_marks);
        tv_c_marks = findViewById(R.id.tv_c_marks);

        tv_plyr_name.setText(getCapsSentences(player_name));
        tv_plyr_age.setText(player_age);
        tv_plyr_goup.setText(getCapsSentences(player_group));
        tv_plyr_gender.setText(getCapsSentences(player_gender));

        iv_record = findViewById(R.id.iv_record);
        edt_comments = findViewById(R.id.edt_comments);
        tv_comb = findViewById(R.id.tv_comb);
        tv_diff_comb = findViewById(R.id.tv_diff_comb);
        tv_final_value = findViewById(R.id.tv_final_value);

        linear_questions = findViewById(R.id.linear_questions);
        edt_exec = findViewById(R.id.edt_exec);
        addTextChageOtherValue(edt_exec,440);
        edt_orig = findViewById(R.id.edt_orig);
        addTextChageOtherValue(edt_orig,20);

        iv_a_minus = findViewById(R.id.iv_a_minus);
        tv_a = findViewById(R.id.tv_a);
        iv_a_add = findViewById(R.id.iv_a_add);
        iv_a_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int minValue = MinAValue;
                int maxValue = MaxAValue;
                int unitValue = UnitAValue;

                setValueInDifficulty(false,tv_a,minValue,maxValue,unitValue);
            }
        });
        iv_a_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int minValue = MinAValue;
                int maxValue = MaxAValue;
                int unitValue = UnitAValue;

                setValueInDifficulty(true,tv_a,minValue,maxValue,unitValue);
            }
        });

        iv_b_minus = findViewById(R.id.iv_b_minus);
        tv_b = findViewById(R.id.tv_b);
        iv_b_add = findViewById(R.id.iv_b_add);
        iv_b_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int minValue = MinBValue;
                int maxValue = MaxBValue;
                int unitValue = UnitBValue;

                setValueInDifficulty(false,tv_b,minValue,maxValue,unitValue);
            }
        });
        iv_b_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int minValue = MinBValue;
                int maxValue = MaxBValue;
                int unitValue = UnitBValue;

                setValueInDifficulty(true,tv_b,minValue,maxValue,unitValue);
            }
        });

        iv_c_minus = findViewById(R.id.iv_c_minus);
        tv_c = findViewById(R.id.tv_c);
        iv_c_add = findViewById(R.id.iv_c_add);
        iv_c_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int minValue = MinCValue;
                int maxValue = MaxCValue;
                int unitValue = UnitCValue;

                setValueInDifficulty(false,tv_c,minValue,maxValue,unitValue);
            }
        });
        iv_c_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int minValue = MinCValue;
                int maxValue = MaxCValue;
                int unitValue = UnitCValue;

                setValueInDifficulty(true,tv_c,minValue,maxValue,unitValue);
            }
        });

        iv_record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(ContextCompat.checkSelfPermission(ScoreForm.this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED){
                    checkPermission();
                }else{
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        speechRecognizer.stopListening();
                    }

                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        iv_record.setImageResource(R.drawable.ic_mic);
                        final Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                        speechRecognizer.startListening(speechIntent);
                    }
                }
                return false;
            }
        });

        tv_submit = findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()){
                    UserLogin();
                }
            }
        });
        UserLogin();
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
                                String data = questionlistObj.getString("data");
                                JSONObject dataObj = new JSONObject(data);
                                JSONArray questinListArry = dataObj.getJSONArray("data");
                                for(int i=0; i< questinListArry.length();i++){
                                    JSONObject data_obj = questinListArry.getJSONObject(i);
                                    String que_question = data_obj.getString("question");
                                    String que_marks = data_obj.getString("marks");

                                    que_marks = que_marks.replace("0.","");

                                    int k = i;
                                    marklist.add("0.0");

                                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View rowView = inflater.inflate(R.layout.question_list_layout, null);

                                    TextView tv_que_name = rowView.findViewById(R.id.tv_que_name);
                                    TextView tv_que_marks = rowView.findViewById(R.id.tv_que_marks);
                                    CheckBox chk_ans = rowView.findViewById(R.id.chk_ans);

                                    tv_que_name.setText(que_question);
                                    tv_que_marks.setText("0."+que_marks);

                                    String finalQue_marks = que_marks;

                                    chk_ans.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                            setComCalulation(isChecked,Integer.parseInt(finalQue_marks));
                                            setQuestionListMarkArray(k,isChecked, finalQue_marks);
                                        }
                                    });

                                    linear_questions.addView(rowView);
                                }

                                JSONArray elementArry = dataObj.getJSONArray("element");
                                for(int k=0; k< elementArry.length();k++){
                                    JSONObject elementObj = elementArry.getJSONObject(k);
                                    String element_name = elementObj.getString("element_name");
                                    String min = elementObj.getString("min");
                                    String max = elementObj.getString("max");
                                    String marks = elementObj.getString("marks");

                                    marks = marks.replace("0.","");

                                    if(element_name.equals("A")){
                                        tv_a_marks.setText("0."+marks+"*");
                                        MinAValue = Integer.parseInt(min);
                                        MaxAValue = Integer.parseInt(max);
                                        UnitAValue = Integer.parseInt(marks);
                                    }else if(element_name.equals("B")){
                                        tv_b_marks.setText("0."+marks+"*");
                                        MinBValue = Integer.parseInt(min);
                                        MaxBValue = Integer.parseInt(max);
                                        UnitBValue = Integer.parseInt(marks);
                                    }else if(element_name.equals("C")){
                                        tv_c_marks.setText("0."+marks+"*");
                                        MinBValue = Integer.parseInt(min);
                                        MaxCValue = Integer.parseInt(max);
                                        UnitCValue = Integer.parseInt(marks);
                                    }
                                }
                            }else{
                                DisplayToastError(ScoreForm.this,message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                        loadingDialog.dismissDialog();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            DisplayToastError(ScoreForm.this,"Server is not connected to internet.");
                        } else if (error instanceof AuthFailureError) {
                            DisplayToastError(ScoreForm.this,"Server couldn't find the authenticated request.");
                        } else if (error instanceof ServerError) {
                            DisplayToastError(ScoreForm.this,"Server is not responding.Please try Again Later");
                        } else if (error instanceof NetworkError) {
                            DisplayToastError(ScoreForm.this,"Your device is not connected to internet.");
                        } else if (error instanceof ParseError) {
                            DisplayToastError(ScoreForm.this,"Parse Error (because of invalid json or xml).");
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

    private void setQuestionListMarkArray(int k, boolean isChecked, String que_marks) {

        if(isChecked){
            marklist.remove(k);
            marklist.add(k,"0."+que_marks);
        }else{
            marklist.remove(k);
            marklist.add(k,"0.0");
        }
        Log.d(TAG, "setQuestionListMarkArray: "+marklist);
    }

    private void setValueInDifficulty(boolean isboolean, TextView textView, int minValue, int maxValue, int unitValue) {
        int crtValue = 0;
        String value = textView.getText().toString();
        if(value.equals("0")){
            crtValue = 0;
        }else {
            crtValue = Integer.parseInt(textView.getText().toString());
        }

        setOtherCalulation(false,crtValue*unitValue);

        if(isboolean){
            if(crtValue >= minValue && crtValue < maxValue){
                crtValue = crtValue + 1;
            }else{
                DisplayToastError(ScoreForm.this,"You can only select min "+minValue+" & max "+maxValue+" value.");
            }
        }else {
            if(crtValue > minValue && crtValue <= maxValue){
                crtValue = crtValue - 1;
            }else{
                DisplayToastError(ScoreForm.this,"You can only select min "+minValue+" & max "+maxValue+" value.");
            }
        }

        textView.setText(String.valueOf(crtValue));
        setDiffCalculation();
        setOtherCalulation(true,crtValue*unitValue);
    }

    private void setDiffCalculation() {
        int a = 0, b = 0, c = 0;

        String value_a = tv_a.getText().toString();
        String value_b = tv_b.getText().toString();
        String value_c = tv_c.getText().toString();

        a = Integer.parseInt(value_a) * UnitAValue;
        b = Integer.parseInt(value_b) * UnitBValue;
        c = Integer.parseInt(value_c) * UnitCValue;

        int intValue = a + b + c;
        double finalValue = (double) intValue / 100;
        BigDecimal bd = new BigDecimal(finalValue).setScale(2, RoundingMode.HALF_UP);
        tv_diff_comb.setText(String.valueOf(bd));
    }

    private void addTextChageOtherValue(EditText editText, int value) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str_value = charSequence.toString();
                str_value = str_value.replace(".","");
                if(str_value.length() == 3){
                    if(Integer.parseInt(str_value) <= value){
                        setOtherCalulation(false,Integer.parseInt(str_value));
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str_value = charSequence.toString();
                str_value = str_value.replace(".","");
                if(str_value.length() > 0){
                    if(!(Integer.parseInt(str_value) <= value)){
                        DisplayToastError(ScoreForm.this,"Please enter valid marks");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str_value = editable.toString();
                str_value = str_value.replace(".","");
                if(str_value.length() == 3){
                    if(Integer.parseInt(str_value) <= value){
                        setOtherCalulation(true,Integer.parseInt(str_value));
                    }
                }
            }
        });
    }

    private void setOtherCalulation(boolean isChecked, int i) {
        if (isChecked) {
            if (OtherValue == 0){
                OtherValue = i;
            }else{
                OtherValue = OtherValue + i;
            }
        } else {
            if (OtherValue == 0){
                OtherValue = 0;
            }else{
                OtherValue = OtherValue - i;
            }
        }

        setFinalCalculation(CombValue,OtherValue);
    }

    private void setFinalCalculation(int combValue, int otherValue) {

        int intValue = combValue + otherValue;
        double finalValue = (double) intValue / 100;
        BigDecimal bd = new BigDecimal(finalValue).setScale(2, RoundingMode.HALF_UP);
        tv_final_value.setText(String.valueOf(bd));
    }

    private void setComCalulation(boolean isChecked, int i) {

        if (isChecked) {
            if (CombValue == 0){
                CombValue = i;
            }else{
                CombValue = CombValue + i;
            }
        } else {
            if (CombValue == 0){
                CombValue = 0;
            }else{
                CombValue = CombValue - i;
            }
        }
        double combValue = (double) CombValue / 100;
        BigDecimal bd = new BigDecimal(combValue).setScale(2, RoundingMode.HALF_UP);
        tv_comb.setText(String.valueOf(bd));
        setFinalCalculation(CombValue,OtherValue);
    }

    private void checkPermission(){
       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
           ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},
                   RecordAudioRequestCode);
       }
    }

    private void showfilterdialogg() {
        dialogRecogAud = new Dialog(ScoreForm.this);
        dialogRecogAud.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogRecogAud.setCanceledOnTouchOutside(true);
        dialogRecogAud.setCancelable(true);
        dialogRecogAud.setContentView(R.layout.pop_up_recog);
        Objects.requireNonNull(dialogRecogAud.getWindow()).setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogRecogAud.show();
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
                                if(isUserNew){
                                    addQuestionsList();
                                }else{
                                    SubmitScoreForm();
                                }
                                isUserNew = false;
                            }else{
                                DisplayToastError(ScoreForm.this,"Sorry For the Inconvince please try again later..");
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
                            DisplayToastError(ScoreForm.this,"Server is not connected to internet.");
                        } else if (error instanceof AuthFailureError) {
                            DisplayToastError(ScoreForm.this,"Server couldn't find the authenticated request.");
                        } else if (error instanceof ServerError) {
                            DisplayToastError(ScoreForm.this,"Server is not responding.Please try Again Later");
                        } else if (error instanceof NetworkError) {
                            DisplayToastError(ScoreForm.this,"Your device is not connected to internet.");
                        } else if (error instanceof ParseError) {
                            DisplayToastError(ScoreForm.this,"Parse Error (because of invalid json or xml).");
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

    private boolean isValid() {
        boolean result = true;

        String Str_exec = edt_exec.getText().toString();
        String Str_orig = edt_orig.getText().toString();

        if (!Str_exec.contains(".") && Str_exec.length() != 4) {
            DisplayToastError(ScoreForm.this,"Please Enter value in Decimals Ex: 0.20");
            result = false;
        }

        if (!Str_orig.contains(".") && Str_orig.length() != 4) {
            DisplayToastError(ScoreForm.this,"Please Enter value in Decimals Ex: 0.20");
            result = false;
        }

        Str_exec = Str_exec.replace(".","");
        if(Str_exec.length() == 3){
            if(!(Integer.parseInt(Str_exec) <= 440)){
                DisplayToastError(ScoreForm.this,"Please Enter Valid Value");
                result = false;
            }
        }

        Str_orig = Str_orig.replace(".","");
        if(Str_orig.length() == 3){
            if(!(Integer.parseInt(Str_orig) <= 20)){
                DisplayToastError(ScoreForm.this,"Please Enter Valid Value");
                result = false;
            }
        }

        return result;
    }

    private void SubmitScoreForm() {

        String Str_marklist = marklist.toString().replace("[","{");
        String combination_json = Str_marklist.replace("]","}");

        Log.d(TAG, "SubmitScoreForm: "+combination_json);

        loadingDialog.startLoadingDialog();

        String SubmitScore_URL = ROOT_URL+"submit_player_score";

        Log.d("SubmitScore_URL",""+SubmitScore_URL);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SubmitScore_URL,
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
                                DisplayToastSuccess(ScoreForm.this,message);
                                Intent i = new Intent(ScoreForm.this,TeamDetails.class);
                                i.putExtra("team_id",team_id);
                                i.putExtra("team_name",team_name);
                                i.putExtra("team_gender",player_gender);
                                i.putExtra("team_group",player_group);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }else{
                                DisplayToastError(ScoreForm.this,message);
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
                            DisplayToastError(ScoreForm.this,"Server is not connected to internet.");
                        } else if (error instanceof AuthFailureError) {
                            DisplayToastError(ScoreForm.this,"Server couldn't find the authenticated request.");
                        } else if (error instanceof ServerError) {
                            DisplayToastError(ScoreForm.this,"Server is not responding.Please try Again Later");
                        } else if (error instanceof NetworkError) {
                            DisplayToastError(ScoreForm.this,"Your device is not connected to internet.");
                        } else if (error instanceof ParseError) {
                            DisplayToastError(ScoreForm.this,"Parse Error (because of invalid json or xml).");
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("team_id", team_id);
                map.put("judge_id", UtilitySharedPreferences.getPrefs(getApplicationContext(),"user_id"));
                map.put("judge_no", UtilitySharedPreferences.getPrefs(getApplicationContext(),"user_judge_no"));
                map.put("player_id", player_id);
                map.put("competition_year", Str_year);
                map.put("element_A", tv_a.getText().toString());
                map.put("element_B", tv_b.getText().toString());
                map.put("element_C", tv_c.getText().toString());
                map.put("combination_json", combination_json);
                map.put("combination", checkEmptyValue(tv_comb.getText().toString()));
                map.put("difficulty", checkEmptyValue(tv_diff_comb.getText().toString()));
                map.put("execution", checkEmptyValue(edt_exec.getText().toString()));
                map.put("originality", checkEmptyValue(edt_orig.getText().toString()));
                map.put("total_score", checkEmptyValue(tv_final_value.getText().toString()));
                map.put("comment", edt_comments.getText().toString());
                Log.d("SubmitParamas",""+map.toString());
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

    public static String checkEmptyValue(String value){

        String str_value = "0.0";

        if(value != null && !value.equals("") && !value.equals("null")){
            return value;
        }else {
            return str_value;
        }

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        speechRecognizer.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == RecordAudioRequestCode && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                DisplayToastSuccess(ScoreForm.this,"Permission Granted");
            }
        }
    }
}