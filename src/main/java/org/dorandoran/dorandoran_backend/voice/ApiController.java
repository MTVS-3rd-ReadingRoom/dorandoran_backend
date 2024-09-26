package org.dorandoran.dorandoran_backend.voice;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@Tag(name = "AI 요청 API")
public class ApiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/tts/basic")
    @Operation(summary = "TTS")
    public ResponseEntity<byte[]> ttsBasic(@RequestParam("text") String text, HttpServletRequest request){
        return aiService.ttsBasicService(text, request);
    }

    @PostMapping("/tts/topic-content")
    @Operation(summary = "topic-content TTS")
    public ResponseEntity<byte[]> ttsBasic(@RequestParam("topic") String topic, @RequestParam("content") String content){
        return aiService.ttsTopicContentService(topic, content);
    }

    @PostMapping("/voice")
    @Operation(summary = "발언 학습")
    public ResponseEntity<byte[]> receiveAndForwardData(@RequestParam("file") MultipartFile data, @RequestParam("user_id") String userId, @RequestParam("chat_room_id") String chat_room) {
        // Forward data to AI server and get response
        byte[] responseData = aiService.sendUserSpeaking(data, userId, chat_room);

        // AI에서 테스트용 음성 파일 반환
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "audio/mpeg");

        return new ResponseEntity<>(responseData, headers, HttpStatus.OK);
    }

    // 채팅방을 입력받아 서버로 application/x-www-form-urlencoded 형식으로 전송
    @PostMapping("/topic_suggest")
    @Operation(summary = "주제 추천(음성)")
    public ResponseEntity<?> sendChatRoom(@RequestParam("chat_room_id") String chat_room_id) {
        // Forward data to AI server and get response
        byte[] responseData = aiService.responseGetTopicVoice(chat_room_id);
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "audio/wav"); // or the appropriate MIME type for your audio

        return new ResponseEntity<>(responseData, headers, HttpStatus.OK);
    }

    @PostMapping("/topic_suggest-text")
    @Operation(summary = "주제 추천(text)")
    public ResponseEntity<?> sendChatRoomResponseText(@RequestParam("chat_room_id") String chat_room_id) {

        ResponseEntity<?> response = aiService.responseGetTopicText(chat_room_id);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}