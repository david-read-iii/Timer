package com.davidread.timer;

import android.os.SystemClock;

import java.util.Locale;

/**
 * {@link TimerModel} models a timer that can be started, paused, resumed, or stopped.
 */
public class TimerModel {

    /**
     * The time at which {@link SystemClock#uptimeMillis()} will be at when this {@link TimerModel}
     * has completed its countdown.
     */
    private long mTargetTime;

    /**
     * The time remaining for this {@link TimerModel}.
     */
    private long mTimeLeft;

    /**
     * Boolean indicating whether this {@link TimerModel} is currently running.
     */
    private boolean mRunning;

    /**
     * The initial time interval this {@link TimerModel} starts counting down from.
     */
    private long mDurationMillis;

    /**
     * Constructs a new {@link TimerModel}.
     */
    public TimerModel() {
        mRunning = false;
    }

    /**
     * Returns true if this {@link TimerModel} is in a running state.
     *
     * @return True if this {@link TimerModel} is in a running state.
     */
    public boolean isRunning() {
        return mRunning;
    }

    /**
     * Has this {@link TimerModel} start to countdown time starting from the time interval passed
     * in milliseconds.
     *
     * @param millisLeft Time interval in milliseconds to begin counting down from.
     */
    public void start(long millisLeft) {
        mDurationMillis = millisLeft;
        mTargetTime = SystemClock.uptimeMillis() + mDurationMillis;
        mRunning = true;
    }

    /**
     * Has this {@link TimerModel} start to countdown time starting from the time interval passed
     * in hours, minutes, and seconds.
     *
     * @param hours   Hours component of the time interval to begin counting down from.
     * @param minutes Minutes component of the time interval to begin counting down from.
     * @param seconds Seconds component of the time interval to begin counting down from.
     */
    public void start(int hours, int minutes, int seconds) {
        // Add 1 sec to duration so timer stays on current second longer.
        mDurationMillis = (hours * 60 * 60 + minutes * 60 + seconds + 1) * 1000;
        mTargetTime = SystemClock.uptimeMillis() + mDurationMillis;
        mRunning = true;
    }

    /**
     * Stops this {@link TimerModel} from counting down time.
     */
    public void stop() {
        mRunning = false;
    }

    /**
     * Pauses the counting down of time for this {@link TimerModel}.
     */
    public void pause() {
        mTimeLeft = mTargetTime - SystemClock.uptimeMillis();
        mRunning = false;
    }

    /**
     * Resumes the counting down of time for this {@link TimerModel}.
     */
    public void resume() {
        mTargetTime = SystemClock.uptimeMillis() + mTimeLeft;
        mRunning = true;
    }

    /**
     * Returns a time interval in milliseconds representing how much longer this {@link TimerModel}
     * must countdown.
     *
     * @return A time interval in milliseconds.
     */
    public long getRemainingMilliseconds() {
        if (mRunning) {
            return Math.max(0, mTargetTime - SystemClock.uptimeMillis());
        }
        return 0;
    }

    /**
     * Returns the seconds component of a time interval representing how much longer this
     * {@link TimerModel} must countdown.
     *
     * @return The seconds component of a time interval.
     */
    public int getRemainingSeconds() {
        if (mRunning) {
            return (int) ((getRemainingMilliseconds() / 1000) % 60);
        }
        return 0;
    }

    /**
     * Returns the minutes component of a time interval representing how much longer this
     * {@link TimerModel} must countdown.
     *
     * @return The minutes component of a time interval.
     */
    public int getRemainingMinutes() {
        if (mRunning) {
            return (int) (((getRemainingMilliseconds() / 1000) / 60) % 60);
        }
        return 0;
    }

    /**
     * Returns the hours component of a time interval representing how much longer this
     * {@link TimerModel} must countdown.
     *
     * @return The hours component of a time interval.
     */
    public int getRemainingHours() {
        if (mRunning) {
            return (int) (((getRemainingMilliseconds() / 1000) / 60) / 60);
        }
        return 0;
    }

    /**
     * Returns a progress percent representing how much longer this {@link TimerModel} must
     * countdown.
     *
     * @return A progress percentage between 0 and 100.
     */
    public int getProgressPercent() {
        if (mDurationMillis != 1000) {
            return Math.min(100, 100 - (int) ((getRemainingMilliseconds() - 1000) * 100 /
                    (mDurationMillis - 1000)));
        }
        return 0;
    }

    /**
     * Returns a {@link String} representation of this {@link TimerModel}.
     *
     * @return A {@link String} representation of this {@link TimerModel}.
     */
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", getRemainingHours(),
                getRemainingMinutes(), getRemainingSeconds());
    }
}