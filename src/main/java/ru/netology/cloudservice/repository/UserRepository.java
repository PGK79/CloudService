package ru.netology.cloudservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudservice.entity.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByLoginAndPassword(String login, String password);

    Optional<UserEntity> findUserByAuthToken(String authToken);

    UserEntity findByLogin(String login);
}
