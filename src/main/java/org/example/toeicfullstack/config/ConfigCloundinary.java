package org.example.toeicfullstack.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConfigCloundinary {

    @Bean
    public Cloudinary Cloudinary() {
        Map<String, String> config = new HashMap<String, String>();
        config.put("cloud_name", "dybz8iuyt");
        config.put("api_key", "934197112982238");
        config.put("api_secret", "qu3OvcrmNO-hd1-7b_0r6bP1Gx8");
        return new Cloudinary(config);
    }

}
