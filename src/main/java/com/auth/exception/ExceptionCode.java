package com.auth.exception;

import lombok.Getter;

public enum ExceptionCode {
    NO_AUTHORITY (403, "No Authority"),
    EMPLOYEE_NOT_FOUND(404, "Employee Not Found"),
    EMPLOYEE_EXIST(409, "Employee Already Exist"),
    DEPARTMENT_NOT_FOUND(404, "Department Not Found"),
    DEPARTMENT_EXIST(409, "Department Already Exist"),
    NO_CHANGED(404, "No Changed"),

    //Access
    ACCESS_DENIED(403,"Access denied"),
    PASSWORD_MISMATCH(403,"Password Mismatch"),
    MEMBER_FOOD_NOT_FOUND(404, "Member Food Not Found"),
    ALLERGY_Food_FOOD_NOT_FOUND(404, "Allergy Food Not Found"),

    //토큰 인증 관련
    UNAUTHORIZED_MEMBER(401, "권한이 없는 멤버입니다."),
    TOKEN_INVALID(403, "토큰값이 유효하지 않습니다."),
    TOKEN_ISNULL(404,"토큰값을 전달받지 못했습니다.");


    @Getter
    private int status;

    @Getter
    private String message;
    ExceptionCode(int code, String message) {
        this.status = code;
        this.message = message;
    }
}