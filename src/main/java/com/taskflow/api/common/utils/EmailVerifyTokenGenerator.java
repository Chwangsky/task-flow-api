package com.taskflow.api.common.utils;

import java.util.UUID;

public class EmailVerifyTokenGenerator {

    public static String generateVerifyToken() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}
