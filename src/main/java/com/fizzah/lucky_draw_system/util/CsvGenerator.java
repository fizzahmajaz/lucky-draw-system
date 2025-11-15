package com.fizzah.lucky_draw_system.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class CsvGenerator {

    /**
     * rows: list of map with column -> value
     */
    public static byte[] generateCsv(List<String> headers, List<Map<String, String>> rows) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {

            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader(headers.toArray(new String[0]))
                    .setSkipHeaderRecord(false)
                    .build();

            try (CSVPrinter printer = new CSVPrinter(osw, format)) {

                for (Map<String, String> row : rows) {
                    String[] values = headers.stream()
                            .map(h -> row.getOrDefault(h, ""))
                            .toArray(String[]::new);
                    printer.printRecord((Object[]) values);
                }

                printer.flush();
            }

            return baos.toByteArray();

        } catch (IOException ex) {
            throw new RuntimeException("Failed to generate CSV", ex);
        }
    }
}
