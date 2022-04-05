package com.riyaz.cipheraz.selectfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelectFileViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private val _path: MutableLiveData<String?> = MutableLiveData(null)
    val path: LiveData<String?> get() = _path

    private val _eventOnFileSelected = MutableLiveData<Boolean?>(null)
    val eventOnFileSelected: LiveData<Boolean?> get() = _eventOnFileSelected

    fun onChooseFileButtonClicked(){

    }

    fun fetchPath(path: String){
        _path.value = path
    }

    fun filePathInvalidEvent(){
        _eventOnFileSelected.value = false
        _path.value = null
    }

    fun fileSelected(){
        _eventOnFileSelected.value = true
    }

    fun onNavigated(){
        _eventOnFileSelected.value = null
    }
}