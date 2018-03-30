package com.saleniuk.logcattofile.sample

import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.saleniuk.logcattofile.LogcatToFile
import com.saleniuk.logcattofile.Parameter
import com.saleniuk.logcattofile.Priority

import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    var handler = Handler()
    val runnable = Runnable { log() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val appDirectory = File(Environment.getExternalStorageDirectory().absolutePath + "/MyPersonalAppFolder")
        val logDirectory = File(appDirectory.absolutePath + "/log")
        val logFile = File(logDirectory, "logcat" + System.currentTimeMillis() + ".txt")

        LogcatToFile.init(this, logFile, Parameter("*", Priority.DEBUG))

        handler.postDelayed(runnable, 500)
    }

    private fun log() {
        Log.d("TEST", "test")
        handler.postDelayed(runnable, 500)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
