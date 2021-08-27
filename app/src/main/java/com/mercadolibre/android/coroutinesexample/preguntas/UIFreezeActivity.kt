package com.mercadolibre.android.coroutinesexample.preguntas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mercadolibre.android.coroutinesexample.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UIFreezeActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        main()

        binding.button.setOnClickListener {
            binding.text.text = ((count++).toString())
        }
    }

    private fun main() {
        CoroutineScope(Main).launch {
            println("Current thread: ${Thread.currentThread().name}")
            delay(3000)
            //Thread.sleep(3000)
        }
    }
}