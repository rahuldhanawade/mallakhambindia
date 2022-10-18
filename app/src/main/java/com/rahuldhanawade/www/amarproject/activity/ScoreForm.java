package com.rahuldhanawade.www.amarproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.rahuldhanawade.www.amarproject.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ScoreForm extends AppCompatActivity {

    private static final String TAG = ScoreForm.class.getSimpleName();
    EditText edt_exec,edt_orig,edt_comments;
    TextView tv_diff_comb,tv_comb,tv_final_value,tv_a,tv_b,tv_c;
    ImageView iv_a_minus,iv_a_add,iv_b_minus,iv_b_add,iv_c_minus,iv_c_add,iv_record;
    Dialog dialogRecogAud;
    Switch swt_q1,swt_q2,swt_q3,swt_q4,swt_q5,swt_q6,swt_q7,swt_q8,swt_q9,swt_q10;
    int CombValue = 0, OtherValue = 0;
    int UnitAValue = 20,UnitBValue = 40,UnitCValue = 60;

    public static final Integer RecordAudioRequestCode = 1;

    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_form);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_score_form);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Team name");
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

    private void init(){
        iv_record = findViewById(R.id.iv_record);
        edt_comments = findViewById(R.id.edt_comments);
        tv_comb = findViewById(R.id.tv_comb);
        tv_diff_comb = findViewById(R.id.tv_diff_comb);
        tv_final_value = findViewById(R.id.tv_final_value);

        swt_q1 = findViewById(R.id.swt_q1);
        swt_q1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setComCalulation(isChecked,10);
            }
        });
        swt_q2 = findViewById(R.id.swt_q2);
        swt_q2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setComCalulation(isChecked,20);
            }
        });
        swt_q3 = findViewById(R.id.swt_q3);
        swt_q3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setComCalulation(isChecked,20);
            }
        });
        swt_q4 = findViewById(R.id.swt_q4);
        swt_q4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setComCalulation(isChecked,20);
            }
        });
        swt_q5 = findViewById(R.id.swt_q5);
        swt_q5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setComCalulation(isChecked,20);
            }
        });
        swt_q6 = findViewById(R.id.swt_q6);
        swt_q6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setComCalulation(isChecked,10);
            }
        });
        swt_q7 = findViewById(R.id.swt_q7);
        swt_q7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setComCalulation(isChecked,10);
            }
        });
        swt_q8 = findViewById(R.id.swt_q8);
        swt_q8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setComCalulation(isChecked,20);
            }
        });
        swt_q9 = findViewById(R.id.swt_q9);
        swt_q9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setComCalulation(isChecked,20);
            }
        });
        swt_q10 = findViewById(R.id.swt_q10);
        swt_q10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setComCalulation(isChecked,10);
            }
        });
        
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
                int minValue = 0;
                int maxValue = 4;
                int unitValue = UnitAValue;

                setValueInDifficulty(false,tv_a,minValue,maxValue,unitValue);
            }
        });
        iv_a_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int minValue = 0;
                int maxValue = 4;
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
                int minValue = 0;
                int maxValue = 6;
                int unitValue = UnitBValue;

                setValueInDifficulty(false,tv_b,minValue,maxValue,unitValue);
            }
        });
        iv_b_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int minValue = 0;
                int maxValue = 6;
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
                int minValue = 0;
                int maxValue = 1;
                int unitValue = UnitCValue;

                setValueInDifficulty(false,tv_c,minValue,maxValue,unitValue);
            }
        });
        iv_c_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int minValue = 0;
                int maxValue = 1;
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
                Toast.makeText(this, "You can only select min "+minValue+" & max "+maxValue+" value.", Toast.LENGTH_SHORT).show();
            }
        }else {
            if(crtValue > minValue && crtValue <= maxValue){
                crtValue = crtValue - 1;
            }else{
                Toast.makeText(this, "You can only select min "+minValue+" & max "+maxValue+" value.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ScoreForm.this, "Please enter valid marks", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}