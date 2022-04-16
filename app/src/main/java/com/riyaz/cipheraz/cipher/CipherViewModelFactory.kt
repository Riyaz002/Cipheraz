package com.riyaz.cipheraz.cipher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.io.IOException

class CipherViewModelFactory(val path: String, val type: String): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CipherViewModel::class.java)){
            return CipherViewModel(path, type) as T
        } else throw IOException("unidentified viewModel class")
    }
}