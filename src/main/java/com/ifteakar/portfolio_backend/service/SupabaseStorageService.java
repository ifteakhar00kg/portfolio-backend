package com.ifteakar.portfolio_backend.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.bucket}")
    private String bucket;

    @Value("${supabase.secret-key}")
    private String secretKey;

    private final OkHttpClient client = new OkHttpClient();

    public String uploadCv(MultipartFile file) throws IOException {

        String fileName = "cv.pdf";

        RequestBody body = RequestBody.create(
                file.getBytes(),
                MediaType.parse("application/pdf")
        );

        Request request = new Request.Builder()
                .url(supabaseUrl +
                        "/storage/v1/object/" +
                        bucket +
                        "/" +
                        fileName)
                .put(body)
                .addHeader("Authorization", "Bearer " + secretKey)
                .addHeader("apikey", secretKey)
                .addHeader("x-upsert", "true")
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new RuntimeException(
                    "Upload failed: " + response.body().string()
            );
        }

        return supabaseUrl +
                "/storage/v1/object/public/" +
                bucket +
                "/" +
                fileName;
    }

    public String getCvUrl() {
        return supabaseUrl +
                "/storage/v1/object/public/" +
                bucket +
                "/cv.pdf";
    }
}