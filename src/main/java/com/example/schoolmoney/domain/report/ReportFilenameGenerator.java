package com.example.schoolmoney.domain.report;

import com.example.schoolmoney.utils.DateToStringConverter;
import com.example.schoolmoney.utils.StringSanitizer;

public class ReportFilenameGenerator {

    private ReportFilenameGenerator() {
    }

    public static String generate(String reportTitle) {
        String timestamp = DateToStringConverter.nowFileTimestamp();

        String filename = String.format(
                "Report_%s_%s.pdf",
                reportTitle.replaceAll("\\s+", "_"),
                timestamp
        );

        return StringSanitizer.sanitizeString(filename);
    }

}
