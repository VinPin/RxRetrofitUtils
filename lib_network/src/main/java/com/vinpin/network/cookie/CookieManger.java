package com.vinpin.network.cookie;

import android.content.Context;
import android.content.SharedPreferences;

import com.vinpin.network.RxRetrofitUtils;

import java.util.HashSet;

/**
 * CookieManger来管理cookies
 *
 * @author vinpin
 *         create at 2018/03/20 9:57
 */
public class CookieManger {

    private static final String FILLNAME = "cookies";
    private static final String COOKIE_PREFS = "Cookies_Prefs";

    private CookieManger() {
    }

    public static CookieManger getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final CookieManger sInstance = new CookieManger();
    }

    public HashSet<String> getCookies() {
        HashSet<String> cookies = new HashSet<>();
        SharedPreferences sharedPreferences = RxRetrofitUtils.getContext().getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        cookies.addAll(sharedPreferences.getStringSet(COOKIE_PREFS, new HashSet<String>()));
        return cookies;
    }

    public void saveCookies(HashSet<String> cookies) {
        if (cookies != null && !cookies.isEmpty()) {
            SharedPreferences sharedPreferences = RxRetrofitUtils.getContext().getApplicationContext().getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
            sharedPreferences.edit().putStringSet(COOKIE_PREFS, cookies).apply();
        }
    }
}
