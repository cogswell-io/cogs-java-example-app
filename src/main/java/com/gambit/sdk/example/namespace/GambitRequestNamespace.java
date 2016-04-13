package com.gambit.sdk.example.namespace;

import com.gambit.sdk.GambitRequest;
import com.gambit.sdk.GambitResponse;
import com.gambit.sdk.example.exceptions.CogsException;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GambitRequestNamespace extends GambitRequest {

    public static class Builder {

        /**
         * Obtained through Gambit UI
         */
        protected final String mAccessKey;

        /**
         * Obtained through Gambit UI
         */
        protected final String mSecretKey;

        /**
         * The name of the namespace to get schema for.
         */
        protected final String mNamespace;

        /**
         * Create request builder with keys obtained through Gambit UI and
         * Gambit Tools SDK
         *
         * @param access_key The access key obtained from Gambit UI
         * @param secret_key The secret key obtained from Gambit UI
         * @param namespace The namespace name to obtain attributes for
         */
        public Builder(String access_key, String secret_key, String namespace) {
            this.mAccessKey = access_key;
            this.mSecretKey = secret_key;
            this.mNamespace = namespace;
        }

        /**
         * The namespace for with which this event is associated. The event's
         * attributes must either be defined for the specified namespace, or
         * they must be core attributes defined by the customer owning this
         * namespace.
         *
         * @return The namespace name
         */
        public String getNamespace() {
            return mNamespace;
        }

        /**
         * Obtained through Gambit UI (public key)
         *
         * @return The access key obtained from Gambit UI
         */
        public String getAccessKey() {
            return mAccessKey;
        }

        /**
         * Obtained through Gambit UI (private key)
         *
         * @return The secret key obtained from Gambit UI
         */
        public String getSecretKey() {
            return mSecretKey;
        }

        /**
         * Build request object
         *
         * @return The namespace name to obtain attributes for
         * @throws Exception
         */
        public GambitRequestNamespace build() throws Exception {
            return new GambitRequestNamespace(this);
        }
    }

    /**
     * The endpoint URL format
     */
    protected static final String endpoint = "namespace/%s/schema"; //must use String.format() on this

    /**
     * Generated request body
     */
    protected String mBody;

    /**
     * Obtained through Gambit UI
     */
    protected final String mAccessKey;

    /**
     * Obtained through Gambit UI
     */
    protected final String mSecretKey;

    /**
     * The namespace for with which to obtain schema for.
     */
    protected String mNamespace;

    /**
     * Construct the request object using it's own {@link Builder} instance.
     * @param builder The {@link Builder} object
     */
    protected GambitRequestNamespace(Builder builder) {
        mAccessKey = builder.getAccessKey();
        mSecretKey = builder.getSecretKey();
        mNamespace = builder.getNamespace();
    }

    /**
     * Define the HTTP method to use
     * @return GET
     */
    @Override
    protected String getMethod() {
        return "GET";
    }

    /**
     * Build the request URL to execute the API call upon.
     * @return Full request {@link URL}
     */
    @Override
    protected URL getUrl() throws IOException {
        StringBuilder builder = new StringBuilder();

        builder.append(getBaseUrl());
        builder.append(String.format(endpoint, mNamespace));

        URL url;

        try {
            url = new URL(builder.toString());
        } catch (MalformedURLException ex) {
            Logger.getLogger(GambitRequestNamespace.class.getName()).log(Level.SEVERE, "Invalid Cogs reauest URL", ex);

            throw new CogsException("Invalid Cogs request URL", ex);
        }

        return url;
    }

    /**
     * Build a JSON according to specification. This is the actual request body.
     * @return JSON string representation of all needed request parameters
     */
    @Override
    protected String getBody() {

        if (mBody == null) {
            mBody = "";
        }

        return mBody;
    }

    /**
     * Inject the HMAC-SHA256 hash as a header to the request
     * @param connection The {@link HttpURLConnection} object that is going to execute the API call.
     */
    @Override
    protected void setRequestParams(HttpURLConnection connection) {
        try {
            JSONObject json = new JSONObject();

            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
            df.setTimeZone(tz);
            String nowAsISO = df.format(new Date());

            json.put("access_key", mAccessKey);
            json.put("timestamp", nowAsISO);

            String jsonBody = json.toString();

            String base64 = DatatypeConverter.printBase64Binary(jsonBody.getBytes());

            connection.setRequestProperty("JSON-Base64", base64);

            connection.setRequestProperty("Payload-HMAC", getHmac(jsonBody, mSecretKey));
        } catch (Throwable ex) {
            Logger.getLogger(GambitRequest.class.getName()).log(Level.SEVERE, "Error setting up request parameters", ex);

            throw new CogsException("Error setting up request parameters", ex);
        }
    }

    /**
     * Build {@link GambitResponseNamespace} instance, containing list of {@link com.gambit.sdk.example.table.event.GambitAttribute}
     * @param response The RAW HTTP response body as text
     * @param code The RAW HTTP response code as an integer
     * @return An instance of {@link GambitResponseNamespace}
     */
    @Override
    protected GambitResponse getResponse(String response, int code) {
        return new GambitResponseNamespace(response, code);
    }

    /**
     * Obtained through Gambit UI (public key)
     *
     * @return The access key obtained from Gambit UI
     */
    public String getAccessKey() {
        return mAccessKey;
    }

    /**
     * Obtained through Gambit UI (private key)
     *
     * @return The secret key obtained from Gambit UI
     */
    public String getSecretKey() {
        return mSecretKey;
    }

    /**
     * The namespace for with which to obtain schema for.
     *
     * @return The name of the namespace
     */
    public String getNamespace() {
        return mNamespace;
    }
}
