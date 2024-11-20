package com.example.screentimetracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScreenTimeService extends Service {

    private int timeLimit; // Time limit in minutes
    private long totalScreenTime = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timeLimit = intent.getIntExtra("timeLimit", 0) * 60 * 1000; // Convert to milliseconds

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                trackScreenTime();
            }
        }, 0, 60000); // Check every minute

        return START_STICKY;
    }

    private void trackScreenTime() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 60000; // Last 1 minute

        List<UsageStats> stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        for (UsageStats usageStat : stats) {
            totalScreenTime += usageStat.getTotalTimeInForeground();
        }

        if (totalScreenTime >= timeLimit) {
            sendNotification();
            totalScreenTime = 0; // Reset after notification
        }
    }

    private void sendNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelId = "screen_time_channel";
        String channelName = "Screen Time Notifications";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Time Limit Exceeded")
                .setContentText("You've exceeded your screen time limit. Consider taking a break!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(1, builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
