package com.mo2a.example.charades_

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*

class HomeViewModel(application: Application, private val activity: Activity) :
    AndroidViewModel(application) {
//    private lateinit var billingClient: BillingClient
//    private lateinit var skuDetails: SkuDetails

    private val _navigateToPick = MutableLiveData<Boolean>()
    val navigateToPick: LiveData<Boolean>
        get() = _navigateToPick

    private val _showAboutDlaog = MutableLiveData<Boolean>()
    val showAboutDialog: LiveData<Boolean>
        get() = _showAboutDlaog

//    init {
//        setupPayment()
//    }


    fun play() {
        _navigateToPick.value = true
    }

    fun about() {
        _showAboutDlaog.value = true
    }

    fun doneNavigating() {
        _navigateToPick.value = false
    }

    fun doneShowing() {
        _showAboutDlaog.value = false
    }
//
//    fun pay() {
//        val billingFlowParams = BillingFlowParams.newBuilder()
//            .setSkuDetails(loadSKU())
//            .build()
//
//        billingClient.launchBillingFlow(activity, billingFlowParams)
//    }
//
//    private fun setupPayment() {
//        billingClient =
//            BillingClient.newBuilder(this.getApplication()).setListener(this)
//                .enablePendingPurchases().build()
//        billingClient.startConnection(object : BillingClientStateListener {
//            override fun onBillingServiceDisconnected() {
//                Log.i("hehe", "disconnected")
//            }
//
//            override fun onBillingSetupFinished(result: BillingResult?) {
//                if (result?.responseCode == BillingClient.BillingResponseCode.OK) {
//                    Log.i("hehe", "success")
//                } else {
//                    Log.i("hehe", result?.responseCode.toString())
//                }
//            }
//        })
//
//    }
//
//    private fun loadSKU(): SkuDetails? {
//        var skuDetails: SkuDetails? = null
//        if (billingClient.isReady) {
//            val params = SkuDetailsParams
//                .newBuilder()
//                .setSkusList(mutableListOf("paidversionnoads15996"))
//                .setType(BillingClient.SkuType.INAPP)
//                .build()
//
//            billingClient.querySkuDetailsAsync(params) { result, list ->
//                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
//                    skuDetails = list[0]
//                }else{
//                    Log.i("hehe", "error")
//                }
//            }
//        }
//
//        return skuDetails
//    }
//
//    override fun onPurchasesUpdated(p0: BillingResult?, p1: MutableList<Purchase>?) {
//        Log.i("hehe", " hahahaha")
//    }

}