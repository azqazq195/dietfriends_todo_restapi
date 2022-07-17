package com.example.todo.service;

import com.example.todo.dto.*;
import com.example.todo.entity.Todo;
import com.example.todo.entity.User;
import com.example.todo.exception.ApiException;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("local")
class TodoServiceTest {

    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private TodoService todoService;
    @Autowired
    private UserService userService;
    private MockMultipartFile mockMultipartFile;
    private CreateTodoRequest createTodoRequest;
    private UpdateTodoRequest updateTodoRequest;

    @BeforeEach
    void setUp() throws IOException {
        String fileName = "test";
        String fileType = "png";
        String filePath = "src/test/resources/file/test.png";
        FileInputStream fileInputStream = new FileInputStream(new File(filePath));
        mockMultipartFile = new MockMultipartFile(
                fileName,
                fileName + "." + fileType,
                fileType,
                fileInputStream
        );

        User user = userService.create(User.builder()
                .name("seongha")
                .email("mail@mail.com")
                .password("qwe")
                .age(1)
                .build());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities())
        );

        createTodoRequest = new CreateTodoRequest(
                "TODO",
                false
        );

        updateTodoRequest = new UpdateTodoRequest(
                "UPDATE TODO",
                true,
                null
        );
    }

    @Test
    @Transactional
    void create() {
        TodoFullDto todoFullDto = todoService.create(createTodoRequest, new MultipartFile[] {mockMultipartFile});
        assertTrue(todoFullDto.getId() > 0);
        assertEquals(1, todoFullDto.getFileInfos().size());
        assertEquals(todoFullDto.getName(), createTodoRequest.getName());
        assertEquals(todoFullDto.isCompleted(), createTodoRequest.getCompleted());
    }

    @Test
    @Transactional
    void update() {
        TodoFullDto storedValue = todoService.create(createTodoRequest, new MultipartFile[] {mockMultipartFile});
        // check updated values, without file
        todoService.update(storedValue.getId(), updateTodoRequest, null);
        TodoFullDto checkValue = todoService.retrieve(storedValue.getId());
        assertEquals(checkValue.getName(), updateTodoRequest.getName());
        assertEquals(checkValue.isCompleted(), updateTodoRequest.getCompleted());
        assertEquals(checkValue.getFileInfos(), storedValue.getFileInfos());

        // check updated values, add file
        todoService.update(storedValue.getId(), updateTodoRequest, new MultipartFile[] {mockMultipartFile});
        checkValue = todoService.retrieve(storedValue.getId());
        assertEquals(checkValue.getName(), updateTodoRequest.getName());
        assertEquals(checkValue.isCompleted(), updateTodoRequest.getCompleted());
        assertTrue(checkValue.getFileInfos().size() > storedValue.getFileInfos().size());

        // check updated values, update file
        storedValue = todoService.retrieve(storedValue.getId());
        List<FileInfoDto> storedFiles = new ArrayList<>(storedValue.getFileInfos());
        System.out.println(storedValue.getFileInfos());
        assertEquals(storedFiles.size(), 2);
        storedFiles.remove(0);
        List<Integer> storedFileIds = storedFiles.stream().map(FileInfoDto::getId).collect(Collectors.toList());
        updateTodoRequest.setFileInfoIds(storedFileIds);
        todoService.update(storedValue.getId(), updateTodoRequest, new MultipartFile[] {mockMultipartFile});
        checkValue = todoService.retrieve(storedValue.getId());
        assertEquals(checkValue.getName(), updateTodoRequest.getName());
        assertEquals(checkValue.isCompleted(), updateTodoRequest.getCompleted());
        assertEquals(checkValue.getFileInfos().size(), storedValue.getFileInfos().size());
        assertNotEquals(checkValue.getFileInfos(), storedValue.getFileInfos());
    }

    @Test
    @Transactional
    void delete() {
        TodoFullDto todoFullDto = todoService.create(createTodoRequest, new MultipartFile[] {mockMultipartFile});
        todoService.delete(todoFullDto.getId());
        assertEquals(todoRepository.findAll().size(), 0);
    }

    @Test
    @Transactional
    void retrieve() {
        TodoFullDto storedValue = todoService.create(createTodoRequest, new MultipartFile[] {mockMultipartFile});
        TodoFullDto checkValue = todoService.retrieve(storedValue.getId());
        assertEquals(checkValue.getId(), storedValue.getId());
        assertEquals(checkValue.getName(), storedValue.getName());
        assertEquals(checkValue.isCompleted(), storedValue.isCompleted());
    }

    @Test
    @Transactional
    void list() {
        for (int i = 0 ; i < 10; i++) {
            todoService.create(createTodoRequest, new MultipartFile[] {mockMultipartFile});
        }

        List<TodoPartialDto> todoPartialDtoList = todoService.list(PageRequest.of(0, 10));
        assertEquals(todoPartialDtoList.size(), 10);
        todoPartialDtoList = todoService.list(PageRequest.of(0, 5));
        assertEquals(todoPartialDtoList.size(), 5);
    }

    @Test
    @Transactional
    void checkExists() {
        TodoFullDto todoFullDto = todoService.create(createTodoRequest, new MultipartFile[] {mockMultipartFile});
        todoService.checkExists(todoFullDto.getId());
        assertThrows(ApiException.class, () -> todoService.checkExists(-1));
    }

    @Test
    @Transactional
    void checkOwner() {
        User anotherUser = userService.create(
                User.builder()
                        .name("seongha2")
                        .email("mail@mail.com2")
                        .password("qwe")
                        .age(1)
                        .build()
        );
        User requestedUser = userService.requestedUser();
        Todo todo = todoRepository.save(createTodoRequest.toEntity(requestedUser));
        todoService.checkOwner(todo, requestedUser.getId());
        assertThrows(ApiException.class, () -> todoService.checkOwner(todo, anotherUser.getId()));
    }
}