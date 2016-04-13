package com.gambit.sdk.example.table.subscriptions;

import com.gambit.sdk.message.GambitMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class GambitSavedSubscription implements Comparable<GambitSavedSubscription> {

    protected GambitSubscription subscribtion;
    protected Date created;

    public GambitSavedSubscription() {
    }

    public GambitSavedSubscription(GambitSubscription subscribtion) {
        this.subscribtion = subscribtion;
        this.created = new Date();
    }

    public GambitSavedSubscription(Date created, GambitSubscription subscribtion) {
        this.created = created;
        this.subscribtion = subscribtion;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        df.setTimeZone(tz);
        String date_iso = df.format(getCreated());

        json.put("created", date_iso);
        json.put("subscribtion", getSubscribtion().toJson());

        return json;
    }

    public static GambitSavedSubscription fromJson(JSONObject json) throws ParseException {

        GambitSavedSubscription object = null;

        if (json.has("created") && json.has("subscribtion")) {
            String date_iso = json.getString("created");
            JSONObject subscribtionJson = json.getJSONObject("subscribtion");

            Date created = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(date_iso);

            GambitSubscription subscribtion = new GambitSubscription(subscribtionJson.toString());

            object = new GambitSavedSubscription(created, subscribtion);
        }

        return object;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    @Override
    public int compareTo(GambitSavedSubscription o) {
        return o.getCreated().compareTo(getCreated());
    }


    public GambitSubscription getSubscribtion() {
        return subscribtion;
    }

    public void setSubscribtion(GambitSubscription subscribtion) {
        this.subscribtion = subscribtion;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
