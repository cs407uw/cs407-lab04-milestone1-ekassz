package com.cs407.lab4_milestone1


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val TAG = "MyActivity"
    private var job : Job? = null
    private var isPaused = false
    private lateinit var progressText: TextView
    private lateinit var pauseResumeTog: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val startButton = findViewById<Button>(R.id.start)
        val stopButton = findViewById<Button>(R.id.stop)
        pauseResumeTog = findViewById<Switch>(R.id.toggle)
        progressText = findViewById(R.id.progressText)

        progressText.visibility = View.GONE

        startButton.setOnClickListener {
            if(job == null || job?.isCompleted == true) {
                startButton.text = getString(R.string.download)
                startDownload(it)
            }
        }

        stopButton.setOnClickListener{
            stopDownload(it)
        }

        pauseResumeTog.setOnCheckedChangeListener{_, isChecked ->
            isPaused = isChecked
        }

    }

    private suspend fun mockFileDownLoader(){
        val startButton = findViewById<Button>(R.id.start)

        withContext(Dispatchers.Main){
            progressText.visibility = View.VISIBLE
            startButton.text = getString(R.string.download)
        }

        for(downloadProgress in 0..100 step 10){
            if(job?.isCancelled == true) return

            while (isPaused){
                delay(100)
            }
            delay(1000)

            withContext(Dispatchers.Main){
                Log.d(TAG, "Download Progress ${downloadProgress}%")
                progressText.text = "Download Progress $downloadProgress%"
            }

        }

        withContext(Dispatchers.Main){
            startButton.text = getString(R.string.start)
            progressText.text = "Download Complete"

        }
    }

    fun startDownload(view: View){
        job = CoroutineScope(Dispatchers.Default).launch {
            mockFileDownLoader()
        }

    }

    fun stopDownload(view:View){
        val startButton = findViewById<Button>(R.id.start)
        job?.cancel()
        job = null
        startButton.text = getString(R.string.start)
        progressText.text = getString(R.string.progressTextCancel)

    }

}
