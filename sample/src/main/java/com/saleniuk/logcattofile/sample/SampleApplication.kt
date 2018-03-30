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

        if (BuildConfig.DEBUG)
            LogcatToFile.init(this, logDirectory, Parameter("*", Priority.DEBUG))
    }
}