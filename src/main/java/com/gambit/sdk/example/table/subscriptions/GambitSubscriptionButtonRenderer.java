package com.gambit.sdk.example.table.subscriptions;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class GambitSubscriptionButtonRenderer extends JButton implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JButton button = (JButton)value;
        return button;
    }
}
