package com.think.mozzo_test

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.google.android.gms.nearby.messages.Strategy
import com.google.android.gms.nearby.messages.SubscribeOptions

class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mMessageListener: MessageListener? = null

    private val TAG = "Java Sample"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()

        mMessageListener = object : MessageListener() {
            override fun onFound(message: Message) {
                val messageAsString = String(message.content)
                Log.d(TAG, "Found message: " + messageAsString)
            }

            override fun onLost(message: Message?) {
                val messageAsString = String(message!!.content)
                Log.d(TAG, "Lost sight of message: " + messageAsString)
            }
        }

        println("Done")
    }

    private fun subscribe() {
        Log.i(TAG, "Subscribing.")
        val options = SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY)
                .build()
        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, options)
    }

    override fun onConnected(bundle: Bundle?) {

        subscribe()
    }

    override fun onConnectionSuspended(i: Int) {

        //        Log.i()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, 1001)
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            }

        } else {
            println("NO resolution")
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK) {
                mGoogleApiClient!!.connect()
            } else {
                Log.e(TAG, "GoogleApiClient connection failed. Unable to resolve.")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    public override fun onStart() {
        super.onStart()
        mGoogleApiClient!!.connect()
    }

    public override fun onStop() {
        if (mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.disconnect()
        }

        super.onStop()
    }
}
