package com.example.todo.controller;

import com.example.todo.dto.SignInRequestDto;
import com.example.todo.dto.SignUpRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("local")
class AuthenticationControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    private static final String BASE_URL = "/auth";

    @Test
    @Transactional
    void signUp() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                new SignUpRequestDto(
                        "name",
                        "email",
                        "password",
                        1
                )
        );
        mockMvc.perform(
                post(BASE_URL + "/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Transactional
    void signIn() throws Exception {
        signUp();
        String requestBody = objectMapper.writeValueAsString(
                new SignInRequestDto(
                        "email",
                        "password"
                )
        );

        mockMvc.perform(
                post(BASE_URL + "/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andDo(print());
    }
}