package com.gambit.sdk.example.table.subscriptions;

import com.gambit.sdk.example.table.event.GambitAttribute;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GambitSubscription implements Comparable<GambitSubscription> {

    protected String namespace;
    protected String topicDescription;
    protected String clientSalt;
    protected String clientSecret;

    protected LinkedList<GambitAttribute> attributes = new LinkedList<>();

    protected boolean isValid = true;

    public GambitSubscription(String rawData) {

        JSONObject payload = null;

        try {
            payload = new JSONObject(rawData);
        }
        catch (JSONException e) {
            Logger.getLogger(GambitSubscription.class.getName())
                    .log(Level.WARNING, "Error parsing subscription JSON payload: " + e.getMessage(), e);
            return;
        }

        parsePayload(payload);
    }

    public GambitSubscription() {
    }

    public GambitSubscription(String namespace, String description) {
        this.namespace = namespace;
        this.topicDescription = description;
    }

    public GambitSubscription(
            String namespace,
            String topicDescription,
            String clientSalt,
            String clientSecret,
            LinkedList<GambitAttribute> attributes) {
        this.namespace = namespace;
        this.topicDescription = topicDescription;
        this.clientSalt = clientSalt;
        this.clientSecret = clientSecret;
        this.attributes = attributes;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        json.put("namespace", getNamespace());
        json.put("topic_description", getTopicDescription());
        json.put("client_salt", getClientSalt());
        json.put("client_secret", getClientSecret());

        JSONArray attributeArray = new JSONArray();
        for(GambitAttribute item : getAttributes()){
            attributeArray.put(item.toJson());
        }
        json.put("attributes", attributeArray);

        return json;
    }

    public static GambitSubscription fromJson(JSONObject json) {

        String namespace = json.getString("namespace");
        String topicDescription = json.getString("topic_description");
        String clientSalt = json.getString("client_salt");
        String clientSecret = json.getString("client_secret");
        JSONArray attributesArrayJson = json.getJSONArray("attributes");
        LinkedList<GambitAttribute> attributes = getAttributes(attributesArrayJson);

        return new GambitSubscription(namespace, topicDescription, clientSalt, clientSecret, attributes);

    }

    private void parsePayload(JSONObject payload) {
        if(payload.has("namespace")){
            namespace = payload.getString("namespace");
        }else {
            isValid = false;
        }
        if(payload.has("topic_description")){
            topicDescription = payload.getString("topic_description");
        }else {
            isValid = false;
        }
        if(payload.has("client_salt")){
            clientSalt = payload.getString("client_salt");
        }else {
            isValid = false;
        }
        if(payload.has("client_secret")){
            clientSecret = payload.getString("client_secret");
        }else {
            isValid = false;
        }
        if(payload.has("attributes")){
            JSONArray attrArray = payload.getJSONArray("attributes");
            attributes = getAttributes(attrArray);
        }
    }

    private static LinkedList<GambitAttribute> getAttributes(JSONArray attributesArrayJson){
        LinkedList<GambitAttribute> attributeList = new LinkedList<>();
        for (int i = 0; i < attributesArrayJson.length(); i++) {
            JSONObject item = attributesArrayJson.getJSONObject(i);
            GambitAttribute attr = GambitAttribute.fromJson(item);
            attributeList.add(attr);
        }
        return attributeList;
    }

    @Override
    public int compareTo(GambitSubscription o) {
        return getTopicDescription().compareToIgnoreCase(o.getTopicDescription());
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getTopicDescription() {
        return topicDescription;
    }

    public void setTopicDescription(String topicDescription) {
        this.topicDescription = topicDescription;
    }

    public String getClientSalt() {
        return clientSalt;
    }

    public void setClientSalt(String clientSalt) {
        this.clientSalt = clientSalt;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }


    public LinkedList<GambitAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(LinkedList<GambitAttribute> attributes) {
        this.attributes = attributes;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

}
