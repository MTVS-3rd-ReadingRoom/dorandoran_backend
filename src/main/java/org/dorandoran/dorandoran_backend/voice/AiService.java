package org.dorandoran.dorandoran_backend.voice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class AiService {

    private static final String AI_SERVER_URL_DISUSSION = "https://sheepdog-bold-bulldog.ngrok-free.app/stt/input_discussion_content";
    private static final String AI_SERVER_URL_TOPIC = "https://sheepdog-bold-bulldog.ngrok-free.app/discussion_topic/topic_suggest";


    @Autowired
    private RestTemplate restTemplate;

    public byte[] sendBinaryDataAndGetResponse(byte[] data) {
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/octet-stream");

        // Prepare request entity
        HttpEntity<ByteArrayResource> requestEntity = new HttpEntity<>(new ByteArrayResource(data), headers);

        // Send POST request and receive response as byte array
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    AI_SERVER_URL_DISUSSION,
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
            System.err.println("서버에서 500 에러 발생: " + e.getResponseBodyAsString());
            // 서버에서 발생한 오류 메시지 로깅
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("예상치 못한 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }

        // Return response body
        return null;
    }


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
                    AI_SERVER_URL_DISUSSION,
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
            System.err.println("서버에서 500 에러 발생: " + e.getResponseBodyAsString());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("예상치 못한 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }

        // Return response body
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

        // HttpEntity 생성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // 요청 보내기
        ResponseEntity<byte[]> response = restTemplate.exchange(AI_SERVER_URL_TOPIC, HttpMethod.POST, requestEntity, byte[].class);

        // 응답 상태 확인
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            byte[] fileContent = response.getBody();

            return fileContent;
        }

        throw new RuntimeException("Failed to fetch audio data");
    }
}
