package com.gambit.sdk.example.table.messages;

import com.gambit.sdk.message.GambitMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class GambitSavedMessage implements Comparable<GambitSavedMessage> {

    /**
     * The date and time this message came through the websocket service
     */
    protected Date mDateReceived;

    /**
     * The message object
     */
    protected GambitMessage mMessage;

    /**
     * The identifier for the subscription on which
     */
    protected String mTopicDescription;

    /**
     * Create a new instance using the date and time of reception and the message it self
     * @param dateReceived Date of reception in local timezone
     * @param message The message object
     */
    public GambitSavedMessage(Date dateReceived, GambitMessage message, String topicDescription) {
        mDateReceived = dateReceived;
        mMessage = message;
        mTopicDescription = topicDescription;
    }

    /**
     * Get the reception date
     * @return Date of reception
     */
    public Date getDateReceived() {
        return mDateReceived;
    }

    /**
     * Get the {@link GambitMessage} object
     * @return Get the message object
     */
    public GambitMessage getMessage() {
        return mMessage;
    }

    /**
     * Get the {@link String topic description}
     *
     * @return the topic description
     */
    public String getTopicDescription() {
        return mTopicDescription;
    }

    /**
     * Compare instances. Items are being sorted by date, descending
     * @param o Another instance to compare against
     * @return comparison result
     */
    @Override
    public int compareTo(GambitSavedMessage o) {
        return o.getDateReceived().compareTo(getDateReceived());
    }

    /**
     * Return JSON representation.
     *
     * @return JSON representation
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        df.setTimeZone(tz);
        String date_iso = df.format(getDateReceived());

        json.put("date_received", date_iso);
        json.put("message", getMessage().getRawMessage());
        json.put("topic_description", getTopicDescription());

        return json;
    }

    /**
     * Export to JSON String
     * @return JSON String
     */
    public String toString() {
        return toJson().toString();
    }

    /**
     * Recreate a message saved to config.json
     *
     * @param json The {@link JSONObject} obtained from the config file
     * @return A new instance identical to the saved one
     */
    public static GambitSavedMessage fromJson(JSONObject json) throws ParseException {

        GambitSavedMessage object = null;

        if (json.has("date_received") && json.has("message")) {
            String date_iso = json.getString("date_received");
            String messageJson = json.getString("message");
            String topicDescription = "unknown";

            try {
                topicDescription = json.getString("topic_description");
            } catch (JSONException e) {
                // Nothing found; ignore this
            }

            Date dateReceived = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(date_iso);

            GambitMessage message = new GambitMessage(messageJson);

            object = new GambitSavedMessage(dateReceived, message, topicDescription);
        }

        return object;
    }
}
