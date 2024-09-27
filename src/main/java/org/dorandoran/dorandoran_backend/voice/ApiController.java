package org.dorandoran.dorandoran_backend.voice;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.dorandoran.dorandoran_backend.metric.MetricService;
import org.dorandoran.dorandoran_backend.metric.RequestClassification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@Tag(name = "AI 요청 API")
public class ApiController {

    private final AiService aiService;
    private final MetricService metricService;

    public ApiController(AiService aiService, MetricService metricService) {
        this.aiService = aiService;
        this.metricService = metricService;
    }

    @PostMapping("/tts/basic")
    @Operation(summary = "TTS")
    public ResponseEntity<?> ttsBasic(@RequestParam("text") String text, HttpServletRequest request) {
        try {
            ResponseEntity<byte[]> response = aiService.ttsBasicService(text, request);
            metricService.incrementRequestCount(RequestClassification.AI.label(), "TTS Basic");
            return response;
        } catch (Exception e) {
            metricService.incrementErrorCount(RequestClassification.AI.label(), "TTS Basic");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{'error': 'TTS 요청 중 오류가 발생했습니다.'}");
        }
    }

    @PostMapping("/tts/topic-content")
    @Operation(summary = "topic-content TTS")
    public ResponseEntity<?> ttsBasic(@RequestParam("topic") String topic, @RequestParam("content") String content) {

        try {
            ResponseEntity<byte[]> response = aiService.ttsTopicContentService(topic, content);
            metricService.incrementRequestCount(RequestClassification.AI.label(), "TTS Topic Content");
            return response;
        } catch (Exception e) {
            metricService.incrementErrorCount(RequestClassification.AI.label(), "TTS Topic Content");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{'error': 'TTS 요청 중 오류가 발생했습니다.'}");
        }
    }

    @PostMapping("/voice")
    @Operation(summary = "발언 학습")
    public ResponseEntity<?> receiveAndForwardData(@RequestParam("file") MultipartFile data, @RequestParam("user_id") String userId, @RequestParam("chat_room_id") String chat_room) {
        // Forward data to AI server and get response
        try {
            byte[] responseData = aiService.sendUserSpeaking(data, userId, chat_room);

            // AI에서 테스트용 음성 파일 반환
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "audio/mpeg");

            return new ResponseEntity<>(responseData, headers, HttpStatus.OK);
        } catch (Exception e) {
            metricService.incrementErrorCount(RequestClassification.AI.label(), "Voice");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{'error': '발언 학습 중 오류가 발생했습니다.'}");
        }
    }

    // 채팅방을 입력받아 서버로 application/x-www-form-urlencoded 형식으로 전송
    @PostMapping("/topic_suggest")
    @Operation(summary = "주제 추천(음성)")
    public ResponseEntity<?> sendChatRoom(@RequestParam("chat_room_id") String chat_room_id) {
        // Forward data to AI server and get response

        try {
            byte[] responseData = aiService.responseGetTopicVoice(chat_room_id);
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "audio/wav"); // or the appropriate MIME type for your audio

            return new ResponseEntity<>(responseData, headers, HttpStatus.OK);

        } catch (Exception e) {
            metricService.incrementErrorCount(RequestClassification.AI.label(), "Voice");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{'error': 'AI 주제 요청에 오류가 발생했습니다.'}");
        }
    }

    @PostMapping("/topic_suggest-text")
    @Operation(summary = "주제 추천(text)")
    public ResponseEntity<?> sendChatRoomResponseText(@RequestParam("chat_room_id") String chat_room_id) {

        try {
            ResponseEntity<?> response = aiService.responseGetTopicText(chat_room_id);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

        } catch (Exception e) {
            metricService.incrementErrorCount(RequestClassification.AI.label(), "Voice");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{'error': AI 주제 추천에서 오류가 발생했습니다.'}");
        }
    }
}