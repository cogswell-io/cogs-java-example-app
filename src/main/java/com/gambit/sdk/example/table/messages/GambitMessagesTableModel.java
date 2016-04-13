package com.gambit.sdk.example.table.messages;

import com.gambit.sdk.example.table.event.GambitAttribute;
import com.gambit.sdk.example.table.event.GambitAttributeRowEditorModel;
import com.gambit.sdk.example.table.event.GambitAttributeTableCellEditorDateTime;
import com.gambit.sdk.example.table.subscriptions.GambitSavedSubscription;
import com.gambit.sdk.example.table.subscriptions.GambitSubscription;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.util.*;

public class GambitMessagesTableModel extends AbstractTableModel {

    /**
     * Table columns
     */
    protected static final String[] columns = {"Date Received", "Event Name", "Topic Description", "Campaign Name"};

    /**
     * Table data
     */
    protected LinkedList<GambitSavedMessage> rows;

    private Map<String, GambitSavedSubscription> topicSubscriptionData;

    private GambitMessagesTableModel(){

    }

    public GambitMessagesTableModel(Map<String, GambitSavedSubscription> topicData) {
        this.topicSubscriptionData = topicData;
    }

    /**
     * Could the number of rows
     * @return Number of rows
     */
    @Override
    public int getRowCount() {
        if (rows != null) {
            return rows.size();
        }

        return 0;
    }

    /**
     * Could the number of columns
     * @return Number of columns
     */
    @Override
    public int getColumnCount() {
        return columns.length;
    }

    /**
     * Get column name
     * @param columnIndex Column number
     * @return Column name as a string
     */
    @Override
    public String getColumnName(int columnIndex) {
        return columns[columnIndex];
    }

    /**
     * Determine how a field in a column should be displayed
     * @param c Column number
     * @return The object class of the element sitting in the specified column in the first row
     */
    @Override
    public Class getColumnClass(int c) {

        if (getValueAt(0, c) != null) {
            return getValueAt(0, c).getClass();
        }

        return String.class;
    }

    /**
     * Will always return false. This table is read only.
     * @param rowIndex row number
     * @param columnIndex column number
     * @return false
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * Retrieve the value of a cell
     * @param rowIndex Row number
     * @param columnIndex Column number
     * @return Value of a cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        GambitSavedMessage message = null;

        if (rows != null && rows.size() > rowIndex) {
            message = rows.get(rowIndex);

            switch (columnIndex) {
                case 0:default:
                    return message.getDateReceived().toString();
                case 1:
                    return message.getMessage().getEventName();
                case 2:
                    return message.getTopicDescription();
                case 3:
                    return message.getMessage().getCampaignName();
            }
        }

        return null;
    }

    /**
     * Set the entire table data at once. Sorting will be applied in here, so no need to bother.
     * @param tableData Table data
     */
    public void setData(ArrayList<GambitSavedMessage> tableData) {
        if (rows == null) {
            rows = new LinkedList<GambitSavedMessage>();
        }
        else {
            rows.clear();
        }

        Collections.sort(tableData);

        Iterator<GambitSavedMessage> tableDataIterator = tableData.iterator();

        while (tableDataIterator.hasNext()) {
            GambitSavedMessage message = tableDataIterator.next();

            rows.add(message);
        }

        fireTableDataChanged();
    }

    /**
     * Retrieve all table data at once
     * @return Table data
     */
    public LinkedList<GambitSavedMessage> getData() {

        if (rows == null) {
            rows = new LinkedList<GambitSavedMessage>();
        }

        return rows;
    }

    public void clearData(){
        if(rows != null){
            rows.clear();
            fireTableDataChanged();
        }
    }

}
