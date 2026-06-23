package com.openconsent.sdk;

public class ConsentEvent {

    private String userId;
    private String appId;
    private String action;
    private String purpose;
    private String policyVersion;
    private long timestamp;

    public ConsentEvent(String userId, String appId, String action, String purpose, String policyVersion, long timestamp) {
        this.userId = userId;
        this.appId = appId;
        this.action = action;
        this.purpose = purpose;
        this.policyVersion = policyVersion;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getAppId() {
        return appId;
    }

    public String getAction() {
        return action;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getPolicyVersion() {
        return policyVersion;
    }

    public long getTimestamp() {
        return timestamp;
    }
}