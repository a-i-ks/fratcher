package de.iske.fratcher.user;

import de.iske.fratcher.util.Status;
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
     * @param emailOrUsername email or username
     * @param password        password
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
        if (!isUserProfileComplete(user)) {
            user.setStatus(Status.INACTIVE);
        } else {
            user.setStatus(Status.DEFAULT);
        }
        userRepository.save(user);
        LOG.info("[ADDED] {}", user);
    }

    /**
     * Update a existing user object. All non null values will be replaced with values from userToMerge.
     * <p>
     * User status and type cannot be updated with this method.
     *
     * @param userToMerge user object containing new values for user
     */
    public void mergeUser(User userToMerge) {
        User userObj = userRepository.findById(userToMerge.getId());
        if (!isUserProfileComplete(userToMerge)) {
            userToMerge.setStatus(Status.INACTIVE);
            LOG.info("[INACTIVE] {}", userToMerge);
        } else {
            LOG.info("[ACTIVE] {}", userToMerge);
            userToMerge.setStatus(Status.DEFAULT);
        }
        // merge user information
        if (userToMerge.getUsername() != null) {
            userObj.setUsername(userToMerge.getUsername());
        }
        if (userToMerge.getPassword() != null &&
                userToMerge.getPassword().length() > 0) {
            userObj.setPassword(userToMerge.getPassword());
        }
        if (userToMerge.getEmail() != null) {
            userObj.setEmail(userToMerge.getEmail());
        }
        // merge profile information
        if (userToMerge.getProfile() == null) {
            userRepository.save(userObj);
            return;
        }
        if (userToMerge.getProfile().getName() != null) {
            userObj.getProfile().setName(userToMerge.getProfile().getName());
        }
        if (userToMerge.getProfile().getAboutMe() != null) {
            userObj.getProfile().setAboutMe(userToMerge.getProfile().getAboutMe());
        }
        if (userToMerge.getProfile().getInterests() != null) {
            userObj.getProfile().setInterests(userToMerge.getProfile().getInterests());
        }
        userRepository.save(userObj);
    }

    /**
     * Check if all necessary information have been added to the user profile
     * to participate in the matching process.
     *
     * @param user the user to check
     * @return true if all info are present
     */
    public boolean isUserProfileComplete(User user) {
        LOG.info("Checking {} for complete profile ...", user);
        if (user.getProfile() == null) {
            LOG.error("Profile is null for user {}", user);
            return false;
        } else if (user.getProfile().getName() == null) {
            LOG.info("No name for user {}", user);
            return false;
        } else if (user.getProfile().getName().length() == 0) {
            LOG.info("No name for user {}", user);
            return false;
        } else if (user.getProfile().getAboutMe() == null) {
            LOG.info("No about me for user {}", user);
            return false;
        }
        return true;
    }

    /**
     * Update the status of passed user to DELETED.
     *
     * @param userToDelete user object that should be set to deleted
     */
    public void deleteUser(User userToDelete) {
        User userObj = userRepository.findById(userToDelete.getId());
        userObj.setStatus(Status.DELETED);
        userRepository.save(userObj);
    }

    /**
     * Update the user type of an existing user to the passed user type.
     *
     * @param userToChange user object that type should be change
     * @param newUserType  the new type of the userToChange
     */
    public void setUserType(User userToChange, User.UserType newUserType) {
        User userObj = userRepository.findById(userToChange.getId());
        userObj.setUserType(newUserType);
        userRepository.save(userObj);
    }
}
