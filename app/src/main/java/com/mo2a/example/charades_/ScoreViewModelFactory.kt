package com.mo2a.example.charades_


import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ScoreViewModelFactory(
    private val application: Application,
    private val numRounds: Int,
    private val numTeams: Int,
    private val teamOneScore: Int,
    private val teamTwoScore: Int,
    private val teamThreeScore: Int,
    private val teamFourScore: Int
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScoreViewModel::class.java)) {
            return ScoreViewModel(application, numRounds, numTeams, teamOneScore, teamTwoScore, teamThreeScore, teamFourScore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

