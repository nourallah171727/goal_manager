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
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@PathVariable Long task_id, @RequestParam("file") MultipartFile file) {
        String storedFilePath = uploadService.storeFile(file ,task_id);

        return ResponseEntity.ok("File uploaded successfully: " + storedFilePath);
    }
    @GetMapping("/{taskId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long taskId) {
        Resource resource = uploadService.downloadFile(taskId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
