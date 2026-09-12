package com.pulsedrive.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            Cloudinary cloudinary) {

        this.cloudinary = cloudinary;
    }

        public Map<String, Object> uploadImage(
            MultipartFile file)
            throws IOException {

                if (!isConfigured()) {
                    throw new IllegalStateException(
                            "Cloudinary is not configured. Set CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, and CLOUDINARY_API_SECRET."
                    );
                }

        if (file == null ||
                file.isEmpty()) {

            throw new IllegalArgumentException(
                    "Image file is required"
            );
        }

        String contentType =
                file.getContentType();

        if (contentType == null ||
                !contentType.startsWith("image/")) {

            throw new IllegalArgumentException(
                    "Only image files are allowed"
            );
        }

        Map<?, ?> uploadResult = cloudinary
                .uploader()
                .upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "folder",
                                "pulsedrive/vehicles",
                                "resource_type",
                                "image"
                        )
                );

                Map<String, Object> typedResult = new HashMap<>();
                uploadResult.forEach((key, value) -> {
                        if (key instanceof String) {
                                typedResult.put((String) key, value);
                        }
                });

                return typedResult;
    }

        private boolean isConfigured() {
                String cloudName = cloudinary.config.cloudName;
                String apiKey = cloudinary.config.apiKey;
                String apiSecret = cloudinary.config.apiSecret;
                return cloudName != null && !cloudName.isBlank()
                                && apiKey != null && !apiKey.isBlank()
                                && apiSecret != null && !apiSecret.isBlank();
        }

    public void deleteImage(
            String publicId)
            throws IOException {

        if (publicId == null ||
                publicId.isBlank()) {

            throw new IllegalArgumentException(
                    "Cloudinary public id is required"
            );
        }

        cloudinary
                .uploader()
                .destroy(
                        publicId,
                        ObjectUtils.emptyMap()
                );
    }
}