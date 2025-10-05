package com.example.report_issues.util;

import java.time.Year;
import java.util.UUID;

public class TrackingIdGenerator {

    /**
     * Generates a tracking id like: CIT-2025-6f1a2b3c (8 hex chars)
     */
    public static String generate() {
        String year = String.valueOf(Year.now().getValue());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String shortHex = uuid.substring(0, 8);
        return "CIT-" + year + "-" + shortHex;
    }
}
