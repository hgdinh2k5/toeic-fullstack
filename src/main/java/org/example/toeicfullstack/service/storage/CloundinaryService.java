package org.example.toeicfullstack.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloundinaryService {
    Map<String, Object> uploadImage(MultipartFile file) throws IOException;
    Map<String, Object> uploadVideo(MultipartFile file) throws IOException;
    Map<String, Object> uploadAudio(MultipartFile file) throws IOException;
    void deleteFile(String publicId, String resourceType) throws IOException;
}
