package com.rtvnewsnetwork.common.exception;

import java.io.Serial;

public class ForbiddenException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 4292001101880737691L;

    public ForbiddenException(String message) {
        super(message);
    }

}
