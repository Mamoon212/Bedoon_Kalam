package com.mo2a.example.charades_


import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.mo2a.example.charades_.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        val homeViewModelFactory = HomeViewModelFactory(activity?.application!!, activity!!)
        val homeViewModel =
            ViewModelProviders.of(this, homeViewModelFactory).get(HomeViewModel::class.java)
        binding.homeViewModel = homeViewModel

        homeViewModel.navigateToPick.observe(this, androidx.lifecycle.Observer {
            if (it) {
                this.findNavController()
                    .navigate(HomeFragmentDirections.actionHomeFragmentToPickModeFragment())
                homeViewModel.doneNavigating()
            }
        })

        homeViewModel.showAboutDialog.observe(this, Observer {
            if (it) {
                val alertDialog = activity?.let {activity ->
                    val builder = AlertDialog.Builder(activity)
                    builder.apply {
                        setPositiveButton("Dismiss") { _, _ ->
                            homeViewModel.doneShowing()
                        }
                    }
                        .setTitle("About this application")
                        .setMessage(
                            "- لو عجبك الأبلكيشن وعايز تدعمني وكمان متشوفش إعلانات تاني تقدر تشتري النسخة المدفوعة. \n" +
                                    "- أول مرة تفتح اللعبة ممكن تاخد وقت في التحميل ممكن يوصل دقايق، ده هيحصل أول مرة بس، سيبه يكمل عادي وهي هتشتغل لما يخلص.\n" +
                                    "- مكتبة الأفلام فيها ١٧٢٢ فيلم، هي كل الأفلام المتسجلة على ويكيبيديا من ١٩٢٠ لحد ٢٠١٩، اختيار الأسامي بيكون عشوائي فوارد إن يحصل تكرار لكن ده مش معناه إن الأفلام خلصت. \n" +
                                    "- المفروض كل واحد يمسك الموبايل الّي عليه اللعبة في دوره ويديه للّي بعده لما يخلص. \n" +
                                    "- اتبسطوا!"
                        )

                    builder.create()
                }
                alertDialog?.show()
            }
        })


        requireActivity().onBackPressedDispatcher.addCallback(this, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })

        binding.lifecycleOwner = this
        return binding.root
    }

}
