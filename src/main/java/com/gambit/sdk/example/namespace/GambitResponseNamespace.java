package com.gambit.sdk.example.namespace;

import com.gambit.sdk.GambitResponse;
import com.gambit.sdk.example.table.event.GambitAttribute;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class GambitResponseNamespace extends GambitResponse {

    /**
     * Gambit API Namespace Attributes
     */
    protected ArrayList<GambitAttribute> mAttributes;

    /**
     * Construct the response object using the raw response body and response code
     * @param response The raw HTTP response body as text
     * @param code The raw HTTP response code as an integer
     */
    public GambitResponseNamespace(String response, int code) {
        super(response, code);

        mAttributes = new ArrayList<GambitAttribute>();

        if (isSuccess()) {
            if (mJson.has("attributes")) {

                JSONArray mAttributesData = mJson.getJSONArray("attributes");

                Iterator mAttributesIterator = mAttributesData.iterator();

                while (mAttributesIterator.hasNext()) {
                    JSONObject mAttributeData = (JSONObject) mAttributesIterator.next();

                    if (mAttributeData.has("name") && mAttributeData.has("data_type") && mAttributeData.has("ciid") && mAttributeData.has("core")) {

                        GambitAttribute attr = null;

                        attr = new GambitAttribute(mAttributeData.getString("name"), mAttributeData.getString("data_type"), mAttributeData.getBoolean("core"), mAttributeData.getBoolean("ciid"));

                        mAttributes.add(attr);
                    }
                }
            }
            else {
                //not good at all
                mIsSuccess = false;
                mErrorCode = "UNKNOWN";
                mErrorDetails = "Unknown response: "+response;
            }
        }
    }
    
    /**
     * Get the Namespace Attributes
     * @return list of {@link GambitAttribute} instances
     */
    public ArrayList<GambitAttribute> getAttributes() {
        return mAttributes;
    }
}
