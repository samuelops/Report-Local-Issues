package com.example.report_issues.controller;

import com.example.report_issues.model.Complaint;
import com.example.report_issues.model.ComplaintStatus;
import com.example.report_issues.repository.ComplaintRepository;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final com.example.report_issues.service.ComplaintCsvExporter csvExporter;
    private final ComplaintRepository repo;

    public AdminController(ComplaintRepository repo, com.example.report_issues.service.ComplaintCsvExporter csvExporter) {
        this.repo = repo;
        this.csvExporter = csvExporter;
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

    @GetMapping("/complaints/export")
    public ResponseEntity<StreamingResponseBody> exportCsv() {
        String filename = "complaints-" + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".csv";

        StreamingResponseBody stream = outputStream -> {
            // use UTF-8 BOM to help Excel detect UTF-8 (optional)
            // outputStream.write(new byte[] {(byte)0xEF, (byte)0xBB, (byte)0xBF});
            try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, java.nio.charset.StandardCharsets.UTF_8)) {
                csvExporter.writeAllAsCsv(writer);
            } catch (Exception e) {
                e.printStackTrace();
                // writing error will close stream
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=utf-8"))
                .body(stream);
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
