package com.think.mozzo_test.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by anand on 19/12/16.
 */

public class DataSyncService extends Service {
    private static DataSyncAdapter SyncAdapter = null;
    private static final Object SyncAdapterLock = new Object();

    @Override
    public void onCreate() {

        synchronized (SyncAdapterLock) {
            if (SyncAdapter == null) {
                SyncAdapter = new DataSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        return SyncAdapter.getSyncAdapterBinder();
    }
}