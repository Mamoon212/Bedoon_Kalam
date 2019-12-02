package com.mo2a.example.charades_

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class PickModeViewModel(application: Application) : AndroidViewModel(application) {


    private val _team = MutableLiveData<Boolean>()
    val team: LiveData<Boolean>
        get() = _team

    private val _navigateToGameFrag = MutableLiveData<Boolean>()
    val navigateToGameFrag: LiveData<Boolean>
        get() = _navigateToGameFrag

    var duration: Int = 0
    var numTeams: Int = 0
    var numRounds: Int = 0

    fun onTeamModeSelected() {
        _team.value = true
    }

    fun onSingleModeSelected() {
        _team.value = false
        numTeams= 1
    }

    fun setDurOne() {
        duration = 1
    }

    fun setDurTwo() {
        duration = 2
    }

    fun setTeamTwo() {
        numTeams = 2
    }

    fun setTeamThree() {
        numTeams = 3
    }

    fun setTeamFour() {
        numTeams = 4
    }

    fun setRoundThree() {
        numRounds = 3
    }

    fun setRoundFive() {
        numRounds = 5
    }

    fun setRoundTen() {
        numRounds = 10
    }

    fun navigate() {
        _navigateToGameFrag.value = true
    }

    fun doneNavigating() {
        _navigateToGameFrag.value = false
    }
}