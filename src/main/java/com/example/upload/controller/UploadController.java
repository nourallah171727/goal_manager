package com.example.upload.controller;

import com.example.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RequestMapping("/files")
@RestController
public class UploadController {
    private final UploadService uploadService;
    @Autowired
    public UploadController(UploadService uploadService){
        this.uploadService=uploadService;
    }
    @PostMapping("/upload/{taskId}")
    public ResponseEntity<String> uploadFile(@PathVariable Long taskId, @RequestParam("file") MultipartFile file) {
        String storedFilePath = uploadService.storeFile(file ,taskId);

        return ResponseEntity.ok("File uploaded successfully: " + storedFilePath);
    }
    @GetMapping("{userId}/download/{taskId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long userId ,@PathVariable Long taskId) {
        Resource resource = uploadService.downloadFile(userId,taskId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
