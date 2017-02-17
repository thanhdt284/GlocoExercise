package com.dpanic.glocoexercise.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.dpanic.glocoexercise.R;

/**
 * Created by dpanic on 17/02/2017.
 * Project: GlocoExercise
 */

public class PermissionUtils {
    private static final int PERMISSION_REQUEST_CODE = 100;

    public static void requestPermissions(final Context context, final String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(context, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                        permission)) {
                    showMessageDialog(context, context.getResources().getString(R.string.permission_explaination),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    performRequest(context, permission);
                                }
                            });
                } else {
                    performRequest(context, permission);
                }
            }
        }
    }

    private static void performRequest(Context context, String permission){
        ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, PERMISSION_REQUEST_CODE);
    }

    public static boolean isHasPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(context, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }
        }

        return true;
    }

    private static void showMessageDialog(Context context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context).setMessage(message).setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null).create().show();
    }
}
