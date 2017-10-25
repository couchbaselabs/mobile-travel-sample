package com.couchbase.travelsample.util;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements android.app.DatePickerDialog.OnDateSetListener {

    public interface DatePickerListener {

        void onDateSelected(String date, String tag);

    }

    private DatePickerListener mFlightSearchView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day_of_month = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new android.app.DatePickerDialog(getActivity(), this, year, month, day_of_month);
    }

    public void setListener(DatePickerListener view) {
        mFlightSearchView = view;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Bundle bundle = getArguments();
        mFlightSearchView.onDateSelected(String.format("%d/%d/%d", i2, i1, i), bundle.getString("tag"));
    }

}