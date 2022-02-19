package com.riyaz.cipheraz.cipher

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.riyaz.cipheraz.utils.Algorithm
import com.riyaz.cipheraz.utils.CipherUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.lang.StringBuilder

class CipherViewModel(val path: String) : ViewModel() {

    private val _filePath = MutableLiveData<String>("");
    val filePath: LiveData<String> get() = _filePath

    private val _crypt = MutableLiveData<Boolean>()
    val crypt: LiveData<Boolean> get() = _crypt

    private val _nameOfOutputFile = MutableLiveData<String>()
    val nameOfOutputFile: LiveData<String> get() = _nameOfOutputFile

    private val key = MutableLiveData<String>()

    var item = listOf(Algorithm.values())

    private val outputFile = Transformations.map(nameOfOutputFile){
        getOutputFilePath() + nameOfOutputFile.value + getExtention()
    }

    val job = Job()

    val viewModelScope = CoroutineScope(Dispatchers.Main + job)

    init {
        _filePath.value = path
    }

    fun crypt(){

        viewModelScope.launch {
            doCryption()
        }
    }

    fun setKey(key: String){
        this.key.value = key
    }

    fun setNameOfOutputFile(name: String) {
        _nameOfOutputFile.value = name
    }

    private suspend fun doCryption() {
        CipherUtils.encrypt(key.value.toString(), File(filePath.value), File(outputFile.value.toString()))
    }

    fun getExtention(): String{
        var i = path.length
        return path.substring(i-4, i)
    }

    fun getOutputFilePath(): String{
        val s: StringBuilder = StringBuilder(filePath.value)
        var i = s.length
        while(s.get(i) != '/'){
            i--;
        }
        s.substring(0, i+1)
        return s.toString()
    }

    fun onGoClicked(){
        _crypt.value = true
    }
}