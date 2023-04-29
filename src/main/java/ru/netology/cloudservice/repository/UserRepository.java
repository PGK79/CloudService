package ru.netology.cloudservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.netology.cloudservice.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginAndPassword(String login, String password);

    Optional<User> findUserByAuthToken(String authToken);

    @Query("select u from User u where u.authToken = :token")
    Optional<User> findUser(@Param("token") String name);



}
