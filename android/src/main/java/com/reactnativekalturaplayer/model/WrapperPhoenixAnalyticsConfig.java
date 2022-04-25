package com.reactnativekalturaplayer.model;

import com.google.gson.JsonObject;
import com.kaltura.playkit.utils.Consts;

public class WrapperPhoenixAnalyticsConfig {

    public static final String PARTNER_ID = "partnerId";
    public static final String BASE_URL   = "baseUrl";
    public static final String KS         = "ks";
    public static final String TIMER_INTERVAL = "timerInterval";

    private int partnerId = -1;
    private String baseUrl = "";
    private String ks = "";
    private int timerInterval = Consts.DEFAULT_ANALYTICS_TIMER_INTERVAL_HIGH_SEC;


    public WrapperPhoenixAnalyticsConfig() {}

    public int getPartnerId() {
        return partnerId;
    }

    public String getKS() {
        return ks;
    }


    public String getBaseUrl() {
        return baseUrl;
    }

    public int getTimerInterval() {
        return timerInterval;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(PARTNER_ID, partnerId);
        jsonObject.addProperty(BASE_URL, baseUrl);
        jsonObject.addProperty(KS, ks == null ? "" : ks);
        jsonObject.addProperty(TIMER_INTERVAL, timerInterval);

        return jsonObject;
    }
}
