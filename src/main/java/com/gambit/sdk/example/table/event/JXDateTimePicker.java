package com.gambit.sdk.example.table.event;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.calendar.SingleDaySelectionModel;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class JXDateTimePicker extends JXDatePicker {

    /**
     * Visual control for entering time
     */
    protected JSpinner mSpinnerTime;

    /**
     * Panel holding additional controls
     */
    protected JPanel mPanelTime;

    /**
     * Determine how the time should be represented in the spinner
     */
    protected DateFormat mFormatTime;

    /**
     * Create a new component instance
     */
    public JXDateTimePicker() {
        super();

        getMonthView().setSelectionModel(new SingleDaySelectionModel());
    }

    /**
     * Update time and notify parent
     * @throws ParseException
     */
    public void commitEdit() throws ParseException {
        commitTime();
        super.commitEdit();
    }

    /**
     * Reset view
     */
    public void cancelEdit() {
        super.cancelEdit();
        setTimeSpinners();
    }

    /**
     * Return the panel that is used at the bottom of the popup. Insert time panel as default.
     * @return bottom panel
     */
    @Override
    public JPanel getLinkPanel() {
        super.getLinkPanel();

        if(mPanelTime == null) {

            mPanelTime = new JPanel();
            mPanelTime.setLayout(new FlowLayout());

            SpinnerDateModel dateModel = new SpinnerDateModel();
            mSpinnerTime = new JSpinner(dateModel);

            if (mFormatTime == null)
            {
                //set default time format
                mFormatTime = DateFormat.getTimeInstance(DateFormat.MEDIUM);
            }

            updateTimeFormat();

            mPanelTime.add(new JLabel("Time: "));
            mPanelTime.add(mSpinnerTime);
            mPanelTime.setBackground(Color.WHITE);
        }

        setTimeSpinners();

        return mPanelTime;
    }

    /**
     * Apply time format to the view component
     */
    protected void updateTimeFormat() {

        if(mSpinnerTime == null) return;

        JFormattedTextField tf = ((JSpinner.DefaultEditor) mSpinnerTime.getEditor()).getTextField();
        DefaultFormatterFactory factory = (DefaultFormatterFactory) tf.getFormatterFactory();
        DateFormatter formatter = (DateFormatter) factory.getDefaultFormatter();
        formatter.setFormat(mFormatTime);
    }

    /**
     * Save the selected time to the {@link Date} object
     */
    protected void commitTime() {

        Date date = getDate();

        if (date != null) {

            Date time = (Date) mSpinnerTime.getValue();

            GregorianCalendar timeCalendar = new GregorianCalendar();
            timeCalendar.setTime( time );

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Date newDate = calendar.getTime();
            setDate(newDate);
        }

    }

    /**
     * (Re)set time view values
     */
    protected void setTimeSpinners() {
        Date date = getDate();
        if (date != null) {
            mSpinnerTime.setValue(date);
        }
    }

    /**
     * Get the time format
     * @return time format
     */
    public DateFormat getTimeFormat() {
        return mFormatTime;
    }

    /**
     * Set the time format
     * @param timeFormat time format
     */
    public void setTimeFormat(DateFormat timeFormat) {
        this.mFormatTime = timeFormat;
        updateTimeFormat();
    }

}