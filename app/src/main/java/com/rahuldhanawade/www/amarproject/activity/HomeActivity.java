package com.rahuldhanawade.www.amarproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rahuldhanawade.www.amarproject.R;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    LinearLayout linear_main;
    FloatingActionButton flt_fliter;
    Dialog dialogFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        linear_main = findViewById(R.id.linear_main);
        linear_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, ListActivity.class);
                startActivity(i);
            }
        });

        flt_fliter = findViewById(R.id.flt_fliter);
        flt_fliter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showfilterdialogg();
            }
        });

    }

    private void showfilterdialogg() {
        dialogFilter = new Dialog(HomeActivity.this);
        dialogFilter.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogFilter.setCanceledOnTouchOutside(true);
        dialogFilter.setCancelable(true);
        dialogFilter.setContentView(R.layout.pop_up_filter_dashboard);
        Objects.requireNonNull(dialogFilter.getWindow()).setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogFilter.show();
    }
}