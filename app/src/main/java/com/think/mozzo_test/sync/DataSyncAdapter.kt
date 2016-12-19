package com.think.mozzo_test.sync

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle

/**
 * Created by anand on 19/12/16.
 */

class DataSyncAdapter(context: Context, autoInitialize: Boolean) : AbstractThreadedSyncAdapter(context, autoInitialize) {

    override fun onPerformSync(account: Account, bundle: Bundle, s: String, contentProviderClient: ContentProviderClient, syncResult: SyncResult) = //Synchronization Code here
            /* we can do things like
              1) downloading data from a server
              2) Uploading data to server
              3) be sure to handle network related exceptions.
            */
            // For exapmple i am connecting to a server
            Unit
}
