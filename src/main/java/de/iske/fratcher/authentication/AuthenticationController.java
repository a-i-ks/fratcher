package de.iske.fratcher.authentication;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * HTTP endpoint for a user-related HTTP requests.
 * @author André Iske
 * @since 2017-07-24
 */
@RestController
@RequestMapping("/api/login")
public class AuthenticationController {


    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<AuthenticationService.UserToken> login(@RequestBody UserLogin userLogin) {
        AuthenticationService.UserToken token = authenticationService.login(userLogin.email, userLogin.password);
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    public static class UserLogin {
        public String email;
        public String password;
    }





}
