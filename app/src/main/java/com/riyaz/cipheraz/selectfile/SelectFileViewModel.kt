package com.riyaz.cipheraz.selectfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelectFileViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private val _eventOnFileSelected = MutableLiveData<Boolean?>(null)
    val eventOnFileSelected: LiveData<Boolean?> get() = _eventOnFileSelected

    fun onChooseFileButtonClicked(){

    }

    fun onFileSelected(){
        _eventOnFileSelected.value = true
    }

    fun onNavigated(){
        _eventOnFileSelected.value = null
    }
}