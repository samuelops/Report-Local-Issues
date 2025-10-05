package com.example.report_issues.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints", indexes = {
        @Index(name = "idx_tracking_id", columnList = "tracking_id")
})
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_id", nullable = false, unique = true, length = 64)
    private String trackingId;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 120)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "image_path", length = 500)
    private String imagePath;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ComplaintStatus status = ComplaintStatus.SUBMITTED;

    @Column(columnDefinition = "DOUBLE")
    private Double latitude;

    @Column(columnDefinition = "DOUBLE")
    private Double longitude;

    @Column(length = 400)
    private String address;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Complaint() {}

    // Basic constructor for convenience
    public Complaint(String trackingId, String name, String email, String phone,
                     String title, String description, String imagePath,
                     Double latitude, Double longitude, String address) {
        this.trackingId = trackingId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters (generate these in your IDE if you prefer)
    public Long getId() { return id; }
    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
