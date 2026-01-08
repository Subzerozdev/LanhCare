package com.lanhcare.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface CloudinaryService {
    CompletableFuture<String> uploadFile(MultipartFile file) throws IOException;
}
