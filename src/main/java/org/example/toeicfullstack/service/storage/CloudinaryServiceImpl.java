package org.example.toeicfullstack.service.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloundinaryService {

    private final Cloudinary cloudinary;

    @Override
    public Map<String, Object> uploadImage(MultipartFile file) throws IOException {
        return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "image",
                "folder", "toeic/images"
        ));
    }

    @Override
    public Map<String, Object> uploadVideo(MultipartFile file) throws IOException {
        return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "video",
                "folder", "toeic/videos"
        ));
    }

    @Override
    public Map<String, Object> uploadAudio(MultipartFile file) throws IOException {
        return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "video",
                "folder", "toeic/audios"
        ));
    }

    @Override
    public void deleteFile(String publicId, String resourceType) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
                "resource_type", resourceType
        ));
    }
}
