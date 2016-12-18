package com.think.mozzo_test

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.google.android.gms.nearby.messages.Strategy
import com.google.android.gms.nearby.messages.SubscribeOptions
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener
import com.kontakt.sdk.android.ble.manager.ProximityManager
import com.kontakt.sdk.android.ble.manager.listeners.EddystoneListener
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener
import com.kontakt.sdk.android.common.KontaktSDK
import com.kontakt.sdk.android.common.profile.IBeaconDevice
import com.kontakt.sdk.android.common.profile.IBeaconRegion
import com.kontakt.sdk.android.common.profile.IEddystoneDevice
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace

import java.util.ArrayList

class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mMessageListener: MessageListener? = null
    private val proximityManager: ProximityManager? = null

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    private var myDataset: ArrayList<String>? = null

    private val TAG = "Java Sample"
    private val initialData = "Searching for URL's"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //        KontaktSDK.initialize(this);

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()

        mMessageListener = object : MessageListener() {
            override fun onFound(message: Message) {
                val messageAsString = String(message.content)
                Log.d(TAG, "Found message: " + messageAsString)
                if (myDataset!![0] == initialData) {
                    myDataset!!.removeAt(0)
                }
                myDataset!!.add(messageAsString)
                mAdapter!!.notifyDataSetChanged()
            }

            override fun onLost(message: Message?) {
                val messageAsString = String(message!!.content)
                Log.d(TAG, "Lost sight of message: " + messageAsString)
                for (i in myDataset!!.indices) {
                    if (myDataset!![i] == messageAsString) {
                        myDataset!!.removeAt(i)
                    }
                }
                if (myDataset!!.size == 0) {
                    myDataset!!.add(initialData)
                }
                mAdapter!!.notifyDataSetChanged()
            }
        }


        //        proximityManager = new ProximityManager(this);
        //        proximityManager.setIBeaconListener(createIBeaconListener());
        //        proximityManager.setEddystoneListener(createEddystoneListener());

        mRecyclerView = findViewById(R.id.my_recycler_view) as RecyclerView?

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView!!.setHasFixedSize(true)

        // use a linear layout manager
        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView!!.layoutManager = mLayoutManager

        // specify an adapter (see also next example)
        myDataset = ArrayList<String>()
        myDataset!!.add(initialData)
        mAdapter = MaterialAdapter(myDataset as ArrayList<String>)
        mRecyclerView!!.adapter = mAdapter

        println("Done")

    }

    //    private IBeaconListener createIBeaconListener() {
    //        return new SimpleIBeaconListener() {
    //            @Override
    //            public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region) {
    //                Log.i(TAG, "IBeacon discovered: " + ibeacon.toString());
    //            }
    //        };
    //    }
    //
    //    private EddystoneListener createEddystoneListener() {
    //        return new SimpleEddystoneListener() {
    //            @Override
    //            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
    //                Log.i(TAG, "Eddystone discovered: " + eddystone.toString());
    //            }
    //        };
    //    }

    //    private void startScanning() {
    //        proximityManager.connect(new OnServiceReadyListener() {
    //            @Override
    //            public void onServiceReady() {
    //                proximityManager.startScanning();
    //            }
    //        });
    //    }

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
        //        startScanning();
        mGoogleApiClient!!.connect()
    }

    public override fun onStop() {
        if (mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.disconnect()
        }

        //        proximityManager.disconnect();
        //        proximityManager = null;
        super.onStop()
    }
}
