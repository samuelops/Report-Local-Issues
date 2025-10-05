package com.example.report_issues.controller;

import com.example.report_issues.dto.ComplaintRequest;
import com.example.report_issues.service.ComplaintService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService service;

    public ComplaintController(ComplaintService service) {
        this.service = service;
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
}
