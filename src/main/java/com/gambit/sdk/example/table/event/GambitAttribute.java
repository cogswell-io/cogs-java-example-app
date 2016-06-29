package com.gambit.sdk.example.table.event;

import org.json.JSONObject;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GambitAttribute implements Comparable<GambitAttribute> {

    public static final String TYPE_DATE = "Date";
    public static final String TYPE_BOOL = "Boolean";
    public static final String TYPE_INT = "Integer";
    public static final String TYPE_NUM = "Number";

    /**
     * Attribute name
     */
    protected String mName = "";

    /**
     * Attribute data type
     */
    protected String mDataType = "text";

    /**
     * Is part of the core attributes
     */
    protected boolean mCore = false;

    /**
     * Is attribute primary key for the namespace
     */
    protected boolean mCiid = false;

    /**
     * User defined value
     */
    protected String mValue;

    public GambitAttribute(){

    }

    /**
     * Create a new attribute object
     * @param name Attribute name
     * @param dataType Attribute data type
     * @param core Flag whether this attribute is coming from this namespace, or is inherited
     * @param ciid Flag whether this attribute is a CIID (primary key)
     */
    public GambitAttribute(String name, String dataType, boolean core, boolean ciid) {
        this.mName = name;
        this.mDataType = dataType;
        this.mCore = core;
        this.mCiid = ciid;
    }

    /**
     * Get Attribute name
     * @return Attribute name
     */
    public String getName() {
        return mName;
    }

    /**
     * Set Attribute name
     * @param name Attribute name
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * Get Attribute data type
     * @return Data type; Check the public static stuff in {@link GambitAttribute}
     */
    public String getDataType() {
        return mDataType;
    }

    /**
     * Set Attribute data type
     * @param mDataType Data type; Check the public static stuff in {@link GambitAttribute}
     */
    public void setDataType(String mDataType) {
        this.mDataType = mDataType;
    }

    /**
     * Is part of the core attributes
     * @return Flag whether this attribute is coming from this namespace, or is inherited
     */
    public boolean isCore() {
        return mCore;
    }

    /**
     * Is part of the core attributes
     * @param mCore Flag whether this attribute is coming from this namespace, or is inherited
     */
    public void setCore(boolean mCore) {
        this.mCore = mCore;
    }

    /**
     * Is attribute primary key for the namespace
     * @return Flag whether this attribute is a CIID (primary key)
     */
    public boolean isCiid() {
        return mCiid;
    }

    /**
     * Is attribute primary key for the namespace
     * @param mCiid Flag whether this attribute is a CIID (primary key)
     */
    public void setCiid(boolean mCiid) {
        this.mCiid = mCiid;
    }

    /**
     * Get User defined value as string
     * @return User defined value as string
     */
    public String getValue() {
        return mValue;
    }

    /**
     * Set User defined value as string
     * @param value User defined value as string
     */
    public void setValue(String value) {
        this.mValue = value;
    }


    /**
     * Return JSON representation.
     *
     * @return JSON Object containing all the information in this instance
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        json.put("name", getName());
        json.put("data_type", getDataType());
        json.put("value", getValue());

        json.put("ciid", isCiid());
        json.put("core", isCore());

        return json;
    }

    /**
     * Recreate an attribute saved to config.json
     *
     * @param json JSON Object generated previously using toJson()
     * @return GambitAttribute instance
     */
    public static GambitAttribute fromJson(JSONObject json) {

        GambitAttribute attr = new GambitAttribute();
        if(json.has("name")) {
            String name = json.getString("name");
            attr.setName(name);
        }
        if(json.has("data_type")){
            String data_type = json.getString("data_type");
            attr.setDataType(data_type);
        }
        if(json.has("value")) {
            String value = json.getString("value");
            attr.setValue(value);
        }
        if(json.has("ciid")) {
            boolean ciid = json.getBoolean("ciid");
            attr.setCiid(ciid);
        }
        if(json.has("core")) {
            boolean core = json.getBoolean("core");
            attr.setCore(core);
        }

        return attr;
    }

    /**
     * Compare instances. Primary keys (CIID=true) surface on top with alphabetical order by name
     * @param o Another instance to compare against
     * @return comparison result
     */
    @Override
    public int compareTo(GambitAttribute o) {

        if (o.isCiid()) {
            return 1;
        }
        else {
            return getName().compareTo(o.getName());
        }
    }

    /**
     * Returns a type corrected value for JSON export.
     * @return Type corrected value, either Boolean, Integer, Double, String or null
     */
    public Object getTypeCorrectedValue() {
        if (getValue() != null && !getValue().isEmpty()) {
            try {
                if (getDataType().equals(TYPE_BOOL)) {
                    return new Boolean(getValue());
                } else if (getDataType().equals(TYPE_INT)) {
                    return Integer.parseInt(getValue());
                } else if (getDataType().equals(TYPE_NUM)) {
                    return Double.parseDouble(getValue());
                } else if (getDataType().equals(TYPE_DATE)) {
                    Date date = GambitDateUtils.parseDate(getValue(), GambitDateUtils.HUMAN_READABLE_FORMAT);
                    return GambitDateUtils.formatDate(date, GambitDateUtils.ISO_FORMAT);
                } else {
                    return getValue();
                }
            } catch (Throwable e) {
                Logger.getLogger(GambitAttribute.class.getName()).log(Level.WARNING,
                        "Error performing type corrections for the JSON payload: " + e.getMessage(), e);
                return null;
            }
        } else {
            return null;
        }

    }

}
