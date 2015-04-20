/**
 * Copyright 2015 Willian Gustavo Veiga
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.willianveiga.countdowntimer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.willianveiga.countdowntimer.Time.CountdownTimer;
import com.willianveiga.countdowntimer.Utils.AlertDialogUtils;
import com.willianveiga.countdowntimer.Utils.StringUtils;
import com.willianveiga.countdowntimer.Utils.TimeUtils;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends Activity implements View.OnClickListener, Observer {
    private CountdownTimer countdownTimer;

    private EditText hoursEditText;
    private EditText minutesEditText;
    private EditText secondsEditText;
    private Button startPauseResumeButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        countdownTimer = new CountdownTimer();
        countdownTimer.addObserver(this);

        setViewFields();
        addEventListeners();
    }

    private void setViewFields() {
        hoursEditText = (EditText) findViewById(R.id.hoursEditText);
        minutesEditText = (EditText) findViewById(R.id.minutesEditText);
        secondsEditText = (EditText) findViewById(R.id.secondsEditText);
        startPauseResumeButton = (Button) findViewById(R.id.startPauseResumeButton);
        stopButton = (Button) findViewById(R.id.stopButton);
    }

    private void addEventListeners() {
        startPauseResumeButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startPauseResumeButton:
                switch (((Button) view).getText().toString()) {
                    case "Start":
                    case "Resume":
                        startResumeCountdownTimer();
                        break;
                    case "Pause":
                        pauseCountdownTimer();
                        break;
                }
                break;
            case R.id.stopButton:
                stopCountdownTimer();
                break;
        }
    }

    private void pauseCountdownTimer() {
        countdownTimer.stop();
        startPauseResumeButton.setText(R.string.resume);
    }

    private void startResumeCountdownTimer() {
        String hoursString = hoursEditText.getText().toString();
        String minutesString = minutesEditText.getText().toString();
        String secondsString = secondsEditText.getText().toString();

        if (hoursString.isEmpty() || minutesString.isEmpty() || secondsString.isEmpty()) {
            showFieldsEmptyAlertDialog();
            return;
        }

        int hours = Integer.parseInt(hoursEditText.getText().toString());
        int minutes = Integer.parseInt(minutesEditText.getText().toString());
        int seconds = Integer.parseInt(secondsEditText.getText().toString());
        long milliseconds = TimeUtils.toMilliseconds(hours, minutes, seconds);

        if (milliseconds == 0) {
            showMinimumTimeAlertDialog();
            return;
        }

        countdownTimer.start(milliseconds);

        startPauseResumeButton.setText(R.string.pause);

        findViewById(R.id.mainActivityRelativeLayout).requestFocus();

        hoursEditText.setEnabled(false);
        minutesEditText.setEnabled(false);
        secondsEditText.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void showFieldsEmptyAlertDialog() {
        AlertDialogUtils.create(this, R.string.fields_empty_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).show();
    }

    private void showMinimumTimeAlertDialog() {
        AlertDialogUtils.create(this, R.string.minimum_time_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).show();
    }

    private void stopCountdownTimer() {
        countdownTimer.stop();
        resetUserInterface();
    }

    private void resetUserInterface() {
        String initialTimeUnit = getResources().getString(R.string.initial_time_unit);
        hoursEditText.setText(initialTimeUnit);
        minutesEditText.setText(initialTimeUnit);
        secondsEditText.setText(initialTimeUnit);

        hoursEditText.setEnabled(true);
        minutesEditText.setEnabled(true);
        secondsEditText.setEnabled(true);

        stopButton.setEnabled(false);

        startPauseResumeButton.setText(R.string.start);
    }

    /**
     * TODO
     * CountdownTimer notifies its observers in two different situations.
     * How could I distinguish both?
     * This if/else checking the value smells ...
     */
    @Override
    public void update(Observable countdownTimer, Object milliseconds) {
        if (milliseconds != null) {
            updateTimeFields((long) milliseconds);
        } else {
            timeFinished();
        }
    }

    private void updateTimeFields(long milliseconds) {
        hoursEditText.setText(StringUtils.padTimeUnit(TimeUtils.millisecondsToHours(milliseconds)));
        minutesEditText.setText(StringUtils.padTimeUnit(TimeUtils.millisecondsToMinutes(milliseconds)));
        secondsEditText.setText(StringUtils.padTimeUnit(TimeUtils.millisecondsToSeconds(milliseconds)));
    }

    private void timeFinished() {
        vibrate();
        showTimeFinishedAlertDialog();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1500);
    }

    private void showTimeFinishedAlertDialog() {
        AlertDialogUtils.create(this, R.string.countdown_over_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                resetUserInterface();
                dialogInterface.cancel();
            }
        }).show();
    }
}