package com.aposbot.report;

import com.aposbot.common.BotPropReader;
import com.google.gson.GsonBuilder;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Method;

import java.io.IOException;

public class ReportService {
    public void sendReport(ReportDto report) {
        String json = serialize(report);
        sendRequest(json);
    }

    private void sendRequest(String jsonBody) {
        String url = BotPropReader.getProperties().getProperty("report_api_url");
        if (url == null)
            return;

        try (CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault()) {
            httpclient.start();
            SimpleHttpRequest postRequest = SimpleHttpRequest.create(Method.POST.toString(), url);
            postRequest.setBody(jsonBody, ContentType.APPLICATION_JSON);
            httpclient.execute(postRequest, null);
        } catch (IOException ignore) { }
    }

    private String serialize(ReportDto report) {
        return new GsonBuilder().create().toJson(report);
    }

    public static ReportService create() {
        return new ReportService();
    }
}
