package com.example.todo.controller;

import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.dto.SignInRequestDto;
import com.example.todo.dto.SignUpRequestDto;
import com.example.todo.dto.TokenDto;
import com.example.todo.entity.Todo;
import com.example.todo.repository.TodoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import javax.transaction.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("local")
class TodoControllerTest {

    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    private static final String BASE_URL = "/todos";
    @Value("${config.apikey}")
    private String apiKey;
    private TokenDto tokenDto;

    @BeforeEach
    void setUp() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                new SignUpRequestDto(
                        "name",
                        "email",
                        "password",
                        1
                )
        );
        mockMvc.perform(
                post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andDo(print());
        requestBody = objectMapper.writeValueAsString(
                new SignInRequestDto(
                        "email",
                        "password"
                )
        );

        String responseBody = mockMvc.perform(
                post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        tokenDto = new Gson().fromJson(responseBody, TokenDto.class);
    }

    @Test
    @Transactional
    void create() throws Exception {
        String request = objectMapper.writeValueAsString(
                new CreateTodoRequest(
                        "TODO",
                        false
                )
        );
        MockMultipartFile requestFile = new MockMultipartFile(
                "request",
                "",
                "application/json",
                request.getBytes(StandardCharsets.UTF_8)
        );

        // Without Token
        mockMvc.perform(
                multipart(BASE_URL)
                .file(requestFile))
                .andExpect(status().is4xxClientError());

        // Without ApiKey
        mockMvc.perform(
                multipart(BASE_URL)
                .file(requestFile)
                .header("Authorization", tokenDto.getAccessToken()))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(
                multipart(BASE_URL)
                .file(requestFile)
                .param("apikey", apiKey)
                .header("Authorization", tokenDto.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void retrieve() throws Exception {
        create();
        List<Todo> todoList = todoRepository.findAll();
        assertEquals(todoList.size(), 1);

        mockMvc.perform(
                get(BASE_URL + "/" + todoList.get(0).getId()))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(
                get(BASE_URL + "/" + todoList.get(0).getId())
                .header("Authorization", tokenDto.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void list() throws Exception {
        mockMvc.perform(
                get(BASE_URL))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(
                get(BASE_URL)
                .param("limit", "10")
                .header("Authorization", tokenDto.getAccessToken()))
                .andExpect(status().isOk());

        mockMvc.perform(
                get(BASE_URL)
                .param("limit", "10")
                .param("page", "10")
                .header("Authorization", tokenDto.getAccessToken()))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void update() throws Exception {
        String request = objectMapper.writeValueAsString(
                new CreateTodoRequest(
                        "UPDATE",
                        true
                )
        );
        MockMultipartFile requestFile = new MockMultipartFile(
                "request",
                "",
                "application/json",
                request.getBytes(StandardCharsets.UTF_8)
        );
        create();
        List<Todo> todoList = todoRepository.findAll();
        // Without Token
        mockMvc.perform(
                multipart(BASE_URL + "/" + todoList.get(0).getId())
                .file(requestFile)
                .with(new RequestPostProcessor() {
                    @Override
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        request.setMethod("PUT");
                        return request;
                    }
                }))
                .andExpect(status().is4xxClientError());

        // Without ApiKey
        mockMvc.perform(
                multipart(BASE_URL + "/" + todoList.get(0).getId())
                .file(requestFile)
                .with(new RequestPostProcessor() {
                    @Override
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        request.setMethod("PUT");
                        return request;
                    }
                })
                .header("Authorization", tokenDto.getAccessToken()))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(
                multipart(BASE_URL + "/" + todoList.get(0).getId())
                .file(requestFile)
                .param("apikey", apiKey)
                .header("Authorization", tokenDto.getAccessToken())
                .with(new RequestPostProcessor() {
                    @Override
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        request.setMethod("PUT");
                        return request;
                    }
                }))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void delete() throws Exception {
        create();
        List<Todo> todoList = todoRepository.findAll();

        mockMvc.perform(
                MockMvcRequestBuilders.delete(BASE_URL + "/" + todoList.get(0).getId()))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(
                MockMvcRequestBuilders.delete(BASE_URL + "/" + todoList.get(0).getId())
                .header("Authorization", tokenDto.getAccessToken()))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(
                MockMvcRequestBuilders.delete(BASE_URL + "/" + todoList.get(0).getId())
                .param("apikey", apiKey)
                .header("Authorization", tokenDto.getAccessToken()))
                .andExpect(status().isOk());
    }
}