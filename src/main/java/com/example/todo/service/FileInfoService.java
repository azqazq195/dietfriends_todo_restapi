package com.example.todo.service;

import com.example.todo.dto.FileInfoDto;
import com.example.todo.entity.FileInfo;
import com.example.todo.exception.ApiException;
import com.example.todo.exception.ErrorCode;
import com.example.todo.repository.FileInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileInfoService {

    @Value("${config.volume}")
    private String volumePath;
    private final FileInfoRepository fileInfoRepository;
    private final UserService userService;

    public List<FileInfo> multipartsToFileInfos(MultipartFile[] multipartFiles) {
        List<FileInfo> fileInfos = new ArrayList<>();
        if (multipartFiles == null) {
            return fileInfos;
        }

        for (MultipartFile multipartFile : multipartFiles) {
            fileInfos.add(multipartToFileInfo(multipartFile));
        }
        return fileInfos;
    }

    public FileInfo multipartToFileInfo(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new ApiException(ErrorCode.FAILED_UPLOAD_FILE);
        }
        File file = new File(
                volumePath +
                        UUID.randomUUID() +
                        new Date().getTime());
        try {
            multipartFile.transferTo(file);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.FAILED_UPLOAD_FILE);
        }

        return FileInfo.builder()
                .name(multipartFile.getName())
                .size(file.length())
                .path(file.getPath())
                .user(userService.requestedUser())
                .build();
    }

    public List<FileInfo> findAllById(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return null;
        }
        return fileInfoRepository.findAllById(ids);
    }
}
