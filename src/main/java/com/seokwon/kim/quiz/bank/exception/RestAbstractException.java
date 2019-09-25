package com.seokwon.kim.quiz.bank.exception;

import java.util.HashMap;
import java.util.Map;

public abstract  class RestAbstractException extends RuntimeException{
    protected RestAbstractException(String msg) {
        super(msg);
    }
    public Map<String, String> getBody() {
        final Map<String, String> map = new HashMap<>();
        map.put("status", getMessage());

        return map;
    }
}
