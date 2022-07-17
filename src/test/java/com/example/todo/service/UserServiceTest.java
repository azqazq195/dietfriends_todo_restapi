package com.example.todo.service;

import com.example.todo.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("local")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @Transactional
    void retrieveById() {
        User user = User.builder()
                .name("seongha")
                .email("mail@mail.com")
                .password("qwe")
                .age(1)
                .build();
        User storedValue = userService.create(user);
        User checkValue = userService.retrieve(user.getId());
        assertEquals(checkValue.getId(), storedValue.getId());
    }

    @Test
    @Transactional
    void retrieveByEmail() {
        User user = User.builder()
                .name("seongha")
                .email("mail@mail.com")
                .password("qwe")
                .age(1)
                .build();
        User storedValue = userService.create(user);
        User checkValue = userService.retrieve(user.getEmail());
        assertEquals(checkValue.getId(), storedValue.getId());
    }

    @Test
    @Transactional
    void create() {
        User exceptionUser = User.builder()
                .name("seongha")
                .email("mail@mail.com")
                .password("qwe")
                .age(-1)
                .build();
        assertThrows(ValidationException.class, () -> userService.create(exceptionUser));

        User user = User.builder()
                .name("seongha")
                .email("mail@mail.com")
                .password("qwe")
                .age(1)
                .build();
        userService.create(user);
        assertTrue(user.getId() > 0);
    }

    // override method 로 username 이지만 사실 email 로 조회
    // retrieveByEmail 과 같다.
    @Test
    @Transactional
    void loadUserByUsername() {
        User user = User.builder()
                .name("seongha")
                .email("mail@mail.com")
                .password("qwe")
                .age(1)
                .build();
        User storedValue = userService.create(user);
        User checkValue = userService.loadUserByUsername(user.getEmail());
        assertEquals(checkValue.getId(), storedValue.getId());
    }

    @Test
    @Transactional
    void requestedUser() {
        User user = userService.create(User.builder()
                        .name("seongha")
                        .email("mail@mail.com")
                        .password("qwe")
                        .age(1)
                        .build());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities())
        );

        User storedValue = userService.retrieve(user.getId());
        User checkValue = userService.requestedUser();
        assertEquals(checkValue.getId(), storedValue.getId());
    }

    @Test
    @Transactional
    void requestedUserId() {
        User user = userService.create(User.builder()
                .name("seongha")
                .email("mail@mail.com")
                .password("qwe")
                .age(1)
                .build());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities())
        );

        int storedValue = user.getId();
        int checkValue = userService.requestedUserId();
        assertEquals(checkValue, storedValue);
    }

    @Test
    @Transactional
    void isExists() {
        String email = "mail@mail.com";

        assertFalse(userService.isExists(email));

        userService.create(User.builder()
                .name("seongha")
                .email(email)
                .password("qwe")
                .age(1)
                .build());

        assertTrue(userService.isExists(email));
    }
}