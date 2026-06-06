package com.ifteakar.portfolio_backend.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final String resendApiKey = System.getenv("RESEND_API_KEY");
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Async
    public void sendAdminNotification(String visitorName, String visitorEmail, String messageContent) {
        try {
            String htmlContent = """
                <h3>You received a new message from your portfolio!</h3>
                <p><strong>Name:</strong> %s</p>
                <p><strong>Email:</strong> %s</p>
                <p><strong>Message:</strong><br>%s</p>
                """.formatted(visitorName, visitorEmail, messageContent.replace("\n", "<br>"));


            String jsonBody = """
                {
                  "from": "Portfolio Bot <contact@ifteakar.dev>",
                  "to": ["ifteakarahmed.kg@gmail.com"],
                  "subject": "New Portfolio Message from %s",
                  "html": "%s"
                }
                """.formatted(visitorName, jsonBodyEscape(htmlContent));

            sendPostRequest(jsonBody);

        } catch (Exception e) {
            System.err.println("Error sending admin notification via Resend: " + e.getMessage());
        }
    }

    @Async
    public void sendAutoReplyToVisitor(String visitorEmail, String visitorName) {
        try {
            String htmlContent = """
                <p>Hi %s,</p>
                <p>Thank you for reaching out through my portfolio website! I have successfully received your message.</p>
                <p>I will review your message and get back to you as soon as possible.</p>
                <br>
                <p>Best regards,<br><strong>Khandokar Ifteakar Ahmed</strong></p>
                """.formatted(visitorName);

            String jsonBody = """
                {
                  "from": "Khandokar Ifteakar Ahmed <contact@ifteakar.dev>",
                  "to": ["%s"],
                  "subject": "Thanks for reaching out! - Khandokar Ifteakar Ahmed",
                  "html": "%s"
                }
                """.formatted(visitorEmail, jsonBodyEscape(htmlContent));

            sendPostRequest(jsonBody);

        } catch (Exception e) {
            System.err.println("Error sending auto-reply via Resend: " + e.getMessage());
        }
    }

    private void sendPostRequest(String jsonBody) {
        if (resendApiKey == null || resendApiKey.isEmpty()) {
            System.err.println("Resend API Key is missing! Please set RESEND_API_KEY environment variable.");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header("Authorization", "Bearer " + resendApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    System.out.println("Resend Status Code: " + response.statusCode());
                    System.out.println("Resend Response: " + response.body());
                });
    }

    private String jsonBodyEscape(String input) {
        return input.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
}