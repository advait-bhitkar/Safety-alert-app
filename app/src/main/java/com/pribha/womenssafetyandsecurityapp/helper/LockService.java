package com.pribha.womenssafetyandsecurityapp.helper;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.pribha.womenssafetyandsecurityapp.helper.shakeDetector.ScreenReceiver;

public class LockService extends Service {



        @Override
        public void onCreate() {
            super.onCreate();
        }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            final BroadcastReceiver mReceiver = new ScreenReceiver();
            registerReceiver(mReceiver, filter);
            return super.onStartCommand(intent, flags, startId);
        }
        public class LocalBinder extends Binder {
            LockService getService() {
                return LockService.this;
            }
        }

    }
