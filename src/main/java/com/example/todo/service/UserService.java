package com.example.todo.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.todo.entity.User;
import com.example.todo.exception.ApiException;
import com.example.todo.exception.ErrorCode;
import com.example.todo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional
    public User retrieve(int id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ApiException(ErrorCode.USER_NOT_FOUND)
        );
    }

    @Transactional
    public User retrieve(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ApiException(ErrorCode.USER_NOT_FOUND)
        );
    }

    @Transactional
    public User create(User user) {
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return retrieve(username);
    }

    @Transactional
    public User requestedUser() {
        return retrieve(requestedUserId());
    }

    @Transactional
    public int requestedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((User) authentication.getPrincipal()).getId();
    }

    public boolean isExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
