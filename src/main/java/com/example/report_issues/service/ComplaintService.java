package com.example.report_issues.service;

import com.example.report_issues.dto.ComplaintRequest;
import com.example.report_issues.model.Complaint;
import com.example.report_issues.repository.ComplaintRepository;
import com.example.report_issues.util.TrackingIdGenerator;
import com.example.report_issues.util.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ComplaintService {

    private final ComplaintRepository repo;
    private final FileStorageService fileStorage;
    private final EmailService emailService;

    public ComplaintService(ComplaintRepository repo, FileStorageService fileStorage, EmailService emailService) {
        this.repo = repo;
        this.fileStorage = fileStorage;
        this.emailService = emailService;
    }

    @Transactional
    public String createComplaint(ComplaintRequest req) throws Exception {
        // basic validation
        if (req.getName() == null || req.getName().isBlank()) throw new IllegalArgumentException("Name is required");
        if (req.getTitle() == null || req.getTitle().isBlank()) throw new IllegalArgumentException("Title is required");
        if (req.getDescription() == null || req.getDescription().isBlank()) throw new IllegalArgumentException("Description is required");

        String savedFileName = null;
        if (req.getImage() != null && !req.getImage().isEmpty()) {
            savedFileName = fileStorage.saveImage(req.getImage());
        }

        String trackingId;
        // ensure uniqueness (rare collision but safe)
        do {
            trackingId = TrackingIdGenerator.generate();
        } while (repo.findByTrackingId(trackingId).isPresent());

        Complaint c = new Complaint();
        c.setTrackingId(trackingId);
        c.setName(req.getName());
        c.setEmail(req.getEmail());
        c.setPhone(req.getPhone());
        c.setTitle(req.getTitle());
        c.setDescription(req.getDescription());
        c.setImagePath(savedFileName != null ? savedFileName : null);
        c.setLatitude(req.getLatitude());
        c.setLongitude(req.getLongitude());
        c.setAddress(req.getAddress());
        c.setStatus(com.example.report_issues.model.ComplaintStatus.SUBMITTED);
        c.setCreatedAt(LocalDateTime.now());

        repo.save(c);
        // after saving, attempt to send acknowledgement (async)
        if (c.getEmail() != null && !c.getEmail().isBlank()) {
            try {
                emailService.sendComplaintAcknowledgement(c.getEmail(), c.getName(), c.getTrackingId(), c.getTitle(), c.getDescription());
            } catch (Exception e) {
                // swallow to not fail the API; log
                System.err.println("Email send error: " + e.getMessage());
            }
        }
        return trackingId;
    }
}
