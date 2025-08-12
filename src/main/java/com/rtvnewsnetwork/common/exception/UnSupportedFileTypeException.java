package com.rtvnewsnetwork.common.exception;

import java.io.Serial;

public class UnSupportedFileTypeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3854058871175688734L;

    public UnSupportedFileTypeException(String errorMessage) {
        super(errorMessage);
    }
}
