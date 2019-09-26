package com.seokwon.kim.quiz.bank.exception;

public class DeviceStatException extends RuntimeException {
    public static final int CANNOT_SAVE_DB = 0;
    public static final int AUTENTICATION_ERR = 1;
    private final int code;
    public DeviceStatException(final int code) {
        super("Device Stat error");
        this.code = code;
    }
    public DeviceStatException(final int code, String msg) {
        super(msg);
        this.code = code;
    }
}
