package com.mo2a.example.charades_


import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.mo2a.example.charades_.databinding.FragmentGameBinding

/**
 * A simple [Fragment] subclass.
 */
class GameFragment : Fragment() {

    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val prefs = activity!!.getSharedPreferences("MyPrefs", 0)
        val isPaid: Boolean = prefs.getBoolean("paid", false)

        mInterstitialAd = InterstitialAd(this.context)
        mInterstitialAd.adUnitId = "ca-app-pub-6956387476413004/6523595809"
        if (!isPaid) {
            mInterstitialAd.loadAd(AdRequest.Builder().build())
        }


        val args = GameFragmentArgs.fromBundle(arguments!!)


        // Inflate the layout for this fragment
        val binding: FragmentGameBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_game, container, false
        )
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        val dao = MovieDatabase.getInstance(application).movieDatabaseDao
        val gameViewModelFactory =
            GameViewModelFactory(dao, application, args.numRounds, args.numTeams, args.roundDur)
        val gameViewModel =
            ViewModelProviders.of(this, gameViewModelFactory).get(GameViewModel::class.java)

        createChannel(
            getString(R.string.app_notification_channel_id),
            getString(R.string.app_notification_channel_id)
        )
        binding.gameViewModel = gameViewModel


        //ready to start
        gameViewModel.readyToStart.observe(this, Observer {
            if (it) {
                binding.timerText.visibility = View.VISIBLE
                binding.doneButton.visibility = View.VISIBLE
                binding.readyButton.visibility = View.GONE
                binding.gameBackground.background =
                    resources.getDrawable(R.mipmap.aflam_app_material_06_640x960)
                gameViewModel.startTimer()
            } else {
                binding.timerText.visibility = View.GONE
                binding.doneButton.visibility = View.GONE
                binding.readyButton.visibility = View.VISIBLE
                binding.gameBackground.background =
                    resources.getDrawable(R.mipmap.aflam_app_material_07_640x960)
                gameViewModel.stopTimer()
            }
        })

        //take a break
        gameViewModel.breakState.observe(this, Observer {
            if (it) {
                binding.timerText.visibility = View.GONE
                binding.movieText.visibility = View.GONE
                binding.doneButton.visibility = View.GONE
                binding.readyButton.visibility = View.GONE
                binding.teamText.visibility = View.GONE
                binding.nextButton.visibility = View.VISIBLE
                binding.gameBackground.background =
                    resources.getDrawable(R.mipmap.aflam_app_material_07_640x960)
            } else {
                binding.nextButton.visibility = View.GONE
                binding.teamText.visibility = View.VISIBLE
                binding.movieText.visibility = View.VISIBLE
                binding.gameBackground.background =
                    resources.getDrawable(R.mipmap.aflam_app_material_06_640x960)

            }
        })

        //set movie text
        gameViewModel.commonMovieChannel.observe(this, Observer {
            it?.let {
                binding.movieText.text = it.title
                binding.readyButton.isEnabled = true
            }
        })

        //set timer text
        gameViewModel.currentTimeString.observe(this, Observer {
            binding.timerText.text = it
        })


        //game over
        gameViewModel.eventGameFinish.observe(this, Observer {
            if (it) {
                binding.doneButton.visibility = View.GONE
                binding.readyButton.visibility = View.GONE
                binding.teamText.visibility = View.GONE
                binding.timerText.visibility = View.INVISIBLE
                binding.gameBackground.background =
                    resources.getDrawable(R.mipmap.aflam_app_material_07_640x960)
                if (args.numTeams > 1) {
                    binding.scoreButton.visibility = View.VISIBLE
                } else {
                    binding.singleModePlayAgain.visibility = View.VISIBLE
                }
                binding.movieText.text = getString(R.string.game_over)
                if (!isPaid) {
                    if (mInterstitialAd.isLoaded) {
                        mInterstitialAd.show()
                    } else {
                        Log.d("TAG", "The interstitial wasn't loaded yet.")
                    }
                }

            }
        })

        gameViewModel.navigateToPick.observe(this, Observer {
            if (it) {
                this.findNavController()
                    .navigate(GameFragmentDirections.actionGameFragmentToPickModeFragment())
                gameViewModel.doneNavigatingToPick()
            }
        })

        //handle back button
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val alertDialog = activity?.let {
                        val builder = AlertDialog.Builder(it)
                        builder.apply {
                            setMessage("Are you sure you want to quit the game?")
                            setPositiveButton("Yes") { _, _ ->
                                gameViewModel.quitGame()
                            }
                            setNegativeButton("No") { _, _ ->
                                gameViewModel.cancelQuit()
                            }
                        }
                        builder.create()
                    }
                    alertDialog?.show()
                }
            }
        )

        //navigate back
        gameViewModel.navigateBack.observe(this, Observer {
            if (it) {
                this.findNavController()
                    .navigate(GameFragmentDirections.actionGameFragmentToHomeFragment())
                gameViewModel.doneNavigatingBack()
            }
        })


        //switch team text
        gameViewModel.shouldSwitchTeams.observe(this, Observer {
            when (it) {
                1 -> binding.teamText.text = getString(R.string.team_one)
                2 -> binding.teamText.text = getString(R.string.team_two)
                3 -> binding.teamText.text = getString(R.string.team_three)
                4 -> binding.teamText.text = getString(R.string.team_four)
            }
        })

        //vibrate
        gameViewModel.buzz.observe(this, Observer {
            if (it != GameViewModel.BuzzType.NO_BUZZ) {
                buzz(it.pattern)
                gameViewModel.onBuzzComplete()
            }
        })

        //navigate to score screen
        gameViewModel.navigateToScore.observe(this, Observer {
            if (it) {
                this.findNavController().navigate(
                    GameFragmentDirections.actionGameFragmentToScoreFragment(
                        args.numTeams,
                        args.numRounds,
                        gameViewModel.teamOneScore,
                        gameViewModel.teamTwoScore,
                        gameViewModel.teamThreeScore,
                        gameViewModel.teamFourScore
                    )
                )
                gameViewModel.doneNavigatingToScore()
            }
        })

        return binding.root
    }

    private fun buzz(pattern: LongArray) {
        val buzzer = activity?.getSystemService<Vibrator>()

        buzzer.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buzzer?.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                //deprecated in API 26
                buzzer?.vibrate(pattern, -1)
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description =
                getString(R.string.time_up)
            Log.i("haha", notificationChannel.sound.toString())
            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(notificationChannel)

        }
    }
}
