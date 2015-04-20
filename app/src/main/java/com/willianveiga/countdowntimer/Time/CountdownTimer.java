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

package com.willianveiga.countdowntimer.Time;

import android.os.CountDownTimer;

import java.util.Observable;

public class CountdownTimer extends Observable {
    private CountDownTimer countdownTimer;

    public void start(long milliseconds) {
        int countdownInterval = 1000;
        countdownTimer = new CountDownTimer(milliseconds, countdownInterval) {
            public void onTick(long millisUntilFinished) {
                setChanged();
                notifyObservers(millisUntilFinished);
            }

            public void onFinish() {
                setChanged();
                notifyObservers();
            }
        };
        countdownTimer.start();
    }

    public void stop() {
        countdownTimer.cancel();
    }
}