package com.seokwon.kim.quiz.bank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

public class NotFoundDeviceException extends RestAbstractException {
    public NotFoundDeviceException() {
        super("Not Found Device");
    }
    public Map<String, String> getBody() {
        final Map<String, String> map = new HashMap<>();
        map.put("status", getMessage());

        return map;
    }
}
