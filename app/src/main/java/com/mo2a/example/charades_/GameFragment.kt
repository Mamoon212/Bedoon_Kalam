package com.mo2a.example.charades_


import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.mo2a.example.charades_.databinding.FragmentGameBinding

/**
 * A simple [Fragment] subclass.
 */
class GameFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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

        binding.gameViewModel = gameViewModel


        //ready to start
        gameViewModel.readyToStart.observe(this, Observer {
            if(it){
                binding.timerText.visibility= View.VISIBLE
                binding.doneButton.visibility= View.VISIBLE
                binding.readyButton.visibility= View.GONE
                gameViewModel.startTimer()
            }else{
                binding.timerText.visibility= View.GONE
                binding.doneButton.visibility= View.GONE
                binding.readyButton.visibility= View.VISIBLE
                gameViewModel.stopTimer()
            }
        })

        //set movie text
        gameViewModel.commonMovieChannel.observe(this, Observer {
            it?.let {
                binding.movieText.text = it.title
            }
        })

        //set timer text
        gameViewModel.currentTimeString.observe(this, Observer {
            binding.timerText.text = it
        })

//        //round finished
//        gameViewModel.eventRoundFinish.observe(this, Observer {
//            if (it) {
//                binding.movieText.text = getString(R.string.time_up)
//                binding.doneButton.text = getString(R.string.try_again)
//            }
//        })

        //game over
        gameViewModel.eventGameFinish.observe(this, Observer {
            if (it) {
                binding.doneButton.visibility = View.GONE
                binding.readyButton.visibility = View.GONE
                binding.teamText.visibility = View.GONE
                if(args.numTeams >1){
                    binding.scoreButton.visibility = View.VISIBLE
                }else{
                    binding.singleModePlayAgain.visibility= View.VISIBLE
                }
                binding.movieText.text = getString(R.string.game_over)
            }
        })

        gameViewModel.navigateToPick.observe(this, Observer {
            if(it){
                this.findNavController().navigate(GameFragmentDirections.actionGameFragmentToPickModeFragment())
                gameViewModel.doneNavigatingToPick()
            }
        })

        //handle back button
        requireActivity().onBackPressedDispatcher.addCallback(this,object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                        val alertDialog = activity?.let {
                            val builder = AlertDialog.Builder(it)
                            builder.apply {
                                setPositiveButton("Yes") { _, _ ->
                                    gameViewModel.quitGame()
                                }
                                setNegativeButton("No") { _, _ ->
                                    gameViewModel.cancelQuit()
                                }
                            }
                                .setMessage("Are you sure you want to quit the game?")

                            builder.create()
                        }
                        alertDialog?.show()
                    }
        })

        //navigate back
        gameViewModel.navigateBack.observe(this, Observer {
            if(it){
                this.findNavController().navigate(GameFragmentDirections.actionGameFragmentToHomeFragment())
                gameViewModel.doneNavigatingBack()
            }
        })

//        //set team text
//        gameViewModel.teamMode.observe(this, Observer {
//            if(it > 1){
//                binding.teamText.text= getString(R.string.team_one)
//            }
//        })

        //switch team text
        gameViewModel.shouldSwitchTeams.observe(this, Observer {
            Log.i("3aww", "$it" )
            when (it) {
                1 -> binding.teamText.text= getString(R.string.team_one)
                2 -> binding.teamText.text= getString(R.string.team_two)
                3 -> binding.teamText.text= getString(R.string.team_three)
                4 -> binding.teamText.text= getString(R.string.team_four)
            }
        })

        //vibrate
        gameViewModel.buzz.observe(this, Observer {
            if(it != GameViewModel.BuzzType.NO_BUZZ){
                buzz(it.pattern)
                gameViewModel.onBuzzComplete()
            }
        })

        //navigate to score screen
        gameViewModel.navigateToScore.observe(this, Observer {
            if(it){
                this.findNavController().navigate(GameFragmentDirections.actionGameFragmentToScoreFragment(
                    args.numTeams,
                    args.numRounds,
                    gameViewModel.teamOneScore,
                    gameViewModel.teamTwoScore,
                    gameViewModel.teamThreeScore,
                    gameViewModel.teamFourScore
                    ))
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


}
