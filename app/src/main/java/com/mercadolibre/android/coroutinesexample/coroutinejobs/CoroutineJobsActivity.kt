package com.mercadolibre.android.coroutinesexample.coroutinejobs

import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mercadolibre.android.coroutinesexample.databinding.CoroutineJobsActivityBinding
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CoroutineJobsActivity: AppCompatActivity() {

    private val TAG: String = "AppDebug"

    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 4000 // ms
    private lateinit var job: CompletableJob
    private lateinit var binding: CoroutineJobsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CoroutineJobsActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.jobButton.setOnClickListener {
            //Como hacer el "null check" de vars lateinit
            if(!::job.isInitialized){
                initjob()
            }
            binding.jobProgressBar.startJobOrCancel(job)
        }
    }

    fun resetjob(){
        if(job.isActive || job.isCompleted){
            job.cancel(CancellationException("Resetting job"))
        }
        initjob()
    }

    fun initjob(){
        binding.jobButton.setText("Start Job #1")
        updateJobCompleteTextView("")


        // si se cancela un job, no puede reutilizarse, por eos se crea uno nuevo en cada init
        job = Job()
        job.invokeOnCompletion {
            it?.message.let{
                var msg = it
                if(msg.isNullOrBlank()){
                    msg = "Unknown cancellation error."
                }
                Log.e(TAG, "${job} was cancelled. Reason: ${msg}")
                showToast(msg)
            }
        }


        binding.jobProgressBar.max = PROGRESS_MAX
        binding.jobProgressBar.progress = PROGRESS_START
    }

    // Algo de extension functions
    fun ProgressBar.startJobOrCancel(job: Job){
        if(this.progress > 0){
            Log.d(TAG, "${job} is already active. Cancelling...")
            resetjob()
        }
        else{
            binding.jobButton.setText("Cancel Job #1")

            //esto se hace para individualizar scopes, para que al cancelar un scope se cancele solo lo que quiero cancelar
            CoroutineScope(IO + job).launch{
                Log.d(TAG, "coroutine ${this} is activated with job ${job}.")

                for(i in PROGRESS_START..PROGRESS_MAX){
                    delay((JOB_TIME / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
                updateJobCompleteTextView("Job is complete!")
            }
        }
    }

    private fun updateJobCompleteTextView(text: String){
        // Ejemplo de global scope
        GlobalScope.launch (Main){
            binding.jobCompleteText.setText(text)
        }
    }

    private fun showToast(text: String){
        // Ejemplo de global scope
        GlobalScope.launch (Main){
            Toast.makeText(this@CoroutineJobsActivity, text, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}