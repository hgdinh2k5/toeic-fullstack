package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "course_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(nullable = false)
    private String categoryName;
    private String iconUrl;          // icon danh mục từ Cloudinary
    private String courseImgUrl;     // ảnh đại diện danh mục từ Cloudinary
    @Column(unique = true)
    private String slug;             // VD: "luyen-nghe", "ngu-phap" (dùng cho URL)
    @Column(columnDefinition = "TEXT")
    private String description;
    // 1 category có nhiều course
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Course> courses;
}
