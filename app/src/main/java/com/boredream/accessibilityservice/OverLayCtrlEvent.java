package com.boredream.accessibilityservice;

public class OverLayCtrlEvent {

    private String command;

    public OverLayCtrlEvent(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
