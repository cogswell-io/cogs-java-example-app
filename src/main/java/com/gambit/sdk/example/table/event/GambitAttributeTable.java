package com.gambit.sdk.example.table.event;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import java.util.EventObject;

public class GambitAttributeTable extends JTable {

    /**
     * House a custom TableCellEditor for each row using this helper model
     */
    protected GambitAttributeRowEditorModel mRowEditorModel;

    /**
     * Not exactly necessary.. as there's only one object in the entire universe to fit here.
     * @param rowEditorModel the row specific editor model
     */
    public void setRowEditorModel(GambitAttributeRowEditorModel rowEditorModel) {
        this.mRowEditorModel = rowEditorModel;
    }

    /**
     * Get the custom helper model that allows us to have different TableCellEditors for each row
     * @return The helper model
     */
    public GambitAttributeRowEditorModel getRowEditorModel()
    {
        return mRowEditorModel;
    }

    /**
     * Retrieve a TableCellEditor using the custom helper model this class is all about
     * @param row row number
     * @param col column number (always the values column)
     * @return TableCellEditor provided by the helper, or fallback to default
     */
    public TableCellEditor getCellEditor(int row, int col)
    {
        TableCellEditor editor = null;

        if (getRowEditorModel() != null) {
            editor = getRowEditorModel().getEditor(row);

            if (editor != null) {
                return editor;
            }
        }

        return super.getCellEditor(row,col);
    }

    @Override
    public boolean editCellAt(int row, int column, EventObject e) {
        boolean result = super.editCellAt(row, column, e);

        GambitAttribute attr = ((GambitAttributeTableModel)getModel()).getData().get(row);

        //disable focus controlled edit while editing date.. because it is not working otherwise...
        if (attr.getDataType().equals(GambitAttribute.TYPE_DATE)) {
            putClientProperty("terminateEditOnFocusLost", Boolean.FALSE);
        }

        return result;
    }

    @Override
    public void editingStopped(ChangeEvent e) {
        super.editingStopped(e);

        //restore focus controlled edit
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    }
}
