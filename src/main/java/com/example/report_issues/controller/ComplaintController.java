package com.example.report_issues.controller;


import com.example.report_issues.dto.ComplaintResponse;
import com.example.report_issues.dto.ComplaintRequest;
import com.example.report_issues.model.Complaint;
import com.example.report_issues.repository.ComplaintRepository;
import com.example.report_issues.service.ComplaintService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService service;
    private final ComplaintRepository repo;

    @Value("${server.port:8080}")
    private int serverPort;

    public ComplaintController(ComplaintService service, ComplaintRepository repo) {
        this.service = service;
        this.repo = repo;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> submitComplaint(@ModelAttribute ComplaintRequest req)
//            @RequestPart("name") String name,
//            @RequestPart(value = "email", required = false) String email,
//            @RequestPart(value = "phone", required = false) String phone,
//            @RequestPart("title") String title,
//            @RequestPart("description") String description,
//            @RequestPart(value = "latitude", required = false) Double latitude,
//            @RequestPart(value = "longitude", required = false) Double longitude,
//            @RequestPart(value = "address", required = false) String address,
//            @RequestPart(value = "image", required = false) MultipartFile image
    {
        try {
//            ComplaintRequest req = new ComplaintRequest();
//            req.setName(name);
//            req.setEmail(email);
//            req.setPhone(phone);
//            req.setTitle(title);
//            req.setDescription(description);
//            req.setLatitude(latitude);
//            req.setLongitude(longitude);
//            req.setAddress(address);
//            req.setImage(image);
            String trackingId = service.createComplaint(req);
            return ResponseEntity.ok().body(java.util.Map.of("trackingId", trackingId));
        } catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", iae.getMessage()));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Internal server error"));
        }
    }
    // GET by tracking id: public tracking endpoint
    @GetMapping("/{trackingId}")
    public ResponseEntity<?> getByTrackingId(@PathVariable String trackingId, @RequestHeader(value = "Host", required = false) String host) {
        Optional<Complaint> opt = repo.findByTrackingId(trackingId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(java.util.Map.of("error", "Not found"));
        }
        Complaint c = opt.get();
        ComplaintResponse resp = toResponse(c, host);
        return ResponseEntity.ok(resp);
    }

    // GET recent issues (limit via query param)
    @GetMapping("/recent")
    public ResponseEntity<?> getRecent(@RequestParam(value = "limit", defaultValue = "3") int limit,
                                       @RequestHeader(value = "Host", required = false) String host) {
        // using repository helper; if you want pageable later, replace
        List<Complaint> list = repo.findTop5ByOrderByCreatedAtDesc();
        // limit properly
        List<ComplaintResponse> out = list.stream()
                .limit(limit)
                .map(c -> toResponse(c, host))
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    // helper: build response and full image URL if available
    private ComplaintResponse toResponse(Complaint c, String hostHeader) {
        ComplaintResponse r = new ComplaintResponse();
        r.setTrackingId(c.getTrackingId());
        r.setName(c.getName());
        r.setTitle(c.getTitle());
        r.setDescription(c.getDescription());
        r.setStatus(c.getStatus());
        r.setLatitude(c.getLatitude());
        r.setLongitude(c.getLongitude());
        r.setAddress(c.getAddress());
        r.setAdminNotes(c.getAdminNotes());
        r.setCreatedAt(c.getCreatedAt());
        r.setUpdatedAt(c.getUpdatedAt());
        r.setEmail(c.getEmail());
        r.setPhone(c.getPhone());

        if (c.getImagePath() != null && !c.getImagePath().isBlank()) {
            // build a simple image URL: http://{host}/uploads/{filename}
            // hostHeader may be null in some test contexts; fallback to localhost:8080
            String host = (hostHeader == null || hostHeader.isBlank()) ? ("localhost:" + serverPort) : hostHeader;
            String scheme = "http://";
            r.setImageUrl(scheme + host + "/uploads/" + c.getImagePath());
        } else {
            r.setImageUrl(null);
        }
        return r;
    }
}
