package com.boredream.accessibilityservice.event;

public class OverlayInfoUpdateEvent {

    private String progress;
    private String status;

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
