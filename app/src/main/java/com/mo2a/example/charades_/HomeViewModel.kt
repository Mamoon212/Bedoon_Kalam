package com.mo2a.example.charades_

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _navigateToPick = MutableLiveData<Boolean>()
    val navigateToPick: LiveData<Boolean>
        get() = _navigateToPick

    private val _showAboutDlaog = MutableLiveData<Boolean>()
    val showAboutDialog: LiveData<Boolean>
        get() = _showAboutDlaog


    fun play(){
        _navigateToPick.value= true
    }

    fun about(){
        _showAboutDlaog.value= true
    }

    fun doneNavigating(){
        _navigateToPick.value= false
    }

    fun doneShowing(){
        _showAboutDlaog.value= false
    }

}