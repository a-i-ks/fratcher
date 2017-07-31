package de.iske.fratcher.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface UserRepository extends CrudRepository<User, Long> {
    @Query("SELECT u FROM User_ u WHERE u.email = :email")
    User findByEmail(@Param("email") String email);

    @Query("SELECT u from User_ u WHERE u.email = :email AND u.password = :password")
    User findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    @Query("SELECT u from User_ u WHERE u.id = :id")
    User findById(@Param("id") Long id);

    @Query("SELECT u from User_ u")
    Stream<User> getAllUser();
}
