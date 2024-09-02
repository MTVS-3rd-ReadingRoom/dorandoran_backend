package org.dorandoran.dorandoran_backend.voice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
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
        HttpEntity<ByteArrayResource> requestEntity = new HttpEntity<>(new ByteArrayResource(data), headers);

        // Send POST request and receive response as byte array
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    AI_SERVER_URL,
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

    // public byte[] sendBinaryDataAndGetResponse(byte[] data) {
    //     // Set headers
    //     HttpHeaders headers = new HttpHeaders();
    //     headers.set("Content-Type", "application/octet-stream");
    //
    //     // Prepare request entity
    //     HttpEntity<InputStreamResource> requestEntity = new HttpEntity<>(new InputStreamResource(new ByteArrayInputStream(data)), headers);
    //
    //     // Send POST request and receive response as byte array
    //     ResponseEntity<byte[]> response = restTemplate.exchange(
    //             AI_SERVER_URL,
    //             HttpMethod.POST,
    //             requestEntity,
    //             byte[].class
    //     );
    //
    //     // Return response body
    //     return response.getBody();
    // }
}
