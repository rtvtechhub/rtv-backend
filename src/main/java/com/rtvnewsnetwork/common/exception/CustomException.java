package com.rtvnewsnetwork.common.exception;

import java.io.Serial;

public class CustomException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3854058871175688734L;

    public CustomException(String errorMessage) {
        super(errorMessage);
    }
}
