package org.dorandoran.dorandoran_backend.voice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;

@Service
public class AiService {

    private static final String AI_SERVER_URL = "https://sheepdog-bold-bulldog.ngrok-free.app/speak2speak_chunk";

    @Autowired
    private RestTemplate restTemplate;

    public byte[] sendBinaryDataAndGetResponse(byte[] data) {
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/octet-stream");

        // Prepare request entity
        HttpEntity<InputStreamResource> requestEntity = new HttpEntity<>(new InputStreamResource(new ByteArrayInputStream(data)), headers);

        // Send POST request and receive response as byte array
        ResponseEntity<byte[]> response = restTemplate.exchange(
                AI_SERVER_URL,
                HttpMethod.POST,
                requestEntity,
                byte[].class
        );

        // Return response body
        return response.getBody();
    }
}
