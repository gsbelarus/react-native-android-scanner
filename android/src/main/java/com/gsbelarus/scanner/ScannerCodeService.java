package com.gsbelarus.scanner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import device.scanner.DecodeResult;
import device.scanner.IScannerService;
import device.scanner.ScannerService;

public class ScannerCodeService extends Service {

    private static final int NOTIFICATION_ID = 321;

    private NotificationManager notificationManager;
    private IScannerService scannerService;

    private DecodeResult decodeResult = new DecodeResult();
    private Config config = new Config();

    private BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            executeScannerApi(new Executor<Void>() {
                @Override
                public Void run() throws RemoteException {
                    decodeResult.recycle();
                    scannerService.aDecodeGetResult(decodeResult);

                    ScannerCallbackTaskService.acquireWakeLockNow(context);
                    Intent serviceIntent = new Intent(context, ScannerCallbackTaskService.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("value", decodeResult.decodeValue);
                    bundle.putInt("time", decodeResult.decodeTimeMillisecond);
                    bundle.putInt("length", decodeResult.decodeLength);
                    bundle.putString("symName", decodeResult.symName);
                    bundle.putInt("symType", decodeResult.symType);

                    serviceIntent.putExtras(bundle);
                    startService(serviceIntent);
                    return null;
                }
            });
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            config = (Config) intent.getSerializableExtra("config");

            executeScannerApi(new Executor<Void>() {
                @Override
                public Void run() throws RemoteException {
                    initConfig();
                    return null;
                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        startForeground(NOTIFICATION_ID, createNotification());
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        scannerService = IScannerService.Stub.asInterface(ServiceManager.getService("ScannerService"));

        executeScannerApi(new Executor<Void>() {
            @Override
            public Void run() throws RemoteException {
                scannerService.aDecodeAPIInit();
                return null;
            }
        });

        IntentFilter intentFilter = new IntentFilter("device.scanner.USERMSG");
        registerReceiver(scanReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(scanReceiver);

        executeScannerApi(new Executor<Void>() {
            @Override
            public Void run() throws RemoteException {
                scannerService.aDecodeSetDecodeEnable(0);
                scannerService.aDecodeAPIDeinit();
                return null;
            }
        });
    }

    private void initConfig() throws RemoteException {
        notificationManager.notify(NOTIFICATION_ID, createNotification());

        scannerService.aDecodeSetDecodeEnable(1);
        scannerService.aDecodeSetResultType(ScannerService.ResultType.DCD_RESULT_USERMSG);
        scannerService.aDecodeSetVibratorEnable(config.isVibrationEnabled() ? 1 : 0);
        scannerService.aDecodeSetBeepEnable(config.isBeepEnabled() ? 1 : 0);
        scannerService.aDecodeSetTriggerMode(config.getTriggerMode());
    }

    private Notification createNotification() {
        int appNameId = getResources().getIdentifier("app_name", "string", getPackageName());
        int appLauncherIconId = getResources().getIdentifier("ic_launcher", "mipmap", getPackageName());

        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this)
                .setContentTitle(getString(appNameId))
                .setContentText(config.getNotificationText())
                .setSmallIcon(appLauncherIconId)
                .setColor(Color.parseColor(config.getNotificationColor()))
                .setContentIntent(pendingIntent)
                .build();
    }

    private <T> T executeScannerApi(Executor<T> executor) {
        try {
            if (scannerService != null) return executor.run();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    private interface Executor<T> {
        T run() throws RemoteException;
    }
}