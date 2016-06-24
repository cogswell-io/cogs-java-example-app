package com.gambit.sdk.example.table.event;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.Date;

public class GambitAttributeTableCellEditorDateTime extends AbstractCellEditor implements TableCellEditor {

    /**
     * The datetime picker component
     */
    protected JXDateTimePicker component;

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
        component.setFormats(GambitDateUtils.makeDateFormat(GambitDateUtils.ISO_FORMAT));

        Date date = null;

        if (value != null && !value.equals("")) {
            date = GambitDateUtils.parseDate(value, GambitDateUtils.HUMAN_READABLE_FORMAT);
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
            return GambitDateUtils.formatDate(date, GambitDateUtils.HUMAN_READABLE_FORMAT);
        }
        return "";
    }


}
