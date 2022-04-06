package com.riyaz.cipheraz.cipher

import androidx.lifecycle.*
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

    private val outputFilePath = MutableLiveData<String>(null)

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

    fun setOutputFilePath(path: String){
        outputFilePath.value = path
    }

    fun setNameOfOutputFile(name: String) {
        _nameOfOutputFile.value = name
    }

    val outputFile: LiveData<String> = Transformations.map(outputFilePath.combine(nameOfOutputFile)){
        it.first + it.second
    }

    private suspend fun doCryption() {
        CipherUtils.encrypt(key.value.toString(), File(filePath.value), File(outputFile.value.toString()))
    }

    fun getExtention(): String{
        var i = path.length
        return path.substring(i-4, i)
    }

    fun getOutputFilePath(): String{
        val s = StringBuilder(filePath.value)
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
class PairLiveData<A, B>(first: LiveData<A>, second: LiveData<B>) : MediatorLiveData<Pair<A?, B?>>() {
    init {
        addSource(first) { value = it to second.value }
        addSource(second) { value = first.value to it }
    }
}

fun <A, B> LiveData<A>.combine(other: LiveData<B>): PairLiveData<A, B> {
    return PairLiveData(this, other)
}