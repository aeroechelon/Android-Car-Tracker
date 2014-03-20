package com.ryersonedp.caralarm.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogManager {

    AlertDialog alertDialog;
    /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     *               - pass null if you don't want icon
     * */
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(title); // Setting Dialog Title
        alertDialog.setMessage(message); // Setting Dialog Message
        CharSequence buttonText = "Thanks for the info!";

        // Setting OK Button
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, buttonText, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

}