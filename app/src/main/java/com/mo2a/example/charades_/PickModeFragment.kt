package com.mo2a.example.charades_

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.mo2a.example.charades_.databinding.FragmentPickModeBinding
import kotlinx.android.synthetic.main.fragment_pick_mode.*

class PickModeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val binding: FragmentPickModeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_pick_mode, container, false)

        val pickModeViewModel = ViewModelProviders.of(this).get(PickModeViewModel::class.java)

        binding.pickModeViewModel = pickModeViewModel

        pickModeViewModel.team.observe(this, Observer {
            if (it == true) {
                textView3.visibility = View.VISIBLE
                number_group.visibility = View.VISIBLE
            } else {
                textView3.visibility = View.GONE
                number_group.visibility = View.GONE
            }
        })

        pickModeViewModel.navigateToGameFrag.observe(this, Observer {
            if (it) {
                if (binding.modeGroup.checkedRadioButtonId == -1 ||
                    binding.numRoundGroup.checkedRadioButtonId == -1 ||
                    binding.durationGroup.checkedRadioButtonId == -1
                ) {
                    //options missing
                    Toast.makeText(
                        this.activity,
                        "Please choose all game settings.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    if (binding.modeGroup.checkedRadioButtonId == R.id.teams &&
                        binding.numberGroup.checkedRadioButtonId == -1
                    ) {
                        //options missing
                        Toast.makeText(
                            this.activity,
                            "Please choose all game settings.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        //all options selected
                        this.findNavController().navigate(
                            PickModeFragmentDirections.actionPickModeFragmentToGameFragment(
                                pickModeViewModel.numTeams,
                                pickModeViewModel.numRounds,
                                pickModeViewModel.duration
                            )
                        )
                        pickModeViewModel.doneNavigating()
                    }
                }

            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(PickModeFragmentDirections.actionPickModeFragmentToHomeFragment())
            }
        })



        binding.lifecycleOwner = this
        return binding.root
    }


}
