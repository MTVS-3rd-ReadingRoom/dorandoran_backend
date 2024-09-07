package org.dorandoran.dorandoran_backend.voice;

import lombok.extern.slf4j.Slf4j;
import org.dorandoran.dorandoran_backend.common.AiServerUrl;
import org.dorandoran.dorandoran_backend.debateroom.DebateRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class AiService {

    private final DebateRoomRepository debateRoomRepository;

    @Autowired
    public AiService(DebateRoomRepository debateRoomRepository) {
        this.debateRoomRepository = debateRoomRepository;
    }


    public byte[] sendUserSpeaking(MultipartFile data, String userId, String chatRoom) {
        RestTemplate restTemplate = new RestTemplate();

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Prepare multipart request
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            body.add("file", new ByteArrayResource(data.getBytes()) {
                @Override
                public String getFilename() {
                    return data.getOriginalFilename();
                }
            });
        } catch (IOException e) {
            log.error("Failed to read file: " + e.getMessage());
        }
        body.add("user_id", userId);
        body.add("chat_room_id", chatRoom);

        // Prepare request entity
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    AiServerUrl.DISCUSSION,
                    HttpMethod.POST,
                    requestEntity,
                    byte[].class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        }  catch (Exception e) {
            log.error("sendUserSpeaking error: {}", e.getMessage());
        }

        return null;
    }

    public byte[] responseGetTopicVoice(String chatRoomId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("chat_room_id", chatRoomId);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try{
            ResponseEntity<byte[]> response = restTemplate.exchange(AiServerUrl.TOPIC_VOICE, HttpMethod.POST, requestEntity, byte[].class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.error("sendUserSpeaking error: {}", e.getMessage());
        }

        throw new RuntimeException("Failed to fetch audio data");
    }

    @Transactional
    public ResponseEntity<?> responseGetTopicText(String debateRoomNo) {
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 바디에 들어갈 데이터 설정
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("chat_room_id", debateRoomNo);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try{
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    AiServerUrl.TOPIC_TEXT, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {
                    });
            // 응답 상태 확인
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("topic: " + response.getBody().get("topic").toString());
                debateRoomRepository.updateTopic(debateRoomNo, response.getBody().get("topic").toString());
                // debateRoomRepository.updateTopic(Long.parseLong(debateRoomNo), response.getBody().get("topic").toString());
                return response;
            }
        } catch (Exception e) {
            log.error("sendUserSpeaking error: {}", e.getMessage());
        }
        throw new RuntimeException("Failed to fetch audio data");
    }
}
