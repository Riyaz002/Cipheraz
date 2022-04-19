package com.riyaz.cipheraz.cipher

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.riyaz.cipheraz.utils.Algorithm
import com.riyaz.cipheraz.utils.CipherUtils
import com.riyaz.cipheraz.utils.Mode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.lang.StringBuilder

class CipherViewModel(uri: String, type: String) : ViewModel() {

    private val _initialUri = MutableLiveData<String>()
    val  initialUri: LiveData<String> get() = _initialUri

    private val _mode = MutableLiveData<Mode>()
    val mode: LiveData<Mode> get() = _mode

    private val _fileName = MutableLiveData<String>("")
    val fileName: LiveData<String> get() = _fileName

    private var _eventCreateDocument = MutableLiveData<Boolean?>(null)
    val eventCreateDocument: LiveData<Boolean?> get() = _eventCreateDocument

    private val _type = MutableLiveData<String>(null)
    val type: LiveData<String> get() = _type

    val job = Job()

    fun setMode(mode: Mode) {
        _mode.value = mode
    }


}