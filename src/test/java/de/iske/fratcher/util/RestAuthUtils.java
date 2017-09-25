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
        final AuthenticationService.UserToken userToken = authenticationService.login("admin", "powerlan");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + userToken.token);
        return new HttpEntity<>(entity, headers);
    }

    public <T> HttpEntity<T> getEntityWithUserAuthHeader(T entity, String user, String password) {
        final AuthenticationService.UserToken userToken = authenticationService.login(user, password);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + userToken.token);
        return new HttpEntity<>(entity, headers);
    }
}
