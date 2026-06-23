package com.openconsent.sdk;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConsentStorage {

    private static final String PREFS_NAME = "openconsent_prefs";
    private static final String KEY_EVENTS = "consent_events";

    private final SharedPreferences prefs;
    private final Gson gson;

    public ConsentStorage(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void saveEvent(ConsentEvent event) {
        List<ConsentEvent> events = getAllEvents();
        events.add(event);
        String json = gson.toJson(events);
        prefs.edit().putString(KEY_EVENTS, json).apply();
    }

    public List<ConsentEvent> getAllEvents() {
        String json = prefs.getString(KEY_EVENTS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<ConsentEvent>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void clearAll() {
        prefs.edit().remove(KEY_EVENTS).apply();
    }
}