package com.gambit.sdk.example.table.event;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class GambitAttributeTableModel extends AbstractTableModel {

    /**
     * Table columns
     */
    protected static final String[] columns = {"Attribute Name", "PK (Required)", "Type", "Value"};

    /**
     * Table data
     */
    protected LinkedList<GambitAttribute> rows;

    /**
     * Custom Helper Model for row specific value editors
     */
    protected GambitAttributeRowEditorModel rowEditorModel;

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
     * Basically, we want to be able to edit only the last column
     * @param rowIndex Row number
     * @param columnIndex Column number
     * @return Boolean flag
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 3) {
            return true;
        }

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

        GambitAttribute attr = null;

        if (rows != null && rows.size() > rowIndex) {
            attr = rows.get(rowIndex);

            switch (columnIndex) {
                case 0:default:
                    return attr.getName();
                case 1:
                    return attr.isCiid();
                case 2:
                    return attr.getDataType();
                case 3:
                    return attr.getValue();
            }
        }

        return null;
    }

    /**
     * Set the value of a cell
     * @param aValue Value object, probably string
     * @param rowIndex Row number
     * @param columnIndex Column number
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 3) {
            GambitAttribute attr = null;

            if (rows != null && rows.size() > rowIndex) {
                attr = rows.get(rowIndex);

                attr.setValue(aValue.toString());

                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
    }

    /**
     * Set the entire table data at once. Sorting will be applied in here, so no need to bother.
     * @param tableData Table data
     */
    public void setData(ArrayList<GambitAttribute> tableData) {
        if (rows == null) {
            rows = new LinkedList<GambitAttribute>();
        }
        else {
            rows.clear();
            getRowEditorModel().clear();
        }

        Collections.sort(tableData);

        ///

        Iterator<GambitAttribute> tableDataIterator = tableData.iterator();

        while (tableDataIterator.hasNext()) {
            GambitAttribute attr = tableDataIterator.next();

            //

            rows.add(attr);

            //

            if (attr.getDataType().equals(GambitAttribute.TYPE_DATE)) {
                if (getRowEditorModel() != null) {

                    GambitAttributeTableCellEditorDateTime editor = new GambitAttributeTableCellEditorDateTime();

                    getRowEditorModel().addEditorForRow(rows.size()-1, editor);
                }
            }
            else if (attr.getDataType().equals(GambitAttribute.TYPE_BOOL)) {
                if (getRowEditorModel() != null) {
                    getRowEditorModel().addEditorForRow(rows.size()-1, new DefaultCellEditor(new JComboBox(new String[] {"", "true", "false"})));
                }
            }
        }

        fireTableDataChanged();
    }

    /**
     * Retrieve all table data at once
     * @return Table data
     */
    public LinkedList<GambitAttribute> getData() {
        return rows;
    }

    public void clearData(){
        rows.clear();
        fireTableDataChanged();
    }

    /**
     * Not exactly necessary.. as there's only one object in the entire universe to fit here.
     * @param rowEditorModel the row specific editor model
     */
    public void setRowEditorModel(GambitAttributeRowEditorModel rowEditorModel) {
        this.rowEditorModel = rowEditorModel;
    }

    /**
     * Get the custom helper model for row specific cell editors
     * @return the row specific editor model
     */
    public GambitAttributeRowEditorModel getRowEditorModel()
    {
        return rowEditorModel;
    }


}
