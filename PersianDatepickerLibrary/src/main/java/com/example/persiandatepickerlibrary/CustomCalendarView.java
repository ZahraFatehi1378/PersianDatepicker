package com.example.persiandatepickerlibrary;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.persiandatepickerlibrary.Listeners.SetOnDateItemClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * this class represents a view for date picker
 * there are some methods to :
 * get chosen date as milli sec
 * choose persian or domini
 * change components colors
 * set mark on some dates
 *
 * @author : zahra fatehi
 * @version 0.0
 */
public class CustomCalendarView extends LinearLayout {

    private ImageView next, previous;
    private GridView gridView;
    private TextView month, sat, sun, mon, tue, wed, thu, fri;
    private Context context;
    private final Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    private final int Max_CalendarDays = 42;
    private GridAdapter myGridAdapter;
    public static String txtColor = "#222f3e";//default color but you can change it
    private SetOnDateItemClickListener setOnDateItemClickListener;
    private ArrayList<Long> markedDays;
    private final List<Date> dates = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    public static Drawable itemBackground;
    private boolean shamsi = false;

    /**
     * create a custom calendar and initialize it
     * @param context is the context of activity which we add this library to
     */
    public CustomCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializeLayout();
        addListeners();
    }

    /**
     * initialize components
     */
    private void initializeLayout() {
        View view = LayoutInflater.from(context).inflate(R.layout.datepicket_layout, this, true);
        next = view.findViewById(R.id.next);
        previous = view.findViewById(R.id.before);
        gridView = view.findViewById(R.id.gridView);
        month = view.findViewById(R.id.month);
        sat = view.findViewById(R.id.sat);
        sun = view.findViewById(R.id.sun);
        mon = view.findViewById(R.id.mon);
        tue = view.findViewById(R.id.tue);
        wed = view.findViewById(R.id.wed);
        thu = view.findViewById(R.id.thu);
        fri = view.findViewById(R.id.fri);
        SetUpCalendar();
    }

    /**
     * this method represents the listeners
     * increase month when you click on next
     * decrease month when you click on previous
     * and for gridView it prepare the chosen date year , month and day in milli sec and changes the item background
     */
    private void addListeners() {
        previous.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            SetUpCalendar();
        });

        next.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            SetUpCalendar();
        });

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(dates.get(position));
            dateCalendar.set(Calendar.HOUR, 0);
            dateCalendar.set(Calendar.MINUTE, 0);
            dateCalendar.set(Calendar.MILLISECOND, 0);
            dateCalendar.set(Calendar.SECOND, 0);
            setOnDateItemClickListener.onDateItemClicked(dateCalendar);
            animations(view);
            view.setBackground(itemBackground);
            for (int i = 0; i < parent.getChildCount(); i++) {
                if (i != position)
                    parent.getChildAt(i).setBackgroundResource(0);
            }
        });
    }

    private void animations(View view) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        alpha.setDuration(1000).start();
        rotation.setDuration(1000).start();
    }

    /**
     * this method shows the current date in calendar header
     * and sets each days of month according to be shamsi or not
     */
    private void SetUpCalendar() {
        if (!shamsi) {
            setUpCalendarMainCalendar();
        } else {
            setUpShamsiCalendar();
        }
    }

    /**
     * if calendar is not shamsi this method just gives current month days and 41 items of dates to adaptor
     * to be checked and be added to each item of days if they exists in chosen month
     * this method adds header to sow year and month
     */
    private void setUpCalendarMainCalendar() {
        String currentDate = dateFormat.format(calendar.getTime());
        month.setText(currentDate);// set header
        dates.clear();
        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        monthCalendar.set(Calendar.HOUR, 0);
        monthCalendar.set(Calendar.MINUTE, 0);
        monthCalendar.set(Calendar.MILLISECOND, 0);
        monthCalendar.set(Calendar.SECOND, 0);
        int FirstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -FirstDayOfMonth);

        while (dates.size() < Max_CalendarDays) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        myGridAdapter = new GridAdapter(context, dates, calendar, false);
        gridView.setAdapter(myGridAdapter);
        setIconForMarkedDates();
    }

    /**
     * this method does what setUpCalendarMainCalendar() does in shamsi and
     * adds one column to the columns because sat in weekend
     */
    @SuppressLint("SetTextI18n")
    private void setUpShamsiCalendar() {
        PersianCalendar persianCalendar = new PersianCalendar();
        persianCalendar.setTimeInMillis(calendar.getTimeInMillis());
        month.setText(setPersianMonth(persianCalendar.get(Calendar.MONTH)) + "  " + persianCalendar.get(Calendar.YEAR));// set header
        dates.clear();
        Calendar monthCalendar = (Calendar) calendar.clone();

        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        persianCalendar.setTimeInMillis(monthCalendar.getTimeInMillis());
        while (persianCalendar.get(Calendar.DAY_OF_MONTH) != 1) {
            monthCalendar.add(Calendar.DAY_OF_MONTH, -1);
            persianCalendar.setTimeInMillis(monthCalendar.getTimeInMillis());

        }
        monthCalendar.set(Calendar.HOUR, 0);
        monthCalendar.set(Calendar.MINUTE, 0);
        monthCalendar.set(Calendar.MILLISECOND, 0);
        monthCalendar.set(Calendar.SECOND, 0);
        int FirstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -FirstDayOfMonth);


        dates.add(monthCalendar.getTime());//to shift columns when we use shamsi
        while (dates.size() < Max_CalendarDays ) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
            persianCalendar.setTimeInMillis(monthCalendar.getTimeInMillis());
        }

        //delete extra row when we have 7 unusable dates
        Calendar dateCalendar6 = Calendar.getInstance();
        dateCalendar6.setTime(dates.get(6));
        PersianCalendar persianCalendar6 = new PersianCalendar();
        persianCalendar6.setTimeInMillis(dateCalendar6.getTimeInMillis());
        Calendar dateCalendar7 = Calendar.getInstance();
        dateCalendar7.setTime(dates.get(7));
        PersianCalendar persianCalendar7 = new PersianCalendar();
        persianCalendar7.setTimeInMillis(dateCalendar7.getTimeInMillis());
        if (persianCalendar6.get(Calendar.MONTH) != persianCalendar7.get(Calendar.MONTH)){
            dates.subList(0, 7).clear();
        }
        myGridAdapter = new GridAdapter(context, dates, calendar, true);
        gridView.setAdapter(myGridAdapter);
        setIconForMarkedDates();
    }

    /**
     * this method will set icon for the days which they are marked
     * it check the date as milli sec is in our marked days array or not
     */
    private void setIconForMarkedDates() {
        myGridAdapter.setSetMarkOnDate((imageView, date) -> {
            if (markedDays != null) {
                if (markedDays.contains(date))//check to set ic or not
                {
                    animations(imageView);
                    imageView.setVisibility(VISIBLE);
                    imageView.setColorFilter(Color.parseColor(txtColor));
                } else {
                    imageView.setVisibility(GONE);
                }
            }
        });
    }

    /**
     * this method helps to set background and color for texts and icons in calendar
     *
     * @param txtColor is texts color
     * @param itemBackground is chosen item and current day background
     */
    public void setBackgroundsAndColors(String txtColor, Drawable itemBackground) {
        next.setColorFilter(Color.parseColor(txtColor));
        previous.setColorFilter(Color.parseColor(txtColor));
        month.setTextColor(Color.parseColor(txtColor));
        sat.setTextColor(Color.parseColor(txtColor));
        sun.setTextColor(Color.parseColor(txtColor));
        mon.setTextColor(Color.parseColor(txtColor));
        tue.setTextColor(Color.parseColor(txtColor));
        wed.setTextColor(Color.parseColor(txtColor));
        thu.setTextColor(Color.parseColor(txtColor));
        fri.setTextColor(Color.parseColor(txtColor));
        CustomCalendarView.txtColor = txtColor;
        CustomCalendarView.itemBackground = itemBackground;
    }

    /**
     *
     * @param markedDays are the list of the days as milli sec which we wanna be marked
     */
    public void setMarkedDays(ArrayList<Long> markedDays) {
        this.markedDays = markedDays;
        myGridAdapter.notifyDataSetChanged();
        setIconForMarkedDates();
    }


    /**
     *
     * @param setOnDateItemClickListener is an interface to set onItemClickListener
     */
    public void setOnDateItemClickListener(SetOnDateItemClickListener setOnDateItemClickListener) {
        this.setOnDateItemClickListener = setOnDateItemClickListener;
        myGridAdapter.notifyDataSetChanged();
    }

    /**
     * if this view is shamsi this method will change the name of dates to persian
     * @param shamsi is a boolean to shows the calendar is shamsi or not
     */
    public void setShamsi(boolean shamsi) {
        this.shamsi = shamsi;
        sat.setText("جمعه");
        sun.setText(" شنبه");
        mon.setText("یکشنبه");
        tue.setText("دوشنبه");
        wed.setText("سه شنبه");
        thu.setText("چهارشنبه");
        fri.setText("پنج شنبه");
        SetUpCalendar();
    }

    /**
     *
     * @param persianMonth shows the index of month
     * @return the name of the month witch we gave the index
     */
    public String setPersianMonth(int persianMonth) {
        switch (persianMonth) {
            case 0:
                return "فروردین";
            case 1:
                return "اردیبهشت";
            case 2:
                return "خرداد";
            case 3:
                return "تیر";
            case 4:
                return "مرداد";
            case 5:
                return "شهریور";
            case 6:
                return "مهر";
            case 7:
                return "آبان";
            case 8:
                return "آذر";
            case 9:
                return "دی";
            case 10:
                return "بهمن";
            case 11:
                return "اسفند";
        }
        return "not available";
    }


}

