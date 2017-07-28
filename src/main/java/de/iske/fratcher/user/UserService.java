package de.iske.fratcher.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
     * Retrieve a user with the given email and password.
     *
     * @param email    email
     * @param password password
     * @return the user or null if none could be found
     */
    public User getUser(String email, String password) {
        LOG.debug("Retrieving user from database. user={}", email);
        return userRepository.findByEmailAndPassword(email, password);
    }

    public User getUser(Long id) {
        return userRepository.findById(id);
    }

    public Iterable<User> getUserList() {
        return userRepository.getAllUser();
    }

    public void addUser(User user) {
        userRepository.save(user);
    }
}
