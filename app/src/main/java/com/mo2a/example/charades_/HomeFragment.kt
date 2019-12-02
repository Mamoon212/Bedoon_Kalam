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
import com.mo2a.example.charades_.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentHomeBinding= DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        val homeViewModel= ViewModelProviders.of(this).get(HomeViewModel::class.java)

        binding.homeViewModel= homeViewModel

        homeViewModel.navigateToPick.observe(this, androidx.lifecycle.Observer {
            if(it){
                this.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPickModeFragment())
                homeViewModel.doneNavigating()
            }
        })

        homeViewModel.showAboutDialog.observe(this, Observer {
            if(it){
                val alertDialog = activity?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.apply {
                        setPositiveButton("Dismiss") { _, _ ->
                            homeViewModel.doneShowing()
                        }
                    }
                        .setTitle("About this application")
                        .setMessage("Blah blah blah blah blah")

                    builder.create()
                }
                alertDialog?.show()
            }
        })


        requireActivity().onBackPressedDispatcher.addCallback(this,object :
            OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })

        binding.lifecycleOwner= this
        return binding.root
    }


}
