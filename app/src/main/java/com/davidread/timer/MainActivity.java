package com.davidread.timer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * {@link MainActivity} represents a time picker and countdown timer user interface. It has two
 * modes. The first mode has the user select a time to begin counting down from using
 * {@link #mHoursPicker}, {@link #mMinutesPicker}, and {@link #mHoursPicker}. Clicking
 * {@link #mStartButton} puts the activity in the second mode. THe second mode shows the countdown
 * time decrementing in {@link #mTimeLeftTextView} and {@link #mProgressBar}. Clicking
 * {@link #mPauseButton} pauses the countdown decrement. Clicking {@link #mCancelButton} puts the
 * activity back in the first mode.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * {@link NumberPicker} for specifying the hours component of the time interval to begin
     * counting down from.
     */
    private NumberPicker mHoursPicker;

    /**
     * {@link NumberPicker} for specifying the minutes component of the time interval to begin
     * counting down from.
     */
    private NumberPicker mMinutesPicker;

    /**
     * {@link NumberPicker} for specifying the seconds component of the time interval to begin
     * counting down from.
     */
    private NumberPicker mSecondsPicker;

    /**
     * {@link Button} that starts the countdown timer on click.
     */
    private Button mStartButton;

    /**
     * {@link Button} that pauses the countdown timer on click.
     */
    private Button mPauseButton;

    /**
     * {@link Button} that clears the countdown timer on click.
     */
    private Button mCancelButton;

    /**
     * {@link ProgressBar} providing a visual depiction of how much time the countdown timer has
     * left to countdown.
     */
    private ProgressBar mProgressBar;

    /**
     * {@link TextView} providing a text depiction of how much time the countdown timer has left to
     * countdown.
     */
    private TextView mTimeLeftTextView;

    /**
     * {@link Handler} for receiving multiple posts of {@link #mUpdateTimerRunnable} that update
     * this activity's UI with the latest information from {@link #mTimerModel}.
     */
    private Handler mHandler;

    /**
     * {@link TimerModel} for counting down time starting from a specific time interval.
     */
    private TimerModel mTimerModel;

    /**
     * {@link Runnable} that updates this activity's UI with the amount of time remaining in the
     * countdown. It assigns itself to run again after 200ms if the countdown hasn't completed.
     */
    private final Runnable mUpdateTimerRunnable = new Runnable() {
        @Override
        public void run() {

            // Update UI to show remaining time and progress.
            mTimeLeftTextView.setText(mTimerModel.toString());
            int progress = mTimerModel.getProgressPercent();
            mProgressBar.setProgress(progress);

            // Only post Runnable if more time remains.
            if (progress == 100) {
                timerCompleted();
            } else {
                mHandler.postDelayed(this, 200);
            }
        }
    };

    /**
     * Invoked once when this activity is initially created. It initializes member variables and
     * puts this activity in the first mode.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initially hide the timer and progress bar.
        mTimeLeftTextView = findViewById(R.id.time_left_text_view);
        mTimeLeftTextView.setVisibility(View.INVISIBLE);
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mStartButton = findViewById(R.id.start_button);
        mPauseButton = findViewById(R.id.pause_button);
        mCancelButton = findViewById(R.id.cancel_button);

        // Hide pause and cancel buttons until the timer starts.
        mPauseButton.setVisibility(View.GONE);
        mCancelButton.setVisibility(View.GONE);

        // Show 2 digits in NumberPickers.
        NumberPicker.Formatter numFormat = i -> new DecimalFormat("00").format(i);

        // Set min and max values for all NumberPickers.
        mHoursPicker = findViewById(R.id.hours_picker);
        mHoursPicker.setMinValue(0);
        mHoursPicker.setMaxValue(99);
        mHoursPicker.setFormatter(numFormat);

        mMinutesPicker = findViewById(R.id.minutes_picker);
        mMinutesPicker.setMinValue(0);
        mMinutesPicker.setMaxValue(59);
        mMinutesPicker.setFormatter(numFormat);

        mSecondsPicker = findViewById(R.id.seconds_picker);
        mSecondsPicker.setMinValue(0);
        mSecondsPicker.setMaxValue(59);
        mSecondsPicker.setFormatter(numFormat);

        mTimerModel = new TimerModel();

        // Instantiate Handler so Runnables can be posted to UI message queue.
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Invoked when {@link #mStartButton} is clicked. It gets the initial time interval from
     * {@link #mHoursPicker}, {@link #mMinutesPicker}, and {@link #mSecondsPicker}. Then, it puts
     * the activity in the second mode, starts {@link #mTimerModel}, and has {@link #mHandler}
     * update the UI every 200ms.
     */
    public void startButtonClick(View view) {
        // Get values from NumberPickers.
        int hours = mHoursPicker.getValue();
        int minutes = mMinutesPicker.getValue();
        int seconds = mSecondsPicker.getValue();

        if (hours + minutes + seconds > 0) {
            // Show progress.
            mTimeLeftTextView.setVisibility(View.VISIBLE);
            mProgressBar.setProgress(0);
            mProgressBar.setVisibility(View.VISIBLE);

            // Show only Pause and Cancel buttons.
            mStartButton.setVisibility(View.GONE);
            mPauseButton.setVisibility(View.VISIBLE);
            mPauseButton.setText(R.string.pause);
            mCancelButton.setVisibility(View.VISIBLE);

            // Start the model
            mTimerModel.start(hours, minutes, seconds);

            // Start sending Runnables to message queue.
            mHandler.post(mUpdateTimerRunnable);
        }
    }

    /**
     * Invoked when {@link #mPauseButton} is clicked. If {@link #mTimerModel} is running, it
     * pauses it. If {@link #mTimerModel} is not running, it resumes it.
     */
    public void pauseButtonClick(View view) {
        if (mTimerModel.isRunning()) {
            // Pause and change to resume button
            mTimerModel.pause();
            mHandler.removeCallbacks(mUpdateTimerRunnable);
            mPauseButton.setText(R.string.resume);
        } else {
            // Resume and change to pause button
            mTimerModel.resume();
            mHandler.post(mUpdateTimerRunnable);
            mPauseButton.setText(R.string.pause);
        }
    }

    /**
     * Invoked when {@link #mCancelButton} is clicked. It cancels {@link #mTimerModel} and puts
     * the activity back in the first mode.
     */
    public void cancelButtonClick(View view) {
        mTimeLeftTextView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        timerCompleted();
    }

    /**
     * Stops {@link #mTimerModel}, stops {@link #mHandler} from further updating the UI, and puts
     * the UI back into the first mode.
     */
    private void timerCompleted() {
        mTimerModel.stop();

        // Remove any remaining Runnables that may reside in UI message queue
        mHandler.removeCallbacks(mUpdateTimerRunnable);

        // Show only the start button
        mStartButton.setVisibility(View.VISIBLE);
        mPauseButton.setVisibility(View.GONE);
        mCancelButton.setVisibility(View.GONE);
    }
}