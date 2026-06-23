package com.openconsent.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.VisibleForTesting;
import com.google.gson.Gson;
import java.util.List;
import java.util.UUID;

public class ConsentManager {

    private static ConsentManager instance;
    private final ConsentStorage storage;
    private final String appId;
    private final Gson gson;
    private final String userId;

    private ConsentManager(Context context, String appId) {
        this.storage = new ConsentStorage(context.getApplicationContext());
        this.appId = appId;
        this.gson = new Gson();
        this.userId = resolveUserId(context.getApplicationContext());
    }

    // Genera un ID único por instalación y lo persiste
    private String resolveUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("openconsent", Context.MODE_PRIVATE);
        String id = prefs.getString("device_user_id", null);
        if (id == null) {
            id = "user_" + UUID.randomUUID().toString().substring(0, 8);
            prefs.edit().putString("device_user_id", id).apply();
        }
        return id;
    }

    public static ConsentManager getInstance(Context context, String appId) {
        if (instance == null) {
            instance = new ConsentManager(context, appId);
        }
        return instance;
    }

    // Devuelve el userId generado automáticamente
    public String getUserId() {
        return userId;
    }

    public void recordConsent(String purpose, String policyVersion) {
        ConsentEvent event = new ConsentEvent(
                userId, appId, "ACCEPTED", purpose, policyVersion,
                System.currentTimeMillis()
        );
        storage.saveEvent(event);
    }

    public void recordRejection(String purpose, String policyVersion) {
        ConsentEvent event = new ConsentEvent(
                userId, appId, "REJECTED", purpose, policyVersion,
                System.currentTimeMillis()
        );
        storage.saveEvent(event);
    }

    public void recordRevocation(String purpose, String policyVersion) {
        ConsentEvent event = new ConsentEvent(
                userId, appId, "REVOKED", purpose, policyVersion,
                System.currentTimeMillis()
        );
        storage.saveEvent(event);
    }

    public List<ConsentEvent> getHistory() {
        return storage.getAllEvents();
    }

    public void clearHistory() {
        storage.clearAll();
    }

    public boolean hasActiveConsent(String userId) {
        List<ConsentEvent> events = storage.getAllEvents();
        String lastAction = null;
        for (ConsentEvent event : events) {
            if (event.getUserId().equals(userId)) {
                lastAction = event.getAction();
            }
        }
        return "ACCEPTED".equals(lastAction);
    }

    public String exportLogsAsJson() {
        return gson.toJson(storage.getAllEvents());
    }

    public String exportLogsAsCsv() {
        List<ConsentEvent> events = storage.getAllEvents();
        StringBuilder sb = new StringBuilder();
        sb.append("userId,appId,action,purpose,policyVersion,timestamp\n");
        for (ConsentEvent e : events) {
            sb.append(e.getUserId()).append(",")
                    .append(e.getAppId()).append(",")
                    .append(e.getAction()).append(",")
                    .append(e.getPurpose()).append(",")
                    .append(e.getPolicyVersion()).append(",")
                    .append(e.getTimestamp()).append("\n");
        }
        return sb.toString();
    }
    @VisibleForTesting
    public static void resetInstance() {
        instance = null;
    }
}