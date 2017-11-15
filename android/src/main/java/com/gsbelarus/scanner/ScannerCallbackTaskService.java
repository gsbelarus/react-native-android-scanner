package com.gsbelarus.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;

public class ScannerCallbackTaskService extends HeadlessJsTaskService {

    @Override
    public void onCreate() {
        super.onCreate();

        Map<String, Object> params = new HashMap<>();
        params.put("status", "started");

        sendEvent(ScannerModule.ON_STATUS_SCANNED_CHANGED, Arguments.makeNativeMap(params));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Map<String, Object> params = new HashMap<>();
        params.put("status", "finished");

        sendEvent(ScannerModule.ON_STATUS_SCANNED_CHANGED, Arguments.makeNativeMap(params));
    }

    @Nullable
    @Override
    protected HeadlessJsTaskConfig getTaskConfig(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {

            sendEvent(ScannerModule.ON_SCANNED, Arguments.fromBundle(extras));

            return new HeadlessJsTaskConfig(
                    ScannerModule.SCANNER_CALLBACK_TASK,
                    Arguments.fromBundle(extras),
                    0,
                    true);
        }
        return null;
    }

    private void sendEvent(String eventName, @Nullable WritableMap params) {
        ReactInstanceManager reactInstanceManager = getReactNativeHost().getReactInstanceManager();
        ReactContext reactContext = reactInstanceManager.getCurrentReactContext();
        if (reactContext != null) {
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        }
    }
}