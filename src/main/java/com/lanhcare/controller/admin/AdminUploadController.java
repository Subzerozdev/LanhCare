package com.lanhcare.controller.admin;

import com.lanhcare.dto.admin.upload.UploadResponse;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Admin Upload Controller
 * Handles file upload operations for admin panel
 * All endpoints require ADMIN role
 */
@RestController
@RequestMapping("/api/admin/upload")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - File Upload", description = "Admin APIs for uploading images and videos")
public class AdminUploadController {
    
    private final CloudinaryService cloudinaryService;
    
    public AdminUploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }
    
    // ==================== FOOD ITEM IMAGES ====================
    
    /**
     * Upload a single image for food item
     */
    @PostMapping(value = "/food-item/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload food item image",
            description = "Upload a single image for a food item. Allowed formats: JPEG, PNG, GIF, WebP. Max size: 10MB"
    )
    public ResponseEntity<ApiResponse<UploadResponse>> uploadFoodItemImage(
            @Parameter(description = "Image file to upload", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("file") MultipartFile file) {
        
        String url = cloudinaryService.uploadImage(file, "lanhcare/food-items");
        UploadResponse response = UploadResponse.single(url, "IMAGE");
        
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", response));
    }
    
    // ==================== POST MEDIA ====================
    
    /**
     * Upload a single image for post
     */
    @PostMapping(value = "/post/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload post image",
            description = "Upload a single image for a post. Allowed formats: JPEG, PNG, GIF, WebP. Max size: 10MB"
    )
    public ResponseEntity<ApiResponse<UploadResponse>> uploadPostImage(
            @RequestParam("file") MultipartFile file) {
        
        String url = cloudinaryService.uploadImage(file, "lanhcare/posts");
        UploadResponse response = UploadResponse.single(url, "IMAGE");
        
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", response));
    }
    
    /**
     * Upload multiple images for post
     */
    @PostMapping(value = "/post/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload multiple post images",
            description = "Upload multiple images for a post. Max 10 images. Allowed formats: JPEG, PNG, GIF, WebP. Max size per image: 10MB"
    )
    public ResponseEntity<ApiResponse<UploadResponse>> uploadPostImages(
            @RequestParam("files") List<MultipartFile> files) {
        
        if (files.size() > 10) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Maximum 10 images allowed"));
        }
        
        List<String> urls = cloudinaryService.uploadImages(files, "lanhcare/posts");
        UploadResponse response = UploadResponse.multiple(urls, "IMAGE");
        
        return ResponseEntity.ok(ApiResponse.success("Images uploaded successfully", response));
    }
    
    /**
     * Upload a video for post
     */
    @PostMapping(value = "/post/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload post video",
            description = "Upload a video for a post. Allowed formats: MP4, MPEG, MOV, WebM. Max size: 100MB"
    )
    public ResponseEntity<ApiResponse<UploadResponse>> uploadPostVideo(
            @RequestParam("file") MultipartFile file) {
        
        String url = cloudinaryService.uploadVideo(file, "lanhcare/posts/videos");
        UploadResponse response = UploadResponse.single(url, "VIDEO");
        
        return ResponseEntity.ok(ApiResponse.success("Video uploaded successfully", response));
    }
    
    // ==================== COMMENT MEDIA ====================
    
    /**
     * Upload image for comment
     */
    @PostMapping(value = "/comment/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload comment image",
            description = "Upload an image for a comment. Allowed formats: JPEG, PNG, GIF, WebP. Max size: 10MB"
    )
    public ResponseEntity<ApiResponse<UploadResponse>> uploadCommentImage(
            @RequestParam("file") MultipartFile file) {
        
        String url = cloudinaryService.uploadImage(file, "lanhcare/comments");
        UploadResponse response = UploadResponse.single(url, "IMAGE");
        
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", response));
    }
    
    // ==================== GENERAL UPLOAD ====================
    
    /**
     * Upload a general image (for any purpose)
     */
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload general image",
            description = "Upload a general purpose image. Allowed formats: JPEG, PNG, GIF, WebP. Max size: 10MB"
    )
    public ResponseEntity<ApiResponse<UploadResponse>> uploadGeneralImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "lanhcare/general") String folder) {
        
        String url = cloudinaryService.uploadImage(file, folder);
        UploadResponse response = UploadResponse.single(url, "IMAGE");
        
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", response));
    }
    
    // ==================== DELETE ====================
    
    /**
     * Delete a file by URL
     */
    @DeleteMapping("/delete")
    @Operation(
            summary = "Delete uploaded file",
            description = "Delete a file from Cloudinary by its URL"
    )
    public ResponseEntity<ApiResponse<Void>> deleteFile(@RequestParam("url") String url) {
        boolean deleted = cloudinaryService.deleteByUrl(url);
        
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("File deleted successfully", null));
        } else {
            return ResponseEntity.ok(ApiResponse.error("Failed to delete file"));
        }
    }
}
