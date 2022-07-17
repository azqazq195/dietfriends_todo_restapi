package com.example.todo.service;

import com.example.todo.entity.FileInfo;
import com.example.todo.entity.User;
import com.example.todo.repository.FileInfoRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("local")
class FileInfoServiceTest {

    @Autowired
    private FileInfoRepository fileInfoRepository;
    @Autowired
    private FileInfoService fileInfoService;
    @Autowired
    private UserService userService;
    private MockMultipartFile mockMultipartFile;

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

        for (int i = 0 ; i < 5; i++) {
            fileInfoRepository.save(FileInfo.builder()
                    .name("name")
                    .size(1)
                    .path("path")
                    .build());
        }
    }

    @Test
    @Transactional
    void multipartsToFileInfos() {
        List<FileInfo> fileInfoList = fileInfoService.multipartsToFileInfos(new MultipartFile[] {mockMultipartFile});
        assertEquals(1, fileInfoList.size());

        FileInfo fileInfo = fileInfoList.get(0);
        assertEquals(fileInfo.getName(), mockMultipartFile.getName());
    }

    @Test
    @Transactional
    void multipartToFileInfo() {
        FileInfo fileInfo = fileInfoService.multipartToFileInfo(mockMultipartFile);
        assertEquals(fileInfo.getName(), mockMultipartFile.getName());
    }

    @Test
    @Transactional
    void findAllById() {
        assertNull(fileInfoService.findAllById(null));
        assertNull(fileInfoService.findAllById(new ArrayList<>()));

        List<FileInfo> fileInfos = fileInfoRepository.findAll();
        List<Integer> fileInfoIds = new ArrayList<>();
        fileInfoIds.add(fileInfos.get(1).getId());
        fileInfoIds.add(fileInfos.get(3).getId());

        List<FileInfo> checkFileInfos = fileInfoService.findAllById(fileInfoIds);
        assertEquals(fileInfos.get(1).getId(), checkFileInfos.get(0).getId());
        assertEquals(fileInfos.get(3).getId(), checkFileInfos.get(1).getId());
    }
}