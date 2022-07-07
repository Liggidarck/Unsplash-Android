package com.george.unsplash.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

    public boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void showAlertDialog(Context context, int codeError) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Ошибка!")
                .setMessage("При выполнении запроса произошла ошибка! Код ошибки: " + codeError)
                .setPositiveButton("Ок", (dialog, id) -> {
                    dialog.cancel();
                })
                .create()
                .show();
    }

}
