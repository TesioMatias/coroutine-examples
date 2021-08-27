package com.mercadolibre.android.coroutinesexample

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class ActivityViewModel: ViewModel() {

    val TIMEOUT_TIME = 1900L
    var result = MutableLiveData<String?>()

    fun launchDataLoadWithTimeout() {
        viewModelScope.launch(IO) {
            val job = withTimeoutOrNull(TIMEOUT_TIME) {
                updateTextOnMainThread("result1")
                updateTextOnMainThread("result2")
            }

            if (job == null) {
                updateTextOnMainThread("timeouted")
            }
        }
    }

    private suspend fun updateTextOnMainThread(text: String) {
        withContext(Main){
            delay(1000)
            result.value = text
        }
    }
}