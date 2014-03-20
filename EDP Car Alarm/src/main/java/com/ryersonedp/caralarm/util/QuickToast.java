package com.ryersonedp.caralarm.util;


import android.content.Context;

public class QuickToast {

    public static void makeToast(Context context, String toastMessage) {
        android.widget.Toast.makeText(context, toastMessage, android.widget.Toast.LENGTH_SHORT).show();
    }
}

