package com.lanhcare.controller;

import com.lanhcare.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("test/media")
@RequiredArgsConstructor
public class MediaController {
    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public CompletableFuture<ResponseEntity<String>> uploadMedia(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return cloudinaryService.uploadFile(file)
                .thenApply(ResponseEntity::ok);
    }


}
