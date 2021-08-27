package com.mercadolibre.android.coroutinesexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.mercadolibre.android.coroutinesexample.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // EJEMPLO BASICO -----------------------------------------------------//
        //setBasicExample()
        //---------------------------------------------------------------------//

        // EJEMPLO CON VIEWMODEL Y TIMEOUT-------------------------------------//
        setVMexampleWithTimeout()
        //---------------------------------------------------------------------//
    }

    private fun setVMexampleWithTimeout() {
        viewModel.result.observe(this, {
            if (it != null) {
                setNewText(it)
                /*withContext(Main) {
                    setTextOnMainThread("Got $it")
                }*/
            }
        })

        binding.button.setOnClickListener {
            setNewText("Click!")

            viewModel.launchDataLoadWithTimeout()
        }
    }

    private fun setBasicExample() {
        binding.button.setOnClickListener {
            setNewText("Click!")

            //fakeApiRequest() // Da error porque quiero correr una funcion async fuera de una corutina
            CoroutineScope(IO).launch { //forma de organizar corutinas en grupos, por ej, para poder cancelarlos todos juntos al cancelar el scope (IO, Main, Default)
                fakeApiRequest()
            }
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

    private suspend fun fakeApiRequest() {
        logThread("fakeApiRequest")

        val result1 = getResult1FromApi()
        setTextOnMainThread("Got $result1")

        val result2 = getResult2FromApi()
        setTextOnMainThread("Got $result2")

    }


    private suspend fun getResult1FromApi(): String {
        logThread("getResult1FromApi")
        delay(1000)
        return "Result #1"
    }

    private suspend fun getResult2FromApi(): String {
        logThread("getResult2FromApi")
        delay(1000)
        return "Result #2"
    }

    private fun logThread(methodName: String){
        println("debug: ${methodName}: ${Thread.currentThread().name}")
    }
}
