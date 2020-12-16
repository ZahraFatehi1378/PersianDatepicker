package com.example.persiandatepicker.UI;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.persiandatepicker.R;


/**
 * this class represents the events of each day that includes description and alarm
 *
 * @author :zahra fatehi
 * @version :0:0
 */

public class MyShowEventsView extends LinearLayout {

    private final Context context;
    private Button save;
    private ImageView delete, alarm;
    private EditText textEvent;
    private OnShowEventsListener setOnShowEventsListener;

    public MyShowEventsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializeLayout();
        addListeners();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initializeLayout() {
        View view = LayoutInflater.from(context).inflate(R.layout.my_show_events_layout, this, true);
        alarm = view.findViewById(R.id.chooseTime);
        delete = view.findViewById(R.id.clear_text);
        textEvent = view.findViewById(R.id.textEvent);
        save = view.findViewById(R.id.saveEvent);
    }

    /**
     * this method represents the listeners
     */
    private void addListeners() {
        save.setOnClickListener(v -> setOnShowEventsListener.onSaveClick(textEvent.getText().toString()));

        delete.setOnClickListener(v -> setOnShowEventsListener.onDeleteClick());

        alarm.setOnClickListener(v -> setOnShowEventsListener.onSetAlarmClick(textEvent.getText().toString()));
    }

    public void setOnShowEventsListener(OnShowEventsListener setOnShowEventsListener) {
        this.setOnShowEventsListener = setOnShowEventsListener;
    }

    /**
     * @param description is the text that we wanna be add when we click on an item
     */
    public void setDescription(String description) {
        textEvent.setText(description);
    }

    /**
     * this method make bell yellow if we have alarm on that day
     *
     * @param hasAlarm shows we have alarm or not
     */
    public void setAlarmIcon(int hasAlarm) {
        if (hasAlarm != 0) {
            alarm.setColorFilter(Color.parseColor("#f1c40f"));
            animations(alarm);
        } else
            alarm.setColorFilter(Color.parseColor("#f2dbb2"));
        animations(alarm);
    }

    private void animations(View view) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        alpha.setDuration(1000).start();
        rotation.setDuration(1000).start();
    }

    public void makeTextEnable(boolean isEnable, String hintTxt) {
        textEvent.setEnabled(isEnable);
        textEvent.setHint(hintTxt);
    }
}
