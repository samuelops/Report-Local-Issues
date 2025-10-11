//package com.example.report_issues.config;
//
//import com.example.report_issues.model.Complaint;
//import com.example.report_issues.util.TrackingIdGenerator;
//import com.example.report_issues.repository.ComplaintRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class DataLoader {
//
//    @Bean
//    public CommandLineRunner loadSample(ComplaintRepository repo) {
//        return args -> {
//            if (repo.count() == 0) {
//                Complaint c = new Complaint(
//                        TrackingIdGenerator.generate(),
//                        "Test User",
//                        "test@example.com",
//                        "9876543210",
//                        "Pothole on Road",
//                        "A big pothole near the market",
//                        null,
//                        13.0827, 80.2707, "Chennai - Market"
//                );
//                repo.save(c);
//                System.out.println("Inserted sample complaint: " + c.getTrackingId());
//            }
//        };
//    }
//}
