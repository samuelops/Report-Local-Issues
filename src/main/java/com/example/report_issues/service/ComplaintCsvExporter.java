package com.example.report_issues.service;

import com.example.report_issues.model.Complaint;
import com.example.report_issues.repository.ComplaintRepository;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

/**
 * Streams complaints as CSV to an OutputStream.
 * Very simple CSV escaping (wrap fields with double-quotes and escape internal quotes).
 */
@Service
public class ComplaintCsvExporter {

    private final ComplaintRepository repo;

    public ComplaintCsvExporter(ComplaintRepository repo) {
        this.repo = repo;
    }

    /**
     * Write all complaints (optional: can be filtered) to the provided Writer as CSV.
     */
    public void writeAllAsCsv(Writer writer) throws IOException {
        List<Complaint> list = repo.findAll();

        // CSV header
        PrintWriter pw = new PrintWriter(writer);
        pw.println(csvLine("id","trackingId","name","email","phone","title","description",
                "status","latitude","longitude","address","adminNotes","createdAt","updatedAt"));

        for (Complaint c : list) {
            pw.println(csvLine(
                    String.valueOf(c.getId()),
                    c.getTrackingId(),
                    c.getName(),
                    c.getEmail(),
                    c.getPhone(),
                    c.getTitle(),
                    c.getDescription(),
                    c.getStatus() != null ? c.getStatus().name() : "",
                    c.getLatitude() != null ? String.valueOf(c.getLatitude()) : "",
                    c.getLongitude() != null ? String.valueOf(c.getLongitude()) : "",
                    c.getAddress(),
                    c.getAdminNotes(),
                    c.getCreatedAt() != null ? c.getCreatedAt().toString() : "",
                    c.getUpdatedAt() != null ? c.getUpdatedAt().toString() : ""
            ));
        }
        pw.flush();
    }

    // helper to escape CSV fields simply
    private String csvLine(String... fields) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String f : fields) {
            if (!first) sb.append(',');
            sb.append(escapeCsv(f));
            first = false;
        }
        return sb.toString();
    }

    // wrap field in double quotes and escape inner quotes by doubling them
    private String escapeCsv(String in) {
        if (in == null) return "\"\"";
        String escaped = in.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
