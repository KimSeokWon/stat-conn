package com.seokwon.kim.quiz.bank.exception;

public class DeviceStatException extends Exception {
    public static final int CANNOT_SAVE_DB = 0;
    private final int code;
    public DeviceStatException(final int code) {
        super("Device Stat error");
        this.code = code;
    }
}
