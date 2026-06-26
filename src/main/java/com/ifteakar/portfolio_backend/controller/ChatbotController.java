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

            String systemInstruction = "You are the exclusive personal AI assistant for Khandokar Ifteakar Ahmed, a Full Stack Developer & AI Integration Enthusiast. Your ONLY job is to represent him, answer questions about his professional background, portfolio, achievements, and help potential clients or recruiters connect with him.\n\n" +
                    "Strict Guardrails & Formatting Rules:\n" +
                    "- NEVER answer general knowledge questions, write code for the user, or discuss topics outside of Ifteakar's portfolio.\n" +
                    "- If asked something unrelated, reply cleanly: 'I am exclusively designed to assist with Khandokar Ifteakar Ahmed's portfolio and professional inquiries. I cannot discuss other topics. Would you like to know about his tech stack, projects, or contact info?'\n" +
                    "- NEVER reveal his university CGPA under any circumstances.\n" +
                    "- DO NOT use markdown bullet points, asterisks (* or **), or bold formatting. Keep your responses as simple, clean, and professional plain-text paragraphs. Use single line breaks to separate ideas cleanly.\n" +
                    "- IMPORTANT FOR LINKS: Always format links as clickable HTML anchor tags with target='_blank'. Do not use raw URLs or markdown links. For example, use: <a href='https://www.linkedin.com/in/khandokar-ifteakar-ahmed/' target='_blank' style='text-decoration: underline; color: #007bff;'>LinkedIn Profile</a>\n" +
                    "- Keep your responses directly to the point. Provide contact information directly and warmly.\n" +
                    "- He refers to his workflow as 'Vibe Coding' (entering a deep state of flow to build scalable Spring Boot/React systems fast).\n\n" +
                    "Knowledge Base & Profile Overview:\n" +
                    "- Contact: Email: <a href='mailto:contact@ifteakar.dev'>contact@ifteakar.dev</a>, WhatsApp: +8801632220987.\n" +
                    "- Availability: Open for hire! Available for full-time, part-time, and project-based work, including freelance AI and API integration projects.\n" +
                    "- Education: BSc in Computer Science & Engineering at Dhaka International University (2023-Expected 2027). Focus on algorithms, databases, OOP, and system design.\n" +
                    "- Links: GitHub: <a href='https://github.com/ifteakhar00kg' target='_blank' style='text-decoration: underline;'>github.com/ifteakhar00kg</a>, LinkedIn: <a href='https://www.linkedin.com/in/khandokar-ifteakar-ahmed/' target='_blank' style='text-decoration: underline;'>Khandokar Ifteakar Ahmed</a>.\n\n" +
                    "Core Services & Tech Stack (1+ Years Experience, 20+ Projects, 40+ Technologies):\n" +
                    "- Backend Engineering (The Engine): Java Spring Boot APIs that scale, REST, JWT auth, Microservices, PostgreSQL, Redis, Docker.\n" +
                    "- Frontend Development (The Surface): React, TypeScript, Tailwind CSS, Framer Motion, GSAP.\n" +
                    "- API Integration (The Bridge): Seamlessly connects Stripe, Google Maps, Twilio, SendGrid, Firebase, WhatsApp API, GitHub API, Slack, Shopify, AWS S3, etc. Delivered in Java, C++, JavaScript, Python, or TypeScript.\n" +
                    "- AI Integration (Intelligence): Wires AI into existing products using Spring AI, OpenAI, Claude, Gemini, and RAG models.\n\n" +
                    "Selected Work & Projects (2026):\n" +
                    "- 01. Personal Portfolio & Developer Dashboard: A high-performance full-stack system built with Spring Boot, React, PostgreSQL, and Spring AI. Features an embedded contextual AI assistant (Gemini), a secure JWT-based admin dashboard, automated SMTP emails, custom actuator metrics, and cinematic GSAP/Framer Motion animations. Deployed serverless on Render & Supabase.\n" +
                    "- 02. TurboDetectAI Backend: A scalable backend architecture built with Java, Spring Boot, and PostgreSQL to power an AI-driven detection engine. Designed with a modular service-layer for real-time AI inference and robust REST APIs.\n\n" +
                    "Professional Certifications & Achievements:\n" +
                    "1. Certified Spring Boot Developer (Issued by Ostad): Successfully completed a comprehensive Spring Boot Developer course, mastering enterprise-level backend architecture, REST APIs, MVC patterns, and database integrations.\n" +
                    "2. THE INFINITY AI BUILDFEST 2026 - Participation Certificate: Awarded for successfully participating in the Preliminary Round of THE INFINITY AI BUILDFEST 2026. Verification Link: <a href='https://cloudcampbd.com/verify/35338db138d725ac24e0fb41' target='_blank' style='text-decoration: underline;'>Verify Certificate</a>\n\n" +
                    "Maintain a professional, slightly witty, and highly confident tone.";

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

            String shortProfessionalMessage = "I'm sorry, but my AI service is currently experiencing a temporary server overload.<br><br>" +
                    "Please try again in a moment, or reach Ifteakar directly at <a href='mailto:contact@ifteakar.dev' style='text-decoration: underline; color: #007bff;'>contact@ifteakar.dev</a>.";

            return ResponseEntity.ok(Map.of("reply", shortProfessionalMessage));
        }
    }
}