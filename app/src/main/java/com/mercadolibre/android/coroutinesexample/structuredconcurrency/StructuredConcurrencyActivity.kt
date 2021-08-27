package com.mercadolibre.android.coroutinesexample.structuredconcurrency

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mercadolibre.android.coroutinesexample.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

@InternalCoroutinesApi
class StructuredConcurrencyActivity: AppCompatActivity() {
    private val TAG: String = "AppDebug"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main()
    }

    val handler = CoroutineExceptionHandler { _, exception ->
        println("Exception thrown in one of the children: $exception")
    }

    fun main(){
        val parentJob = CoroutineScope(IO).launch(handler) {

            //Si estas dentro de un CoroutineScope, para usar un supervisorJob podes hacer:
            /*supervisorScope {
                // --------- JOB A ---------
                val jobA = launch {
                    val resultA = getResult(1)
                    println("resultA: ${resultA}")
                }
                jobA.invokeOnCompletion { throwable ->
                    if(throwable != null){
                        println("Error getting resultA: ${throwable}")
                    }
                }

                // --------- JOB B ---------
                val jobB = launch {
                    val resultB = getResult(2)
                    println("resultB: ${resultB}")
                }
                jobB.invokeOnCompletion { throwable ->
                    if(throwable != null){
                        println("Error getting resultB: ${throwable}")
                    }
                }

                // --------- JOB C ---------
                val jobC = launch {
                    val resultC = getResult(3)
                    println("resultC: ${resultC}")
                }
                jobC.invokeOnCompletion { throwable ->
                    if(throwable != null){
                        println("Error getting resultC: ${throwable}")
                    }
                }
            }*/

            // --------- JOB A ---------
            val jobA = launch {
                val resultA = getResult(1)
                println("resultA: ${resultA}")
            }
            jobA.invokeOnCompletion { throwable ->
                if(throwable != null){
                    println("Error getting resultA: ${throwable}")
                }
            }

            // --------- JOB B ---------
            val jobB = launch {
                val resultB = getResult(2)
                println("debug: resultB: ${resultB}")
            }
            jobB.invokeOnCompletion { throwable ->
                if(throwable != null){
                    println("debug: Error getting resultB: ${throwable}")
                }
            }

            // --------- JOB C ---------
            val jobC = launch {
                val resultC = getResult(3)
                println("debug: resultC: ${resultC}")
            }
            jobC.invokeOnCompletion { throwable ->
                if(throwable != null){
                    println("debug: Error getting resultC: ${throwable}")
                }
            }
        }
        parentJob.invokeOnCompletion { throwable ->
            if(throwable != null){
                println("debug: Parent job failed: ${throwable}")
            }
            else{
                println("debug: Parent job SUCCESS")
            }
        }
    }

    suspend fun getResult(number: Int): Int{
        delay(number*500L)
        if(number == 2){
          throw Exception("Error getting result for number: ${number}")
//          cancel(CancellationException("Error getting result for number: ${number}"))
//          throw CancellationException("Error getting result for number: ${number}") // no propaga al padre
        }
        return number*2

    }


    private fun println(message: String){
        Log.d(TAG, message)
    }


}