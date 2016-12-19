package com.think.mozzo_test

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
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
import com.think.mozzo_test.data.Constants
import com.think.mozzo_test.data.Constants.LANDMARKS
import java.util.*

class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    internal var mAccount: Account? = null

    protected var mGeofenceList: ArrayList<Geofence>? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mMessageListener: MessageListener? = null
    private val proximityManager: ProximityManager? = null

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    private var myDataset: ArrayList<String>? = null

    private val TAG = "Java Sample"
    private val initialData = "Searching for URL's"

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(this, LoaderActivity::class.java))
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //        mAccount = CreateSyncAccount(this);
        //        KontaktSDK.initialize(this);

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()

        mGeofenceList = ArrayList<Geofence>()
        populateGeofenceList()

        mMessageListener = object : MessageListener() {
            override fun onFound(message: Message) {
                val messageAsString = String(message.content)
                Log.d(TAG, "Found message: " + messageAsString)
                if (myDataset!![0] == initialData) {
                    myDataset!!.removeAt(0)
                }

                myDataset!!.add(messageAsString)
                mAdapter!!.notifyDataSetChanged()
                insertIntoContentProvider(messageAsString)
            }

            override fun onLost(message: Message?) {
                val messageAsString = String(message!!.content)
                Log.d(TAG, "Lost sight of message: " + messageAsString)

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

        mRecyclerView!!.setHasFixedSize(true)

        mLayoutManager = LinearLayoutManager(this)
        mRecyclerView!!.layoutManager = mLayoutManager

        myDataset = ArrayList<String>()
        myDataset!!.add(initialData)
        mAdapter = MaterialAdapter(myDataset as ArrayList<String>)
        mRecyclerView!!.adapter = mAdapter

        println("Done")

    }

    private fun insertIntoContentProvider(url: String) {
        val values = ContentValues()
        values.put(UrlHistoryProvider._ID,
                url)

        values.put(UrlHistoryProvider.TIME,
                System.currentTimeMillis())

        val uri = contentResolver.insert(
                UrlHistoryProvider.CONTENT_URI, values)

        Toast.makeText(baseContext,
                "Data Updated", Toast.LENGTH_SHORT).show()
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
        registerGeoFence()
    }

    override fun onConnectionSuspended(i: Int) {
        mGoogleApiClient!!.connect()

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

    fun populateGeofenceList() {
        for (entry in LANDMARKS) {
            mGeofenceList?.add(Geofence.Builder()
                    .setRequestId(entry.key)
                    .setCircularRegion(
                            entry.value.latitude,
                            entry.value.longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build())
        }
    }

    fun registerGeoFence() {
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    geofencingRequest,
                    geofencePendingIntent
            ).setResultCallback(this) // Result processed in onResult().
        } catch (securityException: SecurityException) {
        }

    }

    private val geofencingRequest: GeofencingRequest
        get() {
            val builder = GeofencingRequest.Builder()
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            builder.addGeofences(mGeofenceList)
            return builder.build()
        }

    private val geofencePendingIntent: PendingIntent
        get() {
            val intent = Intent(this, GeofenceService::class.java)
            return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }


    override fun onResult(status: Status) {
        if (status.isSuccess) {
            Toast.makeText(
                    this,
                    "Geofences Added Successfully",
                    Toast.LENGTH_SHORT
            ).show()
        } else {
            println("GeoFence Not Added")
        }

    }

    companion object {

        val AUTHORITY = "com.think.mozzo_test_java"
        val ACCOUNT_TYPE = "com.think.mozzo_test.datasyncservice"
        val ACCOUNT = "dummyaccount"


        fun CreateSyncAccount(context: Context): Account? {
            // Create the account type and default account
            val newAccount = Account(
                    ACCOUNT, ACCOUNT_TYPE)
            // Get an instance of the Android account manager
            val accountManager = context.getSystemService(
                    Context.ACCOUNT_SERVICE) as AccountManager
            /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (accountManager.addAccountExplicitly(newAccount, null, null)) {
                return newAccount
            } else {
                /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
                Log.d("TAG", "Error Sync Adapter Not Configured")
                return null

            }
        }
    }
}


