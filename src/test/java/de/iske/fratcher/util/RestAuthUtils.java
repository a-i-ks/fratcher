package de.iske.fratcher.util;

import de.iske.fratcher.authentication.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class RestAuthUtils {

    @Autowired
    AuthenticationService authenticationService;

    public <T> HttpEntity<T> getEntityWithAdminAuthHeader(T entity) {
        final AuthenticationService.UserToken userToken = authenticationService.login("admin", "kla4st#en");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + userToken.token);
        return new HttpEntity<>(entity, headers);
    }
}
