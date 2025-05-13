package com.example.aplicaciongestionstockimprenta;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "OdooSessionPrefs";
    private static final String KEY_SESSION_ID = "session_id";

    private static SessionManager instance;
    private SharedPreferences sharedPreferences;

    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SessionManager not initialized. Call SessionManager.init(context) first.");
        }
        return instance;
    }

    public void setSessionId(String sessionId) {
        sharedPreferences.edit().putString(KEY_SESSION_ID, sessionId).apply();
    }

    public String getSessionId() {
        return sharedPreferences.getString(KEY_SESSION_ID, null);
    }
}
