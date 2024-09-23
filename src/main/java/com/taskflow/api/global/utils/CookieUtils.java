package com.taskflow.api.global.utils;

import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtils {

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }

        return Optional.empty();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static void addHttpOnlyCookie(HttpServletResponse response, String name, String value, int maxAge,
            boolean secure) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true); // HttpOnly 속성 설정
        cookie.setSecure(secure); // secure 속성 설정 (HTTPS 사용 시 true로 설정)
        cookie.setMaxAge(maxAge); // 쿠키의 유효 기간 설정
        response.addCookie(cookie);
    }

    // public static String serialize(Object object) {
    // return Base64.getUrlEncoder()
    // .encodeToString(SerializationUtils.serialize(object));
    // }

    // public static <T> T deserialize(Cookie cookie, Class<T> cls) {
    // return cls.cast(SerializationUtils.deserialize(
    // Base64.getUrlDecoder().decode(cookie.getValue())));
    // }
}