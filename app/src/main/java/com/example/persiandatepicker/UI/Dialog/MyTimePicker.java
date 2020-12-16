package com.example.persiandatepicker.UI.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.example.persiandatepicker.R;

/**
 * this class represents a custom dialog to select a time for alarm manager
 */
public class MyTimePicker extends Dialog {

    private Button save;
    private TimePicker timePicker;
    private SaveTimeListener saveTimeListener;
    private int hourOfDay;
    private int minute;

    public MyTimePicker(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_picker);
        setCancelable(true);
        save = findViewById(R.id.saveTime);
        timePicker =(TimePicker) findViewById(R.id.mySimpleTimePicker);
        timePicker.setIs24HourView(true);
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveTimeListener.saveTime(hourOfDay , minute);
                dismiss();
            }
        });


        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int myHourOfDay, int myMinute) {
                hourOfDay = myHourOfDay;
                minute = myMinute;
            }
        });
        dismiss();

    }

    public void setSaveTimeListener(SaveTimeListener saveTimeListener) {
        this.saveTimeListener = saveTimeListener;
    }
}
