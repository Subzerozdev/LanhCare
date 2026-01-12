package com.lanhcare.service.admin;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lanhcare.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminCloudinaryService {
    private static final Logger log = LoggerFactory.getLogger(AdminCloudinaryService.class);

    private final Cloudinary cloudinary;

    // Allowed image types
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    // Allowed video types
    private static final List<String> ALLOWED_VIDEO_TYPES = List.of(
            "video/mp4", "video/mpeg", "video/quicktime", "video/webm"
    );

    // Max file sizes (in bytes)
    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024; // 100MB

    public AdminCloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Upload a single image to Cloudinary
     * @param file MultipartFile to upload
     * @param folder Folder path in Cloudinary (e.g., "lanhcare/food-items")
     * @return URL of the uploaded image
     */
    public String uploadImage(MultipartFile file, String folder) {
        validateImageFile(file);
        return uploadFile(file, folder, "image");
    }

    /**
     * Upload a single video to Cloudinary
     * @param file MultipartFile to upload
     * @param folder Folder path in Cloudinary
     * @return URL of the uploaded video
     */
    public String uploadVideo(MultipartFile file, String folder) {
        validateVideoFile(file);
        return uploadFile(file, folder, "video");
    }

    /**
     * Upload multiple images to Cloudinary
     * @param files List of MultipartFile to upload
     * @param folder Folder path in Cloudinary
     * @return List of URLs of uploaded images
     */
    public List<String> uploadImages(List<MultipartFile> files, String folder) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(uploadImage(file, folder));
        }
        return urls;
    }

    /**
     * Upload a file (image or video) to Cloudinary
     */
    @SuppressWarnings("unchecked")
    private String uploadFile(MultipartFile file, String folder, String resourceType) {
        try {
            // Generate unique public ID
            String publicId = folder + "/" + UUID.randomUUID().toString();

            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", folder,
                    "resource_type", resourceType,
                    "overwrite", true
            );

            // Add image-specific transformations
            if ("image".equals(resourceType)) {
                uploadParams.put("transformation", ObjectUtils.asMap(
                        "quality", "auto:good",
                        "fetch_format", "auto"
                ));
            }

            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            String url = (String) result.get("secure_url");
            log.info("File uploaded successfully to Cloudinary: {}", url);

            return url;

        } catch (IOException e) {
            log.error("Failed to upload file to Cloudinary", e);
            throw new BadRequestException("Failed to upload file: " + e.getMessage());
        }
    }

    /**
     * Delete a file from Cloudinary by URL
     * @param url The Cloudinary URL of the file to delete
     * @return true if deleted successfully
     */
    @SuppressWarnings("unchecked")
    public boolean deleteByUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }

        try {
            // Extract public ID from URL
            String publicId = extractPublicIdFromUrl(url);
            if (publicId == null) {
                log.warn("Could not extract public ID from URL: {}", url);
                return false;
            }

            Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String resultStatus = (String) result.get("result");

            boolean success = "ok".equals(resultStatus);
            if (success) {
                log.info("File deleted from Cloudinary: {}", publicId);
            } else {
                log.warn("Failed to delete file from Cloudinary: {}", result);
            }

            return success;

        } catch (IOException e) {
            log.error("Failed to delete file from Cloudinary", e);
            return false;
        }
    }

    /**
     * Delete multiple files from Cloudinary
     * @param urls List of URLs to delete
     */
    public void deleteByUrls(List<String> urls) {
        for (String url : urls) {
            deleteByUrl(url);
        }
    }

    /**
     * Validate image file
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Invalid image type. Allowed: JPEG, PNG, GIF, WebP");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BadRequestException("Image size exceeds maximum allowed (10MB)");
        }
    }

    /**
     * Validate video file
     */
    private void validateVideoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Invalid video type. Allowed: MP4, MPEG, MOV, WebM");
        }

        if (file.getSize() > MAX_VIDEO_SIZE) {
            throw new BadRequestException("Video size exceeds maximum allowed (100MB)");
        }
    }

    /**
     * Extract public ID from Cloudinary URL
     * Example URL: https://res.cloudinary.com/djtdg8r8p/image/upload/v1234567890/lanhcare/food-items/abc123.jpg
     * Returns: lanhcare/food-items/abc123
     */
    private String extractPublicIdFromUrl(String url) {
        try {
            // Find the upload/ part and extract everything after version number
            int uploadIndex = url.indexOf("/upload/");
            if (uploadIndex == -1) {
                return null;
            }

            String path = url.substring(uploadIndex + 8); // Skip "/upload/"

            // Skip version if present (starts with 'v' followed by numbers)
            if (path.startsWith("v") && path.contains("/")) {
                int versionEnd = path.indexOf("/");
                path = path.substring(versionEnd + 1);
            }

            // Remove file extension
            int lastDot = path.lastIndexOf(".");
            if (lastDot > 0) {
                path = path.substring(0, lastDot);
            }

            return path;

        } catch (Exception e) {
            log.warn("Failed to extract public ID from URL: {}", url, e);
            return null;
        }
    }
}
