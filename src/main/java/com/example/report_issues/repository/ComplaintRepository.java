package com.example.report_issues.repository;

import com.example.report_issues.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findByTrackingId(String trackingId);

    // Recent issues (caller can pass Pageable normally; simple example)
    List<Complaint> findTop5ByOrderByCreatedAtDesc();
}
