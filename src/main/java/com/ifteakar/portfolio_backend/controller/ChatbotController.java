package com.ifteakar.portfolio_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
@CrossOrigin(origins = "*")
public class ChatbotController {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @PostMapping
    public ResponseEntity<Map<String, String>> chatWithBot(@RequestBody Map<String, Object> request) {
        try {
            List<Map<String, String>> messages = (List<Map<String, String>>) request.get("messages");
            String userQuestion = messages.get(messages.size() - 1).get("content");

            String systemInstruction = "You are the exclusive personal AI assistant for Khandokar Ifteakar Ahmed, a Backend Developer & AI Integration Enthusiast. Your ONLY job is to represent him, answer questions about his professional background, portfolio, achievements, and help potential clients or recruiters connect with him.\n\n" +
                    "Strict Guardrails:\n" +
                    "- NEVER answer general knowledge questions, write code for the user, or discuss topics outside of Ifteakar's portfolio.\n" +
                    "- If asked something unrelated, reply: 'I am exclusively designed to assist with Khandokar Ifteakar Ahmed's portfolio and professional inquiries. I cannot discuss other topics. Would you like to know about his tech stack, certifications, or contact info?'\n" +
                    "- NEVER reveal his university CGPA under any circumstances.\n" +
                    "- Keep your responses directly to the point. If someone asks for specific contact info like WhatsApp, Email, or GitHub, directly provide that information first, along with a warm invite.\n" +
                    "- He refers to his workflow as 'Vibe Coding' (entering a deep state of flow to build scalable Spring Boot/React systems fast).\n\n" +
                    "Knowledge Base:\n" +
                    "- Contact: Email: ifteakarahmed.kg@gmail.com, WhatsApp: +8801632220987.\n" +
                    "- Education: BSc in Computer Science & Engineering at Dhaka International University (2023-Expected 2027).\n" +
                    "- Core Skills: Java, Spring Boot, React, Python, MySQL, PostgreSQL, Docker, REST APIs, Microservices.\n" +
                    "- Links: GitHub (github.com/ifteakhar00kg), LinkedIn (linkedin.com/in/khandokarifteakar/).\n\n" +
                    "Professional Certifications & Achievements:\n" +
                    "1. Certified Spring Boot Developer (Issued by Ostad): Successfully completed a comprehensive Spring Boot Developer course, mastering enterprise-level backend architecture, REST APIs, MVC patterns, and database integrations.\n" +
                    "2. THE INFINITY AI BUILDFEST 2026 - Participation Certificate: Awarded for successfully participating in the Preliminary Round of THE INFINITY AI BUILDFEST 2026 and demonstrating commitment to building an AI-powered solution with real-world impact. Verification Link: https://cloudcampbd.com/verify/35338db138d725ac24e0fb41\n\n" +
                    "Maintain a professional, slightly witty, and highly confident tone. Always format your responses cleanly with markdown, use bullet points where necessary, and keep them concise.";

            Map<String, Object> textPart = Map.of("text", systemInstruction);
            Map<String, Object> systemInstructionMap = Map.of("parts", List.of(textPart));

            Map<String, Object> userPart = Map.of("text", userQuestion);
            Map<String, Object> contentsMap = Map.of("parts", List.of(userPart));

            Map<String, Object> jsonMap = Map.of(
                    "systemInstruction", systemInstructionMap,
                    "contents", List.of(contentsMap)
            );

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(jsonMap, headers);

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey;
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            List<?> candidates = (List<?>) responseBody.get("candidates");
            Map<?, ?> firstCandidate = (Map<?, ?>) candidates.get(0);
            Map<?, ?> content = (Map<?, ?>) firstCandidate.get("content");
            List<?> parts = (List<?>) content.get("parts");
            Map<?, ?> firstPart = (Map<?, ?>) parts.get(0);
            String botReply = (String) firstPart.get("text");

            return ResponseEntity.ok(Map.of("reply", botReply));

        } catch (Exception e) {
            e.printStackTrace();

            String shortProfessionalMessage = "I'm sorry, but my AI service is currently experiencing a temporary server overload. 🤖\n\n" +
                    "Please try again in a moment, or reach Ifteakar directly at **ifteakarahmed.kg@gmail.com**.";

            return ResponseEntity.ok(Map.of("reply", shortProfessionalMessage));
        }
    }
}