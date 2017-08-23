package de.iske.fratcher.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class RestAuthUtils {

    public static <T> HttpEntity<T> getEntityWithAdminAuthHeader(T entity) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBmcmF0Y2hlci5kZSIsImp0aSI6IjEifQ.xBedRIOA_j5QeUH3uUu5f6y6RufIoJdUjXjevNYLUK2SxXSRbbZmcnYaymd5uyN3j2Y445kPIAtcP1W5KSCZzw");
        return new HttpEntity<>(entity, headers);
    }
}
