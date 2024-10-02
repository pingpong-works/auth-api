package com.auth.exception;

import lombok.Getter;

public enum ExceptionCode {
    NO_AUTHORITY (403, "No Authority"),
    EMPLOYEE_NOT_FOUND(404, "Employee Not Found"),
    EMPLOYEE_NOT_CORRECT(409, "Employee Not Correct"),
    INVALID_DELETE_REQUEST(404, "Invalid Delete Request"),
    NO_CHANGED(404, "No Changed");

    @Getter
    private int status;

    @Getter
    private String message;
    ExceptionCode(int code, String message) {
        this.status = code;
        this.message = message;
    }
}