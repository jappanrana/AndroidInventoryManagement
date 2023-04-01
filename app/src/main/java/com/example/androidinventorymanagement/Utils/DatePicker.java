package com.example.androidinventorymanagement.Utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import com.example.androidinventorymanagement.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;

public class DatePicker extends AlertDialog implements DialogInterface.OnClickListener, android.widget.DatePicker.OnDateChangedListener
{
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private android.widget.DatePicker mDatePicker;

    public DatePicker(@NonNull Context context) {
        super(context);
        init();
    }

    public DatePicker(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    public DatePicker(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }


    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_calender_layout, null);
        mDatePicker = view.findViewById(R.id.datePickerElement);

        setView(view);
    }

    public void showDatePicker(DatePickerDialog.OnDateSetListener listener) {
        setButton(BUTTON_POSITIVE, getContext().getString(android.R.string.ok), this);
        setButton(BUTTON_NEGATIVE, getContext().getString(android.R.string.cancel), this);

        mDateSetListener = listener;
        Calendar current = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        Date date = new Date();
        int year = Integer.valueOf(formatter.format(date).substring(4,8));
        int monthOfYear = Integer.valueOf(formatter.format(date).substring(2,4));
        int dayOfMonth = Integer.valueOf(formatter.format(date).substring(0,2));
        int Startyear = 2021;
        int StartmonthOfYear = 1-1;
        int StartdayOfMonth = 1;

        mDatePicker.init(year, monthOfYear, dayOfMonth, this);

        current.set(Startyear,StartmonthOfYear,StartdayOfMonth);
//        c.set(Calendar.MAY, -1);//Year,Month -1,Day
//        c.add(Calendar.MONTH, -1);
        mDatePicker.setMinDate(current.getTimeInMillis());
        mDatePicker.setMaxDate(System.currentTimeMillis());
        show();

        getButton(Dialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#375DEB"));
        getButton(Dialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#375DEB"));
    }


    @Override
    public void onClick(@NonNull DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (mDateSetListener != null) {
                    // Clearing focus forces the dialog to commit any pending
                    // changes, e.g. typed text in a NumberPicker.
                    mDatePicker.clearFocus();
                    if (mDateSetListener != null) {
                        mDateSetListener.onDateSet(mDatePicker, mDatePicker.getYear(),
                                mDatePicker.getMonth()+1, mDatePicker.getDayOfMonth());
                    }

                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    @Override
    public void onDateChanged(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//        mDatePicker.init(year, monthOfYear, dayOfMonth, this);
    }
}
