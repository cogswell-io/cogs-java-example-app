package com.gambit.sdk.example.table.event;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GambitDateUtils {

    /**
     * Date Formats
     */
    public static final String HUMAN_READABLE_FORMAT = "EEE MMM dd, yyyy HH:mm:ssZZ";
    public static final String ISO_FORMAT= "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final TimeZone UTC_TZ = TimeZone.getTimeZone("UTC");

    /**
     * Helper Methods
     */
    public static DateFormat makeDateFormat(String formatString) {
        return new SimpleDateFormat(formatString);
    }

    public static String formatDate(Object date, String formatString) {
        return makeDateFormat(formatString).format(date);
    }

    public static Date parseDate(Object date, String formatString) {
        try {
            return makeDateFormat(formatString)
                    .parse(date.toString());
        } catch (ParseException e) {
            Logger.getLogger(GambitAttributeTableCellEditorDateTime.class.getName())
                    .log(Level.WARNING, "Error parsing date from the date picker: " + e.getMessage(), e);
        }
        return null;
    }

}
