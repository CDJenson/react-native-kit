package com.coship.rnkit.utils;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.widget.Toast;


/**
 *  author: zoujunda
 *  date: 2019/7/3 16:51
 *	version: 1.0
 *  description: One-sentence description
 */
public class Utils {

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        if (a != null && b != null) {
            int length = a.length();
            if (length == b.length()) {
                if (a instanceof String && b instanceof String) {
                    return a.equals(b);
                } else {
                    for (int i = 0; i < length; i++) {
                        if (a.charAt(i) != b.charAt(i)) return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @NonNull
    public static <T> T checkNotNull(@Nullable final T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }


    public static void safeToast(Context context, @StringRes int resId){
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
    }
}
