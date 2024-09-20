package com.taskflow.api.common;

public interface ResponseCode {

    // HTTP Status Code 200
    String SUCCESS = "SU";

    // HTTP STATUS 400
    String VALIDATION_FAILED = "VF";
    String DUPLICATE_EMAIL = "DE";
    String DUPLICATE_NICKNAME = "DN";
    String DUPLICATE_TEL_NUMBER = "DT";
    String NOT_EXISTED_USER = "NU";
    String NOT_EXISTED_BOARD = "NB";

    // HTTP STATUS 401
    String SIGN_IN_FAIL = "SF";
    String AUTHORIZATION_FAIL = "AF";
    String ALREADY_VERIFIED_ACCOUNT = "AV";
    String NOT_VERIFIED_ACCOUNT = "NV";

    // HTTP STATUS 403
    String NO_PERMISSION = "NP";
    String MISMATCH_TOKEN = "ME";

    // HTTP STATUS 410
    String TOKEN_EXPIRED = "TE";

    // HTTP STATUS 500
    String MAIL_SENDER_ERROR = "MSE";
    String DATABASE_ERROR = "DBE";

}