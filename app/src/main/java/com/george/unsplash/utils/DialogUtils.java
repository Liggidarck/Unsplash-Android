package com.george.unsplash.utils;

import android.app.AlertDialog;
import android.content.Context;

public class DialogUtils {

    public void showAlertDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Error!")
                .setMessage("An error occurred while executing the request!")
                .setPositiveButton("Ок", (dialog, id) -> dialog.cancel())
                .create()
                .show();
    }

}
