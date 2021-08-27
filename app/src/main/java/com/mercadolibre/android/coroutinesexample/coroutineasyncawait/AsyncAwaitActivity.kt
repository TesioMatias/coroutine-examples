package com.mercadolibre.android.coroutinesexample.coroutineasyncawait

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mercadolibre.android.coroutinesexample.databinding.AsyncAwaitActivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

class AsyncAwaitActivity: AppCompatActivity() {

    private lateinit var binding: AsyncAwaitActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AsyncAwaitActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.button.setOnClickListener {
            setNewText("Click!")

            CoroutineScope(IO).launch {
                fakeApiRequest()
            }

            //fakeApiRequestAsyncAwait()
        }

    }

    private fun fakeApiRequestAsyncAwait() {
        CoroutineScope(IO).launch {
            val time = measureTimeMillis {
                //la mayor diferencia es que con este patron (async-await) obtengo el resultado de la corutina, fuera de la corutina.
                //Fuera de eso son bastante similares con el patron job-launcher

                val result1: Deferred<String> = async {
                    println("debug: job1 in thread: ${Thread.currentThread().name}")
                    getResult1FromApi()
                }

                val result2: Deferred<String> = async {
                    println("debug: job2 in thread: ${Thread.currentThread().name}")
                    getResult2FromApi()
                }

                /*var result = ""
                val job = launch {
                    result = getResult1FromApi()
                }
                job.join()
                println(result)*/

                setTextOnMainThread("Got ${result1.await()}")
                setTextOnMainThread("Got ${result2.await()}")

            }

            println("debug: tiempo total $time ms.")

        }
    }

    private fun setNewText(input: String){
        val newText = binding.text.text.toString() + "\n$input"
        binding.text.text = newText
    }
    private suspend fun setTextOnMainThread(input: String) {
        withContext(Main) {
            setNewText(input)
        }
    }

    /**
     * Job1 and Job2 run in parallel as different coroutines
     * Also see "Deferred, Async, Await" branch for parallel execution
     */
    private suspend fun fakeApiRequest() {
        withContext(IO) {

            val time3 = measureTimeMillis {

                val job1 = launch {
                    val time1 = measureTimeMillis {
                        println("debug: job1 in thread: ${Thread.currentThread().name}")
                        val result1 = getResult1FromApi()
                        setTextOnMainThread("Got $result1")
                    }
                    println("debug: job1 compeleted in $time1 ms.")
                }

                //esto haria que se espere a que termine el job 1 y despues de ejecute el job 2
                //job1.join()

                val job2 = launch {
                    val time2 = measureTimeMillis {
                        println("debug: job2 in thread: ${Thread.currentThread().name}")
                        val result2 = getResult2FromApi()
                        setTextOnMainThread("Got $result2")
                    }
                    println("debug: job2 compeleted in $time2 ms.")
                }

                //job2.join()
            }

            println("debug: job2 and job 1 compeleted in $time3 ms.")

        }
    }

    private suspend fun getResult1FromApi(): String {
        delay(1000)
        return "Result #1"
    }

    private suspend fun getResult2FromApi(): String {
        delay(1500)
        return "Result #2"
    }
}