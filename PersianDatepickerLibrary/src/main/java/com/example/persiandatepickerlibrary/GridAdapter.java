package com.example.persiandatepickerlibrary;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.persiandatepickerlibrary.Listeners.SetMarkOnDate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * this class represents an adaptor to add items (days) number and sets backgrounds and ...
 * this class handle shamsi too and sets dates numbers in shamsi
 * if days are not in chosen month they will be hidden
 *
 * @author : zahra faehi
 * @version : 0.0
 */

public class GridAdapter extends ArrayAdapter {
    private final List<Date> dates;
    private final Calendar currentDate;
    private final LayoutInflater inflater;
    private SetMarkOnDate setMarkOnDate;
    private boolean isShamsi;


    public GridAdapter(@NonNull Context context, List<Date> dates, Calendar currentDate, boolean isShamsi) {
        super(context, R.layout.my_single_cell_layout);
        this.dates = dates;
        this.currentDate = currentDate;
        this.isShamsi = isShamsi;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(dates.get(position));
        int dayNum = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH) + 1;
        int displayYear = dateCalendar.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);

        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.my_single_cell_layout, parent, false);
        }
        if (isShamsi) {
            PersianCalendar persianCalendar1 = new PersianCalendar();
            PersianCalendar persianCalendar2 = new PersianCalendar();
            persianCalendar1.setTimeInMillis(dateCalendar.getTimeInMillis());
            persianCalendar2.setTimeInMillis(currentDate.getTimeInMillis());
            if (persianCalendar1.get(Calendar.MONTH) != persianCalendar2.get(Calendar.MONTH)) {
                view.setVisibility(View.GONE);
            }
        } else if (displayMonth != currentMonth || displayYear != currentYear) {
            view.setVisibility(View.GONE);
        }

        showToday(view, displayMonth, displayYear, dayNum);

        TextView dayNumber = view.findViewById(R.id.day);
        dayNumber.setTextColor(Color.parseColor(CustomCalendarView.txtColor));
        if (isShamsi) {
            PersianCalendar persianCalendar = new PersianCalendar();
            persianCalendar.setTimeInMillis(dateCalendar.getTimeInMillis());
            dayNumber.setText(String.valueOf(persianCalendar.get(Calendar.DAY_OF_MONTH)));
        } else {
            dayNumber.setText(String.valueOf(dayNum));
        }

        ImageView imageView = view.findViewById(R.id.dailyEvent);
        setMarkOnDate.setIcon(imageView, dateCalendar.getTimeInMillis());
        return view;
    }

    /**
     * this method checks if we are in current month page will set background for current date
     *
     * @param view         is my item
     * @param displayMonth is the month which user chose and can see
     * @param displayYear  is the month which user chose and can see
     * @param dayNum       is one of the days which user can see and if it is today this method will set background for it
     */
    private void showToday(View view, int displayMonth, int displayYear, int dayNum) {
        Calendar todayCalendar = Calendar.getInstance();
        if (todayCalendar.get(Calendar.YEAR) == displayYear
                && todayCalendar.get(Calendar.MONTH) + 1 == displayMonth
                && todayCalendar.get(Calendar.DAY_OF_MONTH) == dayNum) {// show current day(today)
            animations(view);
            view.setBackground(CustomCalendarView.itemBackground);
        }
    }

    private void animations(View view) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        alpha.setDuration(1000).start();
        rotation.setDuration(1000).start();
    }

    /**
     * @param setMarkOnDate is an interface which handles setting marks on dates
     */
    public void setSetMarkOnDate(SetMarkOnDate setMarkOnDate) {
        this.setMarkOnDate = setMarkOnDate;
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }

}
