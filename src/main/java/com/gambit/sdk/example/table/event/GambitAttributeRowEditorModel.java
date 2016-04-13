package com.gambit.sdk.example.table.event;

import javax.swing.table.TableCellEditor;
import java.util.HashMap;

public class GambitAttributeRowEditorModel {

    /**
     * Store TableCellEditor instances for each row, as they may differ from one another
     */
    protected HashMap<Integer, TableCellEditor> data;

    /**
     * Initialize the model
     */
    public GambitAttributeRowEditorModel()
    {
        data = new HashMap();
    }

    /**
     * Attach an editor to a specific row number
     * @param row row number
     * @param e editor instance
     */
    public void addEditorForRow(int row, TableCellEditor e )
    {
        data.put(new Integer(row), e);
    }

    /**
     * Detach editor from a specific row number
     * @param row row number
     */
    public void removeEditorForRow(int row)
    {
        data.remove(new Integer(row));
    }

    /**
     * Retrieve TableCellEditor for a row number, if available
     * @param row row number
     * @return TableCellEditor instance, or null to fallback to default
     */
    public TableCellEditor getEditor(int row)
    {
        if (data.containsKey(new Integer(row))) {
            return data.get(new Integer(row));
        }

        return null;
    }

    /**
     * Clear column values
     */
    public void clear() {
        if (data != null) {
            data.clear();
        }
    }
}
