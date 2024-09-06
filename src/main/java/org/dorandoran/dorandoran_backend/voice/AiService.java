package org.dorandoran.dorandoran_backend.voice;

import org.dorandoran.dorandoran_backend.common.AiServerUrl;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AiService {


    public byte[] sendBinaryDataAndGetResponse(MultipartFile data, String userId, String chatRoom) {
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
            e.printStackTrace();
            return null; // InputStream 생성 실패 시 처리
        }
        body.add("user_id", userId);
        body.add("chat_room_id", chatRoom);

        // Prepare request entity
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Send POST request and receive response as byte array
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    AiServerUrl.DISCUSSION,
                    HttpMethod.POST,
                    requestEntity,
                    byte[].class
            );
            // 성공 시 응답 처리
            if (response.getStatusCode().is2xxSuccessful()) {
                // 정상 응답 처리
                System.out.println("상태코드: " + response.getStatusCode());
                return response.getBody();
            } else {
                // 서버에서 오류 응답 처리
                System.out.println("서버 오류 응답: " + response.getStatusCode());
            }
        } catch (HttpServerErrorException e) {
            System.err.println("AI 요청 에러: " + e.getResponseBodyAsString());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("예상치 못한 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public byte[] sendChatRoom(String chatRoomId) {
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 바디에 들어갈 데이터 설정
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("chat_room_id", chatRoomId);

        System.out.println(chatRoomId);
        // HttpEntity 생성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // 요청 보내기
        try{
            ResponseEntity<byte[]> response = restTemplate.exchange(AiServerUrl.TOPIC, HttpMethod.POST, requestEntity, byte[].class);
            // 응답 상태 확인
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                byte[] fileContent = response.getBody();

                return fileContent;
            }
        } catch (HttpServerErrorException e) {
            System.err.println("AI 요청 에러: " + e.getResponseBodyAsString());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("예상치 못한 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }

        throw new RuntimeException("Failed to fetch audio data");
    }

    public String sendChatRoomResponseText(String chatRoomId) {
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 바디에 들어갈 데이터 설정
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("chat_room_id", chatRoomId);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try{
            ResponseEntity<String> response = restTemplate.exchange(AiServerUrl.TOPIC_TEXT, HttpMethod.POST, requestEntity, String.class);
            // 응답 상태 확인
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (HttpServerErrorException e) {
            System.err.println("AI 요청 에러: " + e.getResponseBodyAsString());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("예상치 못한 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }

        throw new RuntimeException("Failed to fetch audio data");
    }
}
