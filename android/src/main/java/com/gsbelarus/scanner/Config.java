package com.gsbelarus.scanner;

import java.io.Serializable;

import device.scanner.ScannerService;

public class Config implements Serializable {

    private String notificationText = "Scanning in progress...";
    private String notificationColor = "#757575";
    private boolean vibrationEnabled = true;
    private boolean beepEnabled = true;
    private int triggerMode = ScannerService.TriggerMode.DCD_TRIGGER_MODE_ONESHOT;

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public String getNotificationColor() {
        return notificationColor;
    }

    public void setNotificationColor(String notificationColor) {
        this.notificationColor = notificationColor;
    }

    public boolean isVibrationEnabled() {
        return vibrationEnabled;
    }

    public void setVibrationEnabled(boolean vibrationEnabled) {
        this.vibrationEnabled = vibrationEnabled;
    }

    public boolean isBeepEnabled() {
        return beepEnabled;
    }

    public void setBeepEnabled(boolean beepEnabled) {
        this.beepEnabled = beepEnabled;
    }

    public int getTriggerMode() {
        return triggerMode;
    }

    public void setTriggerMode(int triggerMode) {
        switch (triggerMode) {
            case ScannerService.TriggerMode.DCD_TRIGGER_MODE_AUTO:
            case ScannerService.TriggerMode.DCD_TRIGGER_MODE_CONTINUOUS:
            case ScannerService.TriggerMode.DCD_TRIGGER_MODE_ONESHOT:
                this.triggerMode = triggerMode;
                break;
            default:
                throw new RuntimeException("Unknown triggerMode");
        }

    }
}