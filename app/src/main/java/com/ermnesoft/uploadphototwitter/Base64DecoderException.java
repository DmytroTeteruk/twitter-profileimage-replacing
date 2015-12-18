package com.ermnesoft.uploadphototwitter;

/**
 * Describes the exception occurs when decoding Base64
 */
public class Base64DecoderException extends Exception {

    /**
     * serial number class
     */
    private static final long serialVersionUID = 1776685144003004526L;

    /**
     * base constructor in
     */
    public Base64DecoderException() {
        super();
    }

    /**
     * constructor with parameter
     * @param s parameter screen
     */
    public Base64DecoderException(String s) {
        super(s);
    }
}


