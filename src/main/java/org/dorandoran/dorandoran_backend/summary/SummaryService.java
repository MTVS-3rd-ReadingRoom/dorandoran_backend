package org.dorandoran.dorandoran_backend.summary;

import org.dorandoran.dorandoran_backend.common.AiServerUrl;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SummaryService {

    private final RestTemplate restTemplate;

    public SummaryService() {
        this.restTemplate = new RestTemplate();
    }

    public ResponseEntity<Map> requestSummaryFromAIServer(String chatRoomId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("chat_room_id", chatRoomId);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(
                AiServerUrl.SUMMARY,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );
    }
}