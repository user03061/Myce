package com.example.myce.ui.myplace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyplaceViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "내 플레이스"
    }
    val text: LiveData<String> = _text
}