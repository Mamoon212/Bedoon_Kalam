package com.mo2a.example.charades_

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ScoreViewModel(
    application: Application,
    private val numRounds: Int,
    private val numTeams: Int,
    private val teamOneScore: Int,
    private val teamTwoScore: Int,
    private val teamThreeScore: Int,
    private val teamFourScore: Int
) : AndroidViewModel(application) {

    //    private val _showTwoScores = MutableLiveData<Boolean>()
//    val showTwoScores: LiveData<Boolean>
//        get() = _showTwoScores
    private val _showThreeScores = MutableLiveData<Boolean>()
    val showThreeScores: LiveData<Boolean>
        get() = _showThreeScores
    private val _showFourScores = MutableLiveData<Boolean>()
    val showFourScores: LiveData<Boolean>
        get() = _showFourScores


    private val _scoreOneLive = MutableLiveData<String>()
    val scoreOneLive: LiveData<String>
        get() = _scoreOneLive
    private val _scoreTwoLive = MutableLiveData<String>()
    val scoreTwoLive: LiveData<String>
        get() = _scoreTwoLive
    private val _scoreThreeLive = MutableLiveData<String>()
    val scoreThreeLive: LiveData<String>
        get() = _scoreThreeLive
    private val _scoreFourLive = MutableLiveData<String>()
    val scoreFourLive: LiveData<String>
        get() = _scoreFourLive

    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean>
        get() = _navigateBack

    private val _winner = MutableLiveData<String>()
    val winner: LiveData<String>
        get() = _winner


    init {
        decideNumTexts()
        setScores()
        decideWinner()
    }

    private fun setScores() {
        _scoreOneLive.value = "Team One scored \n$teamOneScore/$numRounds"
        _scoreTwoLive.value = "Team Two scored \n$teamTwoScore/$numRounds"
        _scoreThreeLive.value = "Team Three scored \n$teamThreeScore/$numRounds"
        _scoreFourLive.value = "Team Four scored \n$teamFourScore/$numRounds"
    }

    private fun decideNumTexts() {
        when (numTeams) {
            3 -> _showThreeScores.value = true
            4 -> _showFourScores.value = true
        }
    }

    private fun decideWinner() {
        if (teamOneScore > teamTwoScore && teamOneScore > teamThreeScore && teamOneScore > teamFourScore) {
            _winner.value = "Team One"
        } else if (teamTwoScore > teamOneScore && teamTwoScore > teamThreeScore && teamTwoScore > teamFourScore) {
            _winner.value = "Team Two"
        } else if (teamThreeScore > teamOneScore && teamThreeScore > teamTwoScore && teamThreeScore > teamFourScore) {
            _winner.value = "Team Three"
        } else if (teamFourScore > teamOneScore && teamFourScore > teamTwoScore && teamFourScore > teamThreeScore){
            _winner.value= "Team Four"
        }
        else{
            _winner.value=""
        }

    }

    fun playAgain() {
        _navigateBack.value = true
    }

    fun doneNavigating() {
        _navigateBack.value = false
    }


}