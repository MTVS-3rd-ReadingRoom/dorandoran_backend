package org.dorandoran.dorandoran_backend.summary;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class SummaryService {

    private final RestTemplate restTemplate;
    private final String aiServerUrl = "https://sheepdog-bold-bulldog.ngrok-free.app/discussion_summary/discussion_summary";

    public SummaryService() {
        this.restTemplate = new RestTemplate();
    }

    public ResponseEntity<String> requestSummaryFromAIServer(String chatRoomId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("chat_room_id", chatRoomId);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(
                aiServerUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
    }
}