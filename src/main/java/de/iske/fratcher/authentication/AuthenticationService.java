package de.iske.fratcher.authentication;

import de.iske.fratcher.user.User;
import de.iske.fratcher.user.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthenticationService {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    private UserService userService;

    @Value("${authenticationService.jwt.secret}")
    private String JWTSECRET;

    @Value("${authenticationService.salt}")
    private String SALT;

    @Value("${authenticationService.expiration_time}")
    private long EXPIRATION_TIME; // 10 days

    /**
     * Create a JWT token and additional user information if the user's credentails are valid.
     *
     * @param emailOrUsername    email or username
     * @param password password
     * @return a UserToken or null if the credentials are not valid
     */
    public UserToken login(String emailOrUsername, String password) {
        String hashedPassword = hashPassword(password);
        User user = userService.getUser(emailOrUsername, hashedPassword);
        if (user == null) {
            LOG.info("User unable to login. user={}", emailOrUsername);
            return null;
        }
        LOG.info("User successfully logged in. user={}", emailOrUsername);

        String token = Jwts.builder()
                .setSubject(emailOrUsername)
                .setId(user.getId().toString())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, JWTSECRET)
                .compact();

        UserToken userToken = new UserToken();
        userToken.user = user;
        userToken.token = token;
        return userToken;
    }

    /**
     * Validate that a token is valid and returns its body.
     * <p>
     * Throws a SignatureException if the token is not valid.
     *
     * @param jwtToken JWT token
     * @return JWT body
     */
    public Object parseToken(String jwtToken) {
        LOG.debug("Parsing JWT token. JWTtoken={}", jwtToken);
        return Jwts.parser()
                .setSigningKey(JWTSECRET)
                .parse(jwtToken)
                .getBody();
    }

    /**
     * Return (salt + password) hashed with SHA-512.
     * <p>
     * The salt is configured in the property authenticationService.salt.
     *
     * @param password plain text password
     * @return hashed password
     */
    private String hashPassword(String password) {
        return DigestUtils.sha512Hex(SALT + password);

    }

    /**
     * Return object containing a valid user and his corresponding JWT token.
     */
    public static class UserToken {
        public User user;
        public String token;
    }


}
