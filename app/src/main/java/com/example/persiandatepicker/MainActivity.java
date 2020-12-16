package com.example.persiandatepicker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import com.example.persiandatepicker.DB.MyDBHelper;
import com.example.persiandatepicker.Notification.NotificationPublisher;
import com.example.persiandatepicker.UI.Dialog.MyTimePicker;
import com.example.persiandatepicker.UI.Dialog.SaveTimeListener;
import com.example.persiandatepicker.UI.MyShowEventsView;
import com.example.persiandatepicker.UI.OnShowEventsListener;
import com.example.persiandatepickerlibrary.CustomCalendarView;
import com.example.persiandatepickerlibrary.Listeners.SetOnDateItemClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * this activity represents main activity
 * date picker and events will be handle and relate here
 * alarm manager and database will be control here
 *
 * @author : zahra fatehi
 * @version  : 0.0
 */
public class MainActivity extends AppCompatActivity {


    private MyShowEventsView myShowEventsView;
    private MyDBHelper myDBHelper;
    private CustomCalendarView customCalendarView;
    private Long chosenDate = System.currentTimeMillis();

    //bright #F8EFBA
    //dark #222f3e
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (chosenDate == null) {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            cal.clear();
            cal.set(year, month, day, 0, 0, 0);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            chosenDate = cal.getTimeInMillis();
        }

        myDBHelper = new MyDBHelper(this);
        init();
        addListener();
        //ContextCompat.getDrawable(this, R.drawable.bright)
    }

    /**
     * initialize components
     */
    private void init() {
        myShowEventsView = findViewById(R.id.my_show_events_view);
        myShowEventsView.makeTextEnable(false, "choose a date to add a text");
        customCalendarView = findViewById(R.id.my_custom_calendar);

        addCalendar();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void addCalendar() {
        customCalendarView.setBackgroundsAndColors("#F8EFBA", getResources().getDrawable(R.drawable.item_bg));
        customCalendarView.setShamsi(true);
        customCalendarView.setMarkedDays(getMarkedDates());
        customCalendarView.setOnDateItemClickListener(dateCalendar -> {
            myShowEventsView.makeTextEnable(true, "add txt");
            chosenDate = dateCalendar.getTimeInMillis();
            setDescription();
        });
    }

    /**
     * this method get day , month and day and returns it as a date string
     *
     * @return an array of chosen dates which are in data base
     */
    private ArrayList<Long> getMarkedDates() {
        ArrayList<Long> dates = new ArrayList<>();
        Cursor cursor = myDBHelper.showAllData();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                dates.add(cursor.getLong(cursor.getColumnIndex("date")));
            }
        }
        cursor.close();
        myDBHelper.close();
        return dates;
    }


    private void setDescription() {
        String description = myDBHelper.getDescription(chosenDate);
        if (description != null) {
            myShowEventsView.setDescription(description);
            myShowEventsView.setAlarmIcon(myDBHelper.getAlarm(chosenDate));
        } else {
            myShowEventsView.setDescription("");
            myShowEventsView.setAlarmIcon(0);
        }
        myDBHelper.close();
    }

    /**
     * add listeners for components and handel insert update and delete in database according to components which you click on
     */
    private void addListener() {
        myShowEventsView.setOnShowEventsListener(new OnShowEventsListener() {
            @Override
            public void onSaveClick(String description) {// save data in db(first check  it needs to be update or insert)
                if (myDBHelper.insertData(chosenDate, description)) {
                    customCalendarView.setMarkedDays(getMarkedDates());
                    Toast.makeText(MainActivity.this, "inserted", Toast.LENGTH_SHORT).show();
                } else if (myDBHelper.updateData(chosenDate, description))
                    Toast.makeText(MainActivity.this, "updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSetAlarmClick(String description) {
                if(!checkCanSetAlarm(description)){
                    Toast.makeText(MainActivity.this , "time is passed , you can not add alarm" ,Toast.LENGTH_SHORT ).show();
                    return;
                }
                int hasAlarm = myDBHelper.getAlarm(chosenDate);
                if (hasAlarm != 0) {
                    cancelAlarm();
                    myDBHelper.updateAlarm(chosenDate, description, 0);
                    myShowEventsView.setAlarmIcon(0);
                } else {
                    MyTimePicker myTimePicker = new MyTimePicker(MainActivity.this);
                    myTimePicker.show();
                    myTimePicker.setSaveTimeListener((hourOfDay, minute) -> {//set alarm
                        myDBHelper.insertData(chosenDate, description);
                        if (myDBHelper.updateAlarm(chosenDate, description, 1)) {
                            Toast.makeText(MainActivity.this, "alarm added", Toast.LENGTH_SHORT).show();
                            Calendar chosenDateCalendar = Calendar.getInstance();
                            chosenDateCalendar.setTimeInMillis(chosenDate);
                            chosenDateCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            chosenDateCalendar.set(Calendar.MINUTE, minute);
                            setAlarm(chosenDateCalendar, description, chosenDateCalendar.get(Calendar.YEAR) + "/" +
                                    (chosenDateCalendar.get(Calendar.MONTH) - 1) + "/" + chosenDateCalendar.get(Calendar.DAY_OF_MONTH));
                            myShowEventsView.setAlarmIcon(1);
                        }
                    });
                }
            }

            @Override
            public void onDeleteClick() {
                cancelAlarm();
                if (myDBHelper.deleteData(chosenDate)) {
                    myShowEventsView.setDescription("");
                    myShowEventsView.setAlarmIcon(0);
                    customCalendarView.setMarkedDays(getMarkedDates());
                    Toast.makeText(MainActivity.this, "note deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkCanSetAlarm(String description) {
        if (description == null || description.equals("")) {
            Toast.makeText(MainActivity.this, "plz enter txt", Toast.LENGTH_SHORT).show();
            return false;
        }
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());
        Calendar chosen = Calendar.getInstance();
        chosen.setTimeInMillis(chosenDate);
        System.out.println(System.currentTimeMillis() +"     <     "+chosenDate);
        if (System.currentTimeMillis() < chosenDate){
            return true;
        }
        else if ( System.currentTimeMillis()== today.get(Calendar.YEAR)
        && today.get(Calendar.MONTH) == chosen.get(Calendar.MONTH)
        && today.get(Calendar.YEAR) == chosen.get(Calendar.YEAR)){//todo handle today for passed hours and minutes
            return true;
        }
        return false;
    }

    /**
     * cancel alarm manager
     */
    private void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), NotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, getRequestCode(), intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * set alarm with alarm manager
     * @param txt the txt of notification
     * @param time the date of showing notification
     */
    private void setAlarm(Calendar calendar, String txt, String time) {
        Intent intent = new Intent(getApplicationContext(), NotificationPublisher.class);
        intent.putExtra("txt", txt);
        intent.putExtra("time", time);
        intent.putExtra("id", getRequestCode());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, getRequestCode(), intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    /**
     * this method provides the request code for alarm according to the chosen date
     * @return the request code
     */
    private int getRequestCode() {
        int code;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(chosenDate);
        code = (Integer.parseInt(calendar.get(Calendar.YEAR) + "" + (calendar.get(Calendar.MONTH) - 1) + "" +
                calendar.get(Calendar.DAY_OF_MONTH)));

        myDBHelper.close();
        return code;
    }

}