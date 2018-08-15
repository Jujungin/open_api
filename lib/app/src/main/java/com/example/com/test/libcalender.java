package com.example.com.test;

/**
 * Created by jujungin on 2018-08-14.
 */

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

public class libcalender {
    public void showMyToast(Context context, Resources res) {
        Toast.makeText(context, res.getString(R.string.app_name), Toast.LENGTH_SHORT);
    }
}
