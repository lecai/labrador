package com.threathunter.labrador.spl.check;

/**
 * 
 */
public class CheckException extends Exception {

    public CheckException(String msg) {
        super(msg);
    }

    public CheckException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
