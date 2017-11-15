package com.gsbelarus.scanner;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.ServiceManager;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import device.scanner.IScannerService;
import device.scanner.ScannerService;

public class ScannerModule extends ReactContextBaseJavaModule {

    public static final String SCANNER_READ_FAIL = "READ_FAIL";
    public static final String SCANNER_CALLBACK_TASK = "scannerCallbackTask";
    public static final String ON_STATUS_SCANNED_CHANGED = "onStatusScannedChanged";
    public static final String ON_SCANNED = "onScanned";

    public ScannerModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "Scanner";
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        Map<String, Object> constants = new HashMap<>();

        constants.put("SCANNER_READ_FAIL", SCANNER_READ_FAIL);
        constants.put("SCANNER_CALLBACK_TASK", SCANNER_CALLBACK_TASK);
        constants.put("ON_SCANNED", ON_SCANNED);
        constants.put("ON_STATUS_SCANNED_CHANGED", ON_STATUS_SCANNED_CHANGED);

        constants.put("TRIGGER_MODE_ONESHOT", ScannerService.TriggerMode.DCD_TRIGGER_MODE_ONESHOT);
        constants.put("TRIGGER_MODE_AUTO", ScannerService.TriggerMode.DCD_TRIGGER_MODE_AUTO);
        constants.put("TRIGGER_MODE_CONTINUOUS", ScannerService.TriggerMode.DCD_TRIGGER_MODE_CONTINUOUS);

        return constants;
    }

    @ReactMethod
    public void start(ReadableMap options, Promise promise) {
        try {
            Intent intent = new Intent(getReactApplicationContext(), ScannerCodeService.class);
            intent.putExtra("config", initConfig(options));

            ComponentName result = getReactApplicationContext().startService(intent);
            if (result == null) {
                throw new RuntimeException("Service didn't started");
            }

            promise.resolve(null);
        } catch (RuntimeException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void stop(Promise promise) {
        try {
            Intent intent = new Intent(getReactApplicationContext(), ScannerCodeService.class);

            boolean result = getReactApplicationContext().stopService(intent);
            if (!result) {
                throw new RuntimeException("Service didn't stopped");
            }

            promise.resolve(null);
        } catch (RuntimeException e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void isRunning(Promise promise) {
        Boolean isRunning = false;
        ActivityManager manager = (ActivityManager) getReactApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ScannerCodeService.class.getName().equals(service.service.getClassName())) {
                isRunning = true;
                break;
            }
        }
        promise.resolve(isRunning);
    }

    @ReactMethod
    public void isDeviceSupported(Promise promise) {
        promise.resolve(isServiceAvailable());
    }

    private Config initConfig(ReadableMap options) throws RuntimeException {
        Config config = new Config();

        if (options.hasKey("notificationText")) {
            config.setNotificationText(options.getString("notificationText"));
        }
        if (options.hasKey("notificationColor")) {
            config.setNotificationColor(options.getString("notificationColor"));
        }
        if (options.hasKey("vibration")) {
            config.setVibrationEnabled(options.getBoolean("vibration"));
        }
        if (options.hasKey("beep")) {
            config.setBeepEnabled(options.getBoolean("beep"));
        }
        if (options.hasKey("triggerMode")) {
            config.setTriggerMode(options.getInt("triggerMode"));
        }

        return config;
    }

    private boolean isServiceAvailable() {
        return IScannerService.Stub.asInterface(ServiceManager.getService("ScannerService")) != null;
    }
}
