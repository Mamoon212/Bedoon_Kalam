package com.mo2a.example.charades_


import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.mo2a.example.charades_.databinding.FragmentScoreBinding

/**
 * A simple [Fragment] subclass.
 */
class ScoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val args = ScoreFragmentArgs.fromBundle(arguments!!)
        // Inflate the layout for this fragment
        val binding: FragmentScoreBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_score, container, false
        )
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        val scoreViewModelFactory = ScoreViewModelFactory(
            application,
            args.numRounds,
            args.numTeams,
            args.teamOneScore,
            args.teamTwoScore,
            args.teamThreeScore,
            args.teamFourScore
        )
        val scoreViewModel =
            ViewModelProviders.of(this, scoreViewModelFactory).get(ScoreViewModel::class.java)

        binding.scoreViewModel= scoreViewModel
        binding.lifecycleOwner= this


        scoreViewModel.showThreeScores.observe(this, Observer {
            if(it){
                binding.scoreTeamThree.visibility= View.VISIBLE
            }
        })

        scoreViewModel.showFourScores.observe(this, Observer {
            if(it){
                binding.scoreTeamThree.visibility= View.VISIBLE
                binding.scoreTeamFour.visibility= View.VISIBLE
            }
        })

        scoreViewModel.winner.observe(this, Observer {
            if(it.isNotEmpty()){
                binding.winnerText.text= "$it has won!"
            }else{
                binding.winnerText.text= "It's a draw!"
            }
        })

        scoreViewModel.navigateBack.observe(this, Observer {
            if(it){
                this.findNavController().navigate(ScoreFragmentDirections.actionScoreFragmentToPickModeFragment())
                scoreViewModel.doneNavigating()
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val alertDialog = activity?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.apply {
                        setPositiveButton("Yes") { _, _ ->
                            activity?.finish()
                        }
                        setNegativeButton("No") { _, _ ->

                        }
                    }
                        .setMessage("Are you sure you want to close the application?")

                    builder.create()
                }
                alertDialog?.show()
            }
        })

        return binding.root
    }


}
