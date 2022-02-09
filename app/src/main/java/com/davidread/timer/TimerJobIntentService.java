package com.davidread.timer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * {@link TimerJobIntentService} is a class that continues a countdown timer started by
 * {@link MainActivity} in a background task. It displays the countdown timer UI in a
 * {@link Notification}.
 */
public class TimerJobIntentService extends JobIntentService {

    /**
     * {@link String} identifier for the extra containing the remaining milliseconds the countdown
     * timer should be initialized with.
     */
    public static final String EXTRA_MILLIS_LEFT = "com.davidread.timer.extra.EXTRA_MILLIS_LEFT";

    /**
     * {@link String} identifier for the {@link NotificationChannel} used in this class.
     */
    private final String CHANNEL_ID_TIMER = "channel_timer";

    /**
     * Int identifier for the {@link Notification} used in this class.
     */
    private final int NOTIFICATION_ID = 0;

    /**
     * Invoked to initialize this {@link TimerJobIntentService}. It calls
     * {@link #enqueueWork(Context, Class, int, Intent)} to enqueue work for this
     * {@link TimerJobIntentService}.
     *
     * @param context         {@link Context} for the superclass.
     * @param remainingMillis The value to start counting down from for the countdown timer.
     */
    public static void startJob(Context context, long remainingMillis) {
        Intent intent = new Intent(context, TimerJobIntentService.class);
        intent.putExtra(EXTRA_MILLIS_LEFT, remainingMillis);
        enqueueWork(context, TimerJobIntentService.class, 0, intent);
    }

    /**
     * Invoked by {@link JobIntentService} when it is this service's turn to execute. It creates
     * a new {@link TimerModel} to model the counting down of time and creates a new
     * {@link Notification} every second. Once the {@link TimerModel} is done running, it creates
     * one last {@link Notification} to indicate that the countdown is complete.
     *
     * @param intent {@link Intent} that started this service.
     */
    @Override
    protected void onHandleWork(Intent intent) {

        // Get millis from the activity and start a new TimerModel.
        long millisLeft = intent.getLongExtra(EXTRA_MILLIS_LEFT, 0);
        TimerModel timerModel = new TimerModel();
        timerModel.start(millisLeft);

        // Create notification channel.
        createTimerNotificationChannel();

        while (timerModel.isRunning()) {
            try {
                createTimerNotification(timerModel.toString());
                Thread.sleep(1000);

                if (timerModel.getRemainingMilliseconds() == 0) {
                    timerModel.stop();
                    createTimerNotification("Timer is finished!");
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * Registers a new {@link NotificationChannel} with {@link NotificationManager} for this class.
     */
    private void createTimerNotificationChannel() {

        // Notification channels were introduced in API 26.
        if (Build.VERSION.SDK_INT < 26)
            return;

        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID_TIMER, name, importance);
        channel.setDescription(description);

        // Register notification channel with system.
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * Posts a new {@link Notification} displaying the passed {@link String}. Subsequent calls
     * overwrite previously posted {@link Notification} objects.
     *
     * @param text {@link String} containing text to display in the {@link Notification} content
     *             text.
     */
    private void createTimerNotification(String text) {

        // Create notification with various properties.
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_TIMER)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        // Get compatibility NotificationManager.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Post notification using ID. If same ID, this notification replaces previous one.
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}