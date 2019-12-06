package com.mo2a.example.charades_

import android.app.Application
import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.*
import kotlinx.coroutines.*
import java.util.*


private val CORRECT_BUZZ_PATTERN = longArrayOf(0, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 500)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 1000)
private val NO_BUZZ_PATTERN = longArrayOf(0)


class GameViewModel(
    private val database: MovieDatabaseDao,
    application: Application,
    val numRounds: Int,
    private val numTeams: Int,
    roundDur: Int
) : AndroidViewModel(application) {


    // These represent different important times
    // This is when the game is over
    val DONE = 0L
    // This is the number of milliseconds in a second
    val ONE_SECOND = 1000L
    // This is the total time of the game
    val COUNTDOWN_TIME = if (roundDur == 1) 60000L else 120000L

    //Coroutine variables
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    //movie variables
    private var _movie = MutableLiveData<Movie>()
    private val firstMovie = database.get(Random().nextInt(1721))

    val commonMovieChannel = MediatorLiveData<Movie>()

    //timer variables
    private val timer: CountDownTimer
    private val _currentTime = MutableLiveData<Long>()
    private val currentTime: LiveData<Long>
        get() = _currentTime
    val currentTimeString: LiveData<String> = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time)
    }

    //game over
    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish

    //keep track of rounds
    private var roundsPlayed = 1

    //navigate back to home screen
    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean>
        get() = _navigateBack

    //navigate back to options screen
    private val _navigateToPick = MutableLiveData<Boolean>()
    val navigateToPick: LiveData<Boolean>
        get() = _navigateToPick

    //switch teams
    private val _shouldSwitchTeams = MutableLiveData<Int>()
    val shouldSwitchTeams: LiveData<Int>
        get() = _shouldSwitchTeams

    //break state
    private val _breakState = MutableLiveData<Boolean>()
    val breakState: LiveData<Boolean>
        get() = _breakState

    //ready state
    private val _readyToStart = MutableLiveData<Boolean>()
    val readyToStart: LiveData<Boolean>
        get() = _readyToStart

    //navigate to score screen
    private val _navigateToScore = MutableLiveData<Boolean>()
    val navigateToScore: LiveData<Boolean>
        get() = _navigateToScore

    //vibration variables
    private val _buzz = MutableLiveData<BuzzType>()
    val buzz: LiveData<BuzzType>
        get() = _buzz

    //team switching variable
    private var i = 1

    //team scores variables
    var teamOneScore = 0
    var teamTwoScore = 0
    var teamThreeScore = 0
    var teamFourScore = 0

    init {
        commonMovieChannel.addSource(firstMovie, commonMovieChannel::postValue)
        commonMovieChannel.addSource(_movie, commonMovieChannel::postValue)

        if (numTeams > 1) {
            switchTeams()
        }

        resetScore()

        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onFinish() {
                _currentTime.value = DONE
                _buzz.value = BuzzType.GAME_OVER
                if (isGameOver()) {
                    endGame()
                } else {
                    takeBreak()
                    changeMovie()
                    switchTeams()
                    roundsPlayed++

                }

            }

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = millisUntilFinished / ONE_SECOND
                if ((millisUntilFinished / ONE_SECOND) == 10L) {
                    _buzz.value = BuzzType.COUNTDOWN_PANIC
                }
            }
        }
    }

    fun startTimer() {
        timer.start()
    }

    fun stopTimer() {
        timer.cancel()
    }

    fun gotItClicked() {
        _buzz.value = BuzzType.CORRECT
        if (isGameOver()) {
            raiseScore(i - 1)
            endGame()
        } else {
            takeBreak()
            raiseScore(i - 1)
            changeMovie()
            switchTeams()
            roundsPlayed++
        }

    }

    private fun isGameOver(): Boolean {
        return roundsPlayed >= (numRounds * numTeams)
    }

    private fun endGame() {
        setScores()
        _eventGameFinish.value = true
        timer.cancel()
    }

    fun changeMovie() {
        uiScope.launch {
            _movie.value = getMovieFromDatabase()

        }
    }

    private suspend fun getMovieFromDatabase(): Movie {
        return withContext(Dispatchers.IO) {
            val movie = database.getMovie(Random().nextInt(1721))

            movie
        }

    }

    private fun switchTeams() {
        if (numTeams > 1) {
            if (i <= numTeams) {
                _shouldSwitchTeams.value = i
                i++
            } else {
                i = 1
                _shouldSwitchTeams.value = i
                i++

            }
        }
    }

    fun takeBreak() {
        _breakState.value = true
    }


    fun nextButtonClicked() {
        notReady()
        _breakState.value = false

    }


    fun quitGame() {
        _navigateBack.value = true
    }

    fun doneNavigatingBack() {
        _navigateBack.value = false
    }

    fun cancelQuit() {
        //empty func
    }

    fun navigateToPick() {
        _navigateToPick.value = true
    }

    fun doneNavigatingToPick() {
        _navigateToPick.value = false
    }

    fun navigateToScore() {
        _navigateToScore.value = true
    }

    fun doneNavigatingToScore() {
        _navigateToScore.value = false
    }

    fun ready() {
        _readyToStart.value = true
    }

    private fun notReady() {
        _readyToStart.value = false
    }

    enum class TeamClass(
        val id: Int,
        var score: Int
    ) {
        TEAMONE(1, 0),
        TEAMTWO(2, 0),
        TEAMTHREE(3, 0),
        TEAMFOUR(4, 0),
        TEAMNONE(5, 0)
    }

    private fun getTeam(teamId: Int): TeamClass {
        return when (teamId) {
            1 -> TeamClass.TEAMONE
            2 -> TeamClass.TEAMTWO
            3 -> TeamClass.TEAMTHREE
            4 -> TeamClass.TEAMFOUR
            else -> TeamClass.TEAMNONE
        }
    }

    private fun raiseScore(teamId: Int) {
        val team = getTeam(teamId)
        team.score++
    }

    private fun resetScore() {
        TeamClass.TEAMONE.score = 0
        TeamClass.TEAMTWO.score = 0
        TeamClass.TEAMTHREE.score = 0
        TeamClass.TEAMFOUR.score = 0
    }

    private fun setScores() {
        teamOneScore = TeamClass.TEAMONE.score
        teamTwoScore = TeamClass.TEAMTWO.score
        teamThreeScore = TeamClass.TEAMTHREE.score
        teamFourScore = TeamClass.TEAMFOUR.score
    }

    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

    fun onBuzzComplete() {
        _buzz.value = BuzzType.NO_BUZZ
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }


}