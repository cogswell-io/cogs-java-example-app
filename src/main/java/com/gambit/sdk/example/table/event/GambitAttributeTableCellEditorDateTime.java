package com.gambit.sdk.example.table.event;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GambitAttributeTableCellEditorDateTime extends AbstractCellEditor implements TableCellEditor {

    /**
     * The datetime picker component
     */
    protected JXDateTimePicker component;

    /**
     * Date Formats
     */
    private String humanReadableFormat = "EEE MMM dd, YYYY HH:mm:ssZZ";
    private String isoFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
    private TimeZone tz = TimeZone.getTimeZone("UTC");

    /**
     * Spawn a date time picker on demand
     * @param table the table
     * @param value the value
     * @param isSelected flag that indicates selection state
     * @param rowIndex the row number
     * @param vColIndex the column number
     * @return A swing component that will be placed in the requested table column
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int vColIndex) {
        component = new JXDateTimePicker();
        component.setFormats(makeDateFormat(isoFormat));

        Date date = null;

        if (value != null && !value.equals("")) {
            date = parseDate(value, humanReadableFormat);
        } else {
            date = new Date();
        }

        if (date != null) {
            component.setDate(date);
        }

        return component;
    }


    /**
     * Obtain the result from the component and display it as text after the editing is finally over
     * @return date time string in ISO-8601 format
     */
    public Object getCellEditorValue() {
        Date date = component.getDate();

        if (date != null) {
            return formatDate(date, humanReadableFormat);
        }
        return "";
    }

    /**
     * Helper Methods
     */
    private DateFormat makeDateFormat(String formatString) {
        return new SimpleDateFormat(formatString);
    }

    private String formatDate(Object date, String formatString) {
        return makeDateFormat(formatString).format(date);
    }

    private Date parseDate(Object date, String formatString) {
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
