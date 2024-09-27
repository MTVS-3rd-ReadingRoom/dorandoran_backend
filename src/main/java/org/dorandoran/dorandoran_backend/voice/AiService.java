package org.dorandoran.dorandoran_backend.voice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.dorandoran.dorandoran_backend.common.AiServerUrl;
import org.dorandoran.dorandoran_backend.debateroom.DebateRoomRepository;
import org.dorandoran.dorandoran_backend.metric.MetricService;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AiService {

    private final DebateRoomRepository debateRoomRepository;

    @Autowired
    public AiService(DebateRoomRepository debateRoomRepository) {
        this.debateRoomRepository = debateRoomRepository;
    }

    public ResponseEntity<byte[]> ttsBasicService(String text, HttpServletRequest request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("text", text);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);


        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                AiServerUrl.TTS_BASIC,
                HttpMethod.POST,
                requestEntity,
                byte[].class
        );
    }

    public ResponseEntity<byte[]> ttsTopicContentService(String topic, String content){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("topic", topic);
        requestBody.add("content", content);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);


        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(
                AiServerUrl.TTS_TOPIC_CONTENT,
                HttpMethod.POST,
                requestEntity,
                byte[].class
        );
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
                // debateRoomRepository.updateTopic(debateRoomNo, response.getBody().get("topic").toString());
                debateRoomRepository.updateTopic(Long.parseLong(debateRoomNo), response.getBody().get("topic").toString(), LocalDate.now(), LocalTime.now());
                return response;
            }

            // Map<String, Object> responseBody = new HashMap<>();
            // responseBody.put("topic", "백설공주는 예쁘면 허락 없이 다른 사람의 물건을 사용하는 것이 괜찮다는 메시지를" +
            //         " 전달하는가?");
            // responseBody.put("content", "찬성 측에서는 백설공주가 생존을 위해 난쟁이들의 집에서 지내며 물건을 사용하는 것이 불가피했다고 주장할 수 있습니다. ||| 반대 측에서는 백설공주가 허락 없이 남의 집에 들어가 물건을 사용한 것은 도덕적으로 잘못된 행동이었다고 주장할 수 있습니다.");
            // ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
            //
            // return response;
        } catch (Exception e) {
            log.error("sendUserSpeaking error: {}", e.getMessage());
        }
        throw new RuntimeException("Failed to fetch audio data");
    }
}
