package com.rahuldhanawade.www.amarproject.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.Window;

import com.rahuldhanawade.www.amarproject.R;

import java.util.Objects;

public class LoadingDialog {

    Activity activity;
    Dialog dialog;

    public LoadingDialog(Activity myActivity){
        activity = myActivity;
    }

    public void startLoadingDialog(){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}
