package org.example.toeicfullstack.service.storage;

import com.cloudinary.Cloudinary;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public class CloudinaryServiceImpl implements  CloundinaryService{
    private final Cloudinary cloudinary;


    @Override
    public Map<String, Object> uploadImage(MultipartFile file) throws IOException {
        return Map.of();
    }

    @Override
    public Map<String, Object> uploadVideo(MultipartFile file) throws IOException {
        return Map.of();
    }

    @Override
    public Map<String, Object> uploadAudio(MultipartFile file) throws IOException {
        return Map.of();
    }

    @Override
    public void deleteFile(String publicId, String resourceType) throws IOException {

    }
}
