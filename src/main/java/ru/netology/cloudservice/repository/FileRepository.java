package ru.netology.cloudservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudservice.entity.FileEntity;
import ru.netology.cloudservice.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    Optional<FileEntity> findFileByNameAndUser(String filename, User user);

    List<FileEntity> findAllFilesByUser(User user, Pageable pageable);


}