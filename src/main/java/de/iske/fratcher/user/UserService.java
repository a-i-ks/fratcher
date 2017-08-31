package de.iske.fratcher.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service for all user-related functional operations.
 * adopted from https://github.com/micromata/webengineering-2017/blob/master/src/main/java/com/micromata/webengineering/demo/user/UserService.java
 */
@Service
public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Sets the current user to anonymous.
     */
    public void setAnonymous() {
        setCurrentUser(-1L, "<anonymous>");
    }


    /**
     * Check if the current user is not authenticated.
     *
     * @return true if the user is not authenticated.
     */
    public boolean isAnonymous() {
        return getCurrentUser().getId() == -1L;
    }


    /**
     * Retrieve the currently active user or null, if no user is logged in.
     *
     * @return the current user.
     */
    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Set a user for the current request.
     *
     * @param id    user id
     * @param email user email
     */
    public void setCurrentUser(Long id, String email) {
        LOG.debug("Setting user context. id={}, user={}", id, email);
        User user;
        if (id > 0) {
            user = userRepository.findById(id);
        } else {
            user = new User();
            user.setId(id);
            user.setEmail(email);
        }
        UsernamePasswordAuthenticationToken secAuth = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(secAuth);
    }

    /**
     * Retrieve a user with the given email/username and password.
     *
     * @param emailOrUsername    email or username
     * @param password password
     * @return the user or null if none could be found
     */
    public User getUser(String emailOrUsername, String password) {
        LOG.debug("Retrieving user from database. user={}", emailOrUsername);
        return userRepository.findByEmailOrUsernameAndPassword(emailOrUsername, password);
    }


    public User getUser(Long id) {
        return userRepository.findById(id);
    }

    public Iterable<User> getUserList() {
        return userRepository.findAll();
    }

    public void addUser(User user) {
        userRepository.save(user);
    }
}
