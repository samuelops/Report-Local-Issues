package com.example.report_issues.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final boolean enabled;
    private final TaskExecutor taskExecutor; // runs async

    public EmailService(JavaMailSender mailSender,
                        @Value("${app.email.enabled:false}") boolean enabled,
                        TaskExecutor taskExecutor) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.taskExecutor = taskExecutor;
    }

    public void sendComplaintAcknowledgement(String toEmail, String name, String trackingId, String title, String description) {
        if (!enabled) return;
        if (toEmail == null || toEmail.isBlank()) return;

        // run async to not block request
        taskExecutor.execute(() -> {
            try {
                MimeMessage msg = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
                helper.setTo(toEmail);
                helper.setSubject("Complaint Received — " + trackingId);
                helper.setFrom("charlessamuelmgr@gmail.com"); // replace or configure sender

                String html = buildHtml(name, trackingId, title, description);
                helper.setText(html, true);

                mailSender.send(msg);
                System.out.println("Sent ack email to " + toEmail + " for " + trackingId);
            } catch (Exception e) {
                // fail-safe: log and continue
                System.err.println("Failed to send email: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private String buildHtml(String name, String trackingId, String title, String description) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: Arial, sans-serif; color: #222;'>");
        sb.append("<h2>Complaint Received</h2>");
        sb.append("<p>Hi ").append(escapeHtml(name)).append(",</p>");
        sb.append("<p>Thank you for reporting the issue. We have received your complaint and assigned tracking ID:</p>");
        sb.append("<p style='font-weight: bold; font-family: monospace;'>").append(trackingId).append("</p>");
        sb.append("<p><strong>Title:</strong> ").append(escapeHtml(title)).append("</p>");
        sb.append("<p><strong>Description:</strong><br>").append(escapeHtml(description)).append("</p>");
        sb.append("<p>You can track your complaint status at our portal using the tracking ID above.</p>");
        sb.append("<p>— Report Local Issues Team</p>");
        sb.append("</div>");
        return sb.toString();
    }

    private String escapeHtml(String in) {
        if (in == null) return "";
        return in.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#x27;");
    }
}
