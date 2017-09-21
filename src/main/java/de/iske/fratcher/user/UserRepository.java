package de.iske.fratcher.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends CrudRepository<User, Long> {
    @Query("SELECT u FROM User_ u WHERE u.email = :email")
    User findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User_ u WHERE u.username = :username")
    User findbyUserName(@Param("username") String username);

    @Query("SELECT u from User_ u WHERE (u.email = :emailOrUsername OR u.username = :emailOrUsername) AND u.password = :password")
    User findByEmailOrUsernameAndPassword(@Param("emailOrUsername") String emailOrUsername, @Param("password") String password);

    @Query("SELECT u from User_ u WHERE u.id = :id")
    User findById(@Param("id") Long id);

    Iterable<User> findAll();

}
