package com.mo2a.example.charades_

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*

class HomeViewModel(application: Application, private val activity: Activity) :
    AndroidViewModel(application), PurchasesUpdatedListener {
    private lateinit var billingClient: BillingClient
    private val skuList = listOf("android.test.purchased")
    private lateinit var skuDetails: SkuDetails


    private val _navigateToPick = MutableLiveData<Boolean>()
    val navigateToPick: LiveData<Boolean>
        get() = _navigateToPick

    private val _showAboutDlaog = MutableLiveData<Boolean>()
    val showAboutDialog: LiveData<Boolean>
        get() = _showAboutDlaog

    init {
        setupBillingClient()
        if(!ranBefore()) {
            checkPastPurchases()
        }
    }


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

    fun pay() {
        if (::skuDetails.isInitialized) {
            val billingFlowParams = BillingFlowParams
                .newBuilder()
                .setSkuDetails(skuDetails)
                .build()
            billingClient.launchBillingFlow(this.activity, billingFlowParams)
        } else {
            Toast.makeText(
                activity,
                "Failed to connect to Google services, try again later.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this.activity)
            .enablePendingPurchases()
            .setListener(this)
            .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is setup successfully
                    Log.i("3aww", "done")
                    loadAllSKUs()

                } else {
                    Log.i("3aww", "failed")

                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.i("3aww", "fail")

            }
        })

    }

    private fun loadAllSKUs() = if (billingClient.isReady) {
        val params = SkuDetailsParams
            .newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()
        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            // Process the result.
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList.isNotEmpty()) {
                for (skuDetails in skuDetailsList) {
                    //this will return both the SKUs from Google Play Console
                    if (skuDetails.sku == "android.test.purchased") {
                        this.skuDetails = skuDetails
                    }

                }
            } else {
                Log.i("3aww", "wrong")
            }
        }

    } else {
        println("Billing Client not ready")
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult?,
        purchases: MutableList<Purchase>?
    ) {
        checkPastPurchases()
        if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    acknowledgePurchase(purchase.purchaseToken)
                }
            }

        } else if (billingResult?.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            // Handle any other error codes.
            Toast.makeText(
                activity,
                "You have already bought the ad-free version.",
                Toast.LENGTH_LONG
            ).show()
            if (!getApplication<Application>().getSharedPreferences("MyPrefs", 0).getBoolean(
                    "paid",
                    false
                )
            ) {
                saveIntoSharedPrefs()
            }
        } else {
            // Handle an error caused by a user cancelling the purchase flow.
            Toast.makeText(activity, "Purchase incomplete.", Toast.LENGTH_LONG).show()


        }
    }

    private fun acknowledgePurchase(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { billingResult ->
            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
            if (responseCode == BillingClient.BillingResponseCode.OK) {
                saveIntoSharedPrefs()
            } else {
                Log.e("purchaseError", debugMessage)
            }

        }
    }

    @SuppressLint("ApplySharedPref")
    private fun saveIntoSharedPrefs() {
        val pref = getApplication<Application>().getSharedPreferences("MyPrefs", 0)
        val editor = pref.edit()
        editor.putBoolean("paid", true)
        editor.commit()
    }

    private fun checkPastPurchases() {
        val purchaseHistory: List<Purchase>? =
            billingClient.queryPurchases(BillingClient.SkuType.INAPP).purchasesList
        Log.i("history", "$purchaseHistory")
        purchaseHistory?.let {
            Log.i("yo", "yoyoyo")
            if (it.isNotEmpty()) {
                saveIntoSharedPrefs()
            }
        }
    }

    @SuppressLint("ApplySharedPref")
    private fun ranBefore(): Boolean {
        val pref = getApplication<Application>().getSharedPreferences("MyPrefs", 0)
        val bool = pref.getBoolean("ranBefore", false)
        if (!bool) {
            pref.edit().putBoolean("ranBefore", true).commit()
        }
        return bool
    }

    override fun onCleared() {
        super.onCleared()
        billingClient.endConnection()
    }
}