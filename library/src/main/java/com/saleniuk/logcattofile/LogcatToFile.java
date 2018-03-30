package com.saleniuk.logcattofile;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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

public class LogcatToFile {

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


    public static void init(final Context context, final File logFile, final Parameter... parameters) {

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
                            init(context, logFile, parameters);
                        }
                    });
            return;
        }

        if (isExternalStorageWritable()) {

            // create log folder
            logFile.getParentFile().mkdirs();

            // clear the previous logcat and then write the new one to the file
            try {
                Parameter[] parametersToSet = parameters;
                if (parametersToSet == null || (parametersToSet != null && parametersToSet.length == 0)) {
                    parametersToSet = new Parameter[]{new Parameter("*", Priority.DEBUG)};
                }
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile + StringUtils.join(" ", parametersToSet));

                context.registerReceiver(new NotificationDeleteReceiver(),
                        new IntentFilter(context.getPackageName() + ".stop.logs"));

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(context.getApplicationInfo().icon)
                        .setContentTitle(context.getApplicationInfo().name)
                        .setContentText("Log session running, click to stop")
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .setContentIntent(PendingIntent.getBroadcast(
                                context, 0, new Intent(context.getPackageName() + ".stop.logs"), 0))
                        .setDeleteIntent(PendingIntent.getBroadcast(
                                context, 0, new Intent(context.getPackageName() + ".stop.logs"), 0))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(0, mBuilder.build());

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (isExternalStorageReadable()) {
            // only readable
        } else {
            // not accessible
        }
    }

    static class NotificationDeleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f stdout");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
