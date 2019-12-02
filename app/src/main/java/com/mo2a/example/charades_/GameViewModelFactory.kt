package com.mo2a.example.charades_

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameViewModelFactory(
    private val dataSource: MovieDatabaseDao,
    private val application: Application,
    private val numRounds: Int,
    private val numTeams: Int,
    private val roundDur: Int
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(dataSource, application, numRounds, numTeams, roundDur) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

