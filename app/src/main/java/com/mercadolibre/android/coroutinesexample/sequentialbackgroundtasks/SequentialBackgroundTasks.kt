package com.mercadolibre.android.coroutinesexample.sequentialbackgroundtasks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mercadolibre.android.coroutinesexample.databinding.SequentialBackgroundTasksBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

class SequentialBackgroundTasks: AppCompatActivity() {

    private lateinit var binding: SequentialBackgroundTasksBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SequentialBackgroundTasksBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.button.setOnClickListener {
            setNewText("Click!")

            /*CoroutineScope(IO).launch {
                fakeApiRequest()
            }*/

            fakeApiRequest()

        }

    }

    private fun setNewText(input: String){
        val newText = binding.text.text.toString() + "\n$input"
        binding.text.text = newText
    }
    private suspend fun setTextOnMainThread(input: String) {
        withContext (Main) {
            setNewText(input)
        }
    }


    /**
     * Comparison between async/await and job/launch patterns.
     * Major difference is async/await can return a value wrapped in a Deferred type.
     */
    private fun fakeApiRequest() {

        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {

                // Async/Await returning a value
                val result1 = async {
                    println("debug: launching job1: ${Thread.currentThread().name}")
                    getResult1FromApi()
                }.await()

                val result2 = async {
                    println("debug: launching job2: ${Thread.currentThread().name}")
                    getResult2FromApi(result1)
                }.await()

                println("Got result2: $result2")

            }
            println("debug: job1 and job2 are complete. It took ${executionTime} ms")
        }
    }

    private suspend fun getResult1FromApi(): String {
        delay(1000)
        return "Result #1"
    }

    private suspend fun getResult2FromApi(result1: String): String {
        delay(1500)
        return "Result #2"
    }
}