package com.think.mozzo_test

import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import android.text.TextUtils
import android.util.Log

import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

import java.util.ArrayList

/**
 * Created by anand on 19/12/16.
 */

class GeofenceService : IntentService(GeofenceService.TAG) {

    override fun onHandleIntent(intent: Intent?) {
        val event = GeofencingEvent.fromIntent(intent)
        if (event.hasError()) {
            Log.e(TAG, "GeofencingEvent Error: " + event.errorCode)
            return
        }
        val description = getGeofenceTransitionDetails(event)
        sendNotification(description)
    }

    private fun sendNotification(notificationDetails: String) {
        val notificationIntent = Intent(applicationContext, MainActivity::class.java)

        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(MainActivity::class.java).addNextIntent(notificationIntent)
        val notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this)

        builder.setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText("Click notification to return to App")
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())
    }

    companion object {
        protected val TAG = "GeofenceTransitionsIS"

        private fun getGeofenceTransitionDetails(event: GeofencingEvent): String {
            val transitionString = GeofenceStatusCodes.getStatusCodeString(event.geofenceTransition)
            val triggeringIDs : ArrayList<String>?=null
            for (geofence in event.triggeringGeofences) {
                triggeringIDs!!.add(geofence.requestId)
            }
            return String.format("%s: %s", transitionString, TextUtils.join(", ", triggeringIDs))
        }
    }

}// use TAG to name the IntentService worker thread