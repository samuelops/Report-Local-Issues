package com.example.report_issues.controller;

import com.example.report_issues.model.Complaint;
import com.example.report_issues.model.ComplaintStatus;
import com.example.report_issues.repository.ComplaintRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ComplaintRepository repo;

    public AdminController(ComplaintRepository repo) {
        this.repo = repo;
    }

    // List complaints (pageable)
    @GetMapping("/complaints")
    public String listComplaints(@RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                 Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Complaint> p = repo.findAll(pageable);
        model.addAttribute("page", p);
        return "admin/list";
    }

    // Detail view
    @GetMapping("/complaints/{id}")
    public String complaintDetail(@PathVariable Long id, Model model) {
        Optional<Complaint> opt = repo.findById(id);
        if (opt.isEmpty()) {
            model.addAttribute("error", "Complaint not found");
            return "admin/detail";
        }
        model.addAttribute("complaint", opt.get());
        model.addAttribute("statuses", ComplaintStatus.values());
        return "admin/detail";
    }

    // Update status & admin notes
    @PostMapping("/complaints/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam("status") ComplaintStatus status,
                               @RequestParam(value = "adminNotes", required = false) String adminNotes,
                               Model model) {
        Optional<Complaint> opt = repo.findById(id);
        if (opt.isEmpty()) {
            model.addAttribute("error", "Complaint not found");
            return "admin/detail";
        }
        Complaint c = opt.get();
        c.setStatus(status);
        c.setAdminNotes(adminNotes);
        c.setUpdatedAt(LocalDateTime.now());
        repo.save(c);
        return "redirect:/admin/complaints/" + id + "?updated=true";
    }
}
