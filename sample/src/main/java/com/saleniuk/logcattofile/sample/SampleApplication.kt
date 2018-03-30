package com.saleniuk.logcattofile.sample

import android.app.Application
import android.os.Environment
import com.saleniuk.logcattofile.LogcatToFile
import com.saleniuk.logcattofile.Parameter
import com.saleniuk.logcattofile.Priority
import java.io.File

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val appDirectory = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + applicationInfo.packageName)
        val logDirectory = File(appDirectory.absolutePath + File.separator + "log")
        val logFile = File(logDirectory, "logcat" + System.currentTimeMillis() + ".txt")

        if (BuildConfig.DEBUG)
            LogcatToFile.init(this, logFile, Parameter("*", Priority.DEBUG))
    }
}