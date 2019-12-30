package com.ljw.okserialport.serialport.utils;

public class ApiException extends RuntimeException {
    /**
     * 用String，若是int转下就是
     */
    private final String code;

    public ApiException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ApiException(String code, Throwable message) {
        super(message);
        this.code = code;
    }

    /**
     * 异常码
     */
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "ApiException{" +
                "code='" + code + '\'' + ",message='" + getMessage() + '\'' +
                '}';
    }
}

