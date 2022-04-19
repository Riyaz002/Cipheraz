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

    private val _filePath = MutableLiveData<String>("");
    val filePath: LiveData<String> get() = _filePath

    private val _initialUri = MutableLiveData<String>()
    val  initialUri: LiveData<String> get() = _initialUri

    private val _crypt = MutableLiveData<Boolean>()
    val crypt: LiveData<Boolean> get() = _crypt

    private val _mode = MutableLiveData<Mode>()
    val mode: LiveData<Mode> get() = _mode

//    private val _nameOfOutputFile = MutableLiveData<String>()
//    val nameOfOutputFile: LiveData<String> get() = _nameOfOutputFile

//    private val _key = MutableLiveData<String>(null)
//    val key = MutableLiveData<String>()

    private var _eventCreateDocument = MutableLiveData<Boolean?>(null)
    val eventCreateDocument: LiveData<Boolean?> get() = _eventCreateDocument

    private val _type = MutableLiveData<String>(null)
    val type: LiveData<String> get() = _type

    val job = Job()

    val viewModelScope = CoroutineScope(Dispatchers.Main + job)

    init {
        _filePath.value = uri
        _type.value = type
        getInitialUri(uri)
    }

    private fun getInitialUri(uri: String): String {
        var i:Int = 0
        var j = 0
        while(i < uri.length){
            if(uri[i] == '/') j = i
        }
        return uri.substring(0, j)
    }

    fun crypt(){
        _eventCreateDocument.value = true
    }

//    fun setKey(key: String){
//        this.key.value = key
//    }
//
//    fun setNameOfOutputFile(name: String) {
//        _nameOfOutputFile.value = name
//    }

//    private suspend fun doCryption() {
//        CipherUtils.encrypt(key.value.toString(), File(filePath.value), )
//    }

    fun onGoClicked(){
        crypt()
    }

    fun startCryption() {
//        viewModelScope.launch {
//            doCryption()
    //            }
        _crypt.value = true
   }

    fun doneCryption() {
        _crypt.value = false
    }

    fun setMode(mode: Mode) {
        _mode.value = mode
    }

    fun setType(uri: String) {
        _type.value = uri.substring(uri.length-3, uri.length)
    }
    fun initialUri(uri: String){
        _initialUri.value = getInitialUri(uri)
    }
}