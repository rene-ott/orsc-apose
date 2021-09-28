package com.aposbot.report;

import com.aposbot.common.BotPropReader;
import com.google.gson.GsonBuilder;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Method;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReportService {

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void sendReport(ReportDto report) {
        String url = BotPropReader.getProperties().getProperty("report_api_url");
        if (url == null)
            return;

        executorService.execute(() -> {
            String json = serialize(report);
            sendRequest(json, url);
        });
    }

    private void sendRequest(String jsonBody, String url) {
        try (CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault()) {
            httpclient.start();
            SimpleHttpRequest postRequest = SimpleHttpRequest.create(Method.POST.toString(), url);
            postRequest.setBody(jsonBody, ContentType.APPLICATION_JSON);
            HttpResponse response = httpclient.execute(postRequest, null).get();

            if (response.getCode() == 200) {
                System.out.println("Sent API report successfully");
            }
        } catch (IOException | ExecutionException | InterruptedException e) {
            System.out.println("Failed to send API report");
        }
    }

    private String serialize(ReportDto report) {
        return new GsonBuilder().create().toJson(report);
    }

    public static ReportService create() {
        return new ReportService();
    }
}
