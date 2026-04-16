package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.toeicfullstack.entity.enums.TypeNotification;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(nullable = false)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Enumerated(EnumType.STRING)
    private TypeNotification type;
    private int durationInDays;        // thời hạn hiển thị (ngày)
    private String targetUrl;          // đường dẫn khi bấm vào thông báo
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Users createdBy;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserNotification> userNotifications;

}
