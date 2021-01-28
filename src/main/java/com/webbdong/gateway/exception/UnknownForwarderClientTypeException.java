package com.webbdong.gateway.exception;

/**
 * @author Webb Dong
 * @description: UnknownForwarderClientTypeException
 * @date 2021-01-28 6:45 PM
 */
public class UnknownForwarderClientTypeException extends RuntimeException {

    public UnknownForwarderClientTypeException() {
    }

    public UnknownForwarderClientTypeException(String message) {
        super(message);
    }

    public UnknownForwarderClientTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownForwarderClientTypeException(Throwable cause) {
        super(cause);
    }

    public UnknownForwarderClientTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
