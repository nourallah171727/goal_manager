package com.example.upload.service;

import com.example.ranking.repo.UserScorePairRepository;
import com.example.task.entity.Task;
import com.example.task.repo.TaskRepository;
import com.example.task.service.TaskService;
import com.example.upload.repo.UploadRepository;
import com.example.upload.entity.Upload;
import com.example.upload.entity.UploadId;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Transactional
public class UploadService {
    private final UploadRepository uploadRepository;
    private final TaskRepository taskRepository;
    private final UserScorePairRepository userScorePairRepository;
    private final UserRepository userRepository;
    private final TaskService taskService;


    private final Path rootLocation;
    @Autowired
    public UploadService(@Value("${file.upload-dir}") String uploadDir,UploadRepository uploadRepository, TaskRepository taskRepository, UserScorePairRepository userScorePairRepository, UserRepository userRepository,TaskService taskService) {
        this.uploadRepository = uploadRepository;
        this.taskRepository = taskRepository;
        this.userScorePairRepository = userScorePairRepository;
        this.userRepository = userRepository;
        this.taskService=taskService;
        this.rootLocation = Paths.get(uploadDir);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload directory", e);
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("User not found"));
    }

    public String storeFile(MultipartFile file,  Long taskId){
        Task task=taskRepository.findById(taskId).orElseThrow(()->new IllegalArgumentException("task not found"));
        User user=getCurrentUser();
        if(!task.getGoal().getMembers().contains(user)){
            throw new AccessDeniedException("not authorized to upload a file , for a goal's task u did not join");
        }
        List<String> allowedTypes = List.of("application/pdf", "image/jpeg", "image/png", "video/mp4");

        if (!allowedTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException("Unsupported file type");
        }
        String fileName = user.getId() + "_" + taskId + "_"
                + System.currentTimeMillis() + "_"
                + sanitizeFileName(file.getOriginalFilename());

        Path destination = rootLocation.resolve(fileName);

        try {
            Files.copy(file.getInputStream(), destination);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }

        String strDestination=destination.toString();
        Upload upload=new Upload(user.getId(),taskId,strDestination,0);
        uploadRepository.save(upload);

        return strDestination;
    }

    public Resource downloadFile(Long taskId){
        Task task=taskRepository.findById(taskId).orElseThrow(()->new IllegalArgumentException("task not found"));
        User user=getCurrentUser();
        if(!task.getGoal().getMembers().contains(user)){
            throw new AccessDeniedException("not authorized to see a file , for a goal's task u did not join");
        }
        Upload upload=uploadRepository.findById(new UploadId(user.getId(),taskId))
                .orElseThrow(()->new IllegalArgumentException("either task or user are not found"));
        return new FileSystemResource(Path.of(upload.getFilePath()));
    }

    private String sanitizeFileName(String original) {
        return original.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
    }



}
