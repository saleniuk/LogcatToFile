package com.saleniuk.logcattofile;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.permissioneverywhere.PermissionEverywhere;
import com.permissioneverywhere.PermissionResponse;
import com.permissioneverywhere.PermissionResultCallback;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LogcatToFile {

    private static final int NOTIFICATION_ID = 0;

    // determines if logs are being saved to file or not
    private static boolean isRunning = false;

    // file to which logs are saved
    private static File logDirectory;

    // list of logcat parameters
    private static Parameter[] parameters;

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


    public static void init(final Context context, final File logDirectory, final Parameter... parameters) {

        LogcatToFile.logDirectory = logDirectory;
        LogcatToFile.isRunning = true;
        LogcatToFile.parameters = parameters;

        // register receivers to handle state changes
        NotificationDeleteReceiver notificationDeleteReceiver = new NotificationDeleteReceiver();
        context.registerReceiver(notificationDeleteReceiver,
                new IntentFilter(context.getPackageName() + ".stop.logs"));
        context.registerReceiver(notificationDeleteReceiver,
                new IntentFilter(context.getPackageName() + ".start.logs"));
        context.registerReceiver(notificationDeleteReceiver,
                new IntentFilter(context.getPackageName() + ".close.logs"));

        // create log folder
        logDirectory.mkdirs();

        if (context.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED
                || context.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED
                || context.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            PermissionEverywhere.getPermission(context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.READ_LOGS},
                    1122,
                    context.getApplicationInfo().name,
                    "This app needs permissions",
                    context.getApplicationInfo().icon)
                    .enqueue(new PermissionResultCallback() {
                        @Override
                        public void onComplete(PermissionResponse permissionResponse) {
                            Toast.makeText(context, "Granted: " + permissionResponse.isGranted(),
                                    Toast.LENGTH_SHORT).show();
                            init(context, logDirectory, parameters);
                        }
                    });
            return;
        }

        stopLogs(context);
    }

    public static void startLogs(Context context) {
        startLogs(context, "");
    }

    public static void startLogs(Context context, String logFilePrefix) {
        if (isExternalStorageWritable()) {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            final File logFile = new File(logDirectory,
                    (logFilePrefix + "_" + simpleDateFormat.format(Calendar.getInstance().getTime()) + ".txt"));

            // clear the previous logcat and then write the new one to the file
            try {
                Parameter[] parametersToSet = parameters;
                if (parametersToSet == null || (parametersToSet != null && parametersToSet.length == 0)) {
                    parametersToSet = new Parameter[]{new Parameter("*", Priority.DEBUG)};
                }

                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile + StringUtils.join(" ",
                        parametersToSet));

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setLargeIcon(
                                BitmapFactory.decodeResource(context.getResources(), context.getApplicationInfo().icon))
                        .setSmallIcon(R.drawable.icon_record)
                        .setContentTitle(context.getApplicationInfo().name)
                        .setContentText("Log session running")
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .setContentIntent(PendingIntent.getBroadcast(
                                context, 0, new Intent(context.getPackageName() + ".stop.logs"),
                                0))
                        .addAction(R.drawable.icon_stop, "Stop", PendingIntent.getBroadcast(
                                context, 0, new Intent(context.getPackageName() + ".stop.logs"),
                                0))
                        .setPriority(NotificationCompat.PRIORITY_MAX);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (isExternalStorageReadable()) {
            // only readable
        } else {
            // not accessible
        }
    }

    public static void stopLogs(Context context) {

        try {
            Process process = Runtime.getRuntime().exec("logcat -c");
            process = Runtime.getRuntime().exec("logcat -f stdout");
            isRunning = false;

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setLargeIcon(
                            BitmapFactory.decodeResource(context.getResources(), context.getApplicationInfo().icon))
                    .setSmallIcon(R.drawable.icon_stop)
                    .setContentTitle(context.getApplicationInfo().name)
                    .setContentText("Log session stopped")
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setContentIntent(PendingIntent.getBroadcast(
                            context, 0, new Intent(context.getPackageName() + ".start.logs"),
                            0))
                    .addAction(R.drawable.icon_record, "Start", PendingIntent.getBroadcast(
                            context, 0, new Intent(context.getPackageName() + ".start.logs"),
                            0))
                    .addAction(R.drawable.icon_close, "Close", PendingIntent.getBroadcast(
                            context, 0, new Intent(context.getPackageName() + ".close.logs"),
                            0))
                    .setPriority(NotificationCompat.PRIORITY_MAX);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class NotificationDeleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(context.getPackageName() + ".stop.logs")) {
                stopLogs(context);
            } else if (intent.getAction().equals(context.getPackageName() + ".start.logs")) {
//                startLogs(context);
                context.startActivity(new Intent(context, SessionNameActivity.class));
                context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
            } else if (intent.getAction().equals(context.getPackageName() + ".close.logs")) {
                NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID);
            }
        }
    }
}
