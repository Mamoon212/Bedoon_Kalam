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
        _scoreOneLive.value = "فريق 1 جاب: \n$numRounds/$teamOneScore"
        _scoreTwoLive.value = "فريق 2 جاب: \n$numRounds/$teamTwoScore"
        _scoreThreeLive.value = "فريق 3 جاب: \n$numRounds/$teamThreeScore"
        _scoreFourLive.value = "فريق 4 جاب: \n$numRounds/$teamFourScore"
    }

    private fun decideNumTexts() {
        when (numTeams) {
            3 -> _showThreeScores.value = true
            4 -> _showFourScores.value = true
        }
    }

    private fun decideWinner() {
        if (teamOneScore > teamTwoScore && teamOneScore > teamThreeScore && teamOneScore > teamFourScore) {
            _winner.value = "فريق 1"
        } else if (teamTwoScore > teamOneScore && teamTwoScore > teamThreeScore && teamTwoScore > teamFourScore) {
            _winner.value = "فريق 2"
        } else if (teamThreeScore > teamOneScore && teamThreeScore > teamTwoScore && teamThreeScore > teamFourScore) {
            _winner.value = "فريق 3"
        } else if (teamFourScore > teamOneScore && teamFourScore > teamTwoScore && teamFourScore > teamThreeScore){
            _winner.value= "فريق 4"
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