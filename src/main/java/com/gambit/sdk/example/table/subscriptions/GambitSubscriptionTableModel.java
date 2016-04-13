package com.gambit.sdk.example.table.subscriptions;

import com.gambit.sdk.example.table.messages.GambitSavedMessage;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class GambitSubscriptionTableModel extends AbstractTableModel {

    //Table columns
    protected static final String[] columns = {"Namespace", "Topic Description", "Actions"};

    //Table data
    protected LinkedList<GambitSavedSubscription> rows;

    @Override
    public int getRowCount() {
        if (rows != null) {
            return rows.size();
        }

        return 0;
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        GambitSavedSubscription data = rows.get(rowIndex);
        switch (columnIndex) {
            case 0: default:
                return data.getSubscribtion().getNamespace();
            case 1:
                return data.getSubscribtion().getTopicDescription();
            case 2:
                final JButton button = new JButton("Delete");
                return button;
        }
    }

    public void removeRow(int row) {
        rows.remove(row);
    }

    public GambitSavedSubscription getSavedSubscriction(int row){
        return rows.get(row);
    }

    public void setData(Collection<GambitSavedSubscription> tableData) {
        if (rows == null) {
            rows = new LinkedList<GambitSavedSubscription>();
        }
        else {
            rows.clear();
        }
        Iterator<GambitSavedSubscription> tableDataIterator = tableData.iterator();

        while (tableDataIterator.hasNext()) {
            GambitSavedSubscription data = tableDataIterator.next();

            rows.add(data);
        }

        fireTableDataChanged();

    }

    public LinkedList<GambitSavedSubscription> getData() {

        if (rows == null) {
            rows = new LinkedList<GambitSavedSubscription>();
        }

        return rows;
    }
}
