package com.example.myce.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalendarViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply { //내부적으로 수정 가능한 변수
        value = "This is Calendar Screen"
    }
    val text: LiveData<String> = _text //외부에서 확인 할 수 있는 테그트 객체로 변환
}