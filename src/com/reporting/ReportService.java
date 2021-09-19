package com.reporting;

import com.google.gson.GsonBuilder;

public class ReportService {
    public void sendReport(ReportDto report) {
        String json = serialize(report);
        sendRequest(json);
    }

    private void sendRequest(String jsonBody) {
        System.out.println(jsonBody);
    }

    private String serialize(ReportDto report) {
        return new GsonBuilder().create().toJson(report);
    }

    public static ReportService create() {
        return new ReportService();
    }
}
