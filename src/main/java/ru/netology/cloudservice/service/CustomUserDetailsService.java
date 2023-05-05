package ru.netology.cloudservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netology.cloudservice.entity.UserEntity;
import ru.netology.cloudservice.exception.AuthorizeException;
import ru.netology.cloudservice.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomUserDetailsService(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByLogin(userName);
        if (userEntity == null) {
            throw new AuthorizeException("Введенные данные пользователя не верны");
        }
        UserDetails user = User.builder()
                .username(userEntity.getLogin())
                .password(userEntity.getPassword())
                .roles("")
                .build();
        return user;
    }
}
