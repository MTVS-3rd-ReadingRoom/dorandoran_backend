package org.dorandoran.dorandoran_backend.voice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/send-data")
    // public ResponseEntity<byte[]> receiveAndForwardData(@RequestBody byte[] data) {
    public ResponseEntity<byte[]> receiveAndForwardData(
            @RequestParam("file") MultipartFile data,
            @RequestParam("user_id") String userId,
            @RequestParam("chat_room_id") String chat_room
    ){
        // Forward data to AI server and get response
        byte[] responseData = aiService.sendBinaryDataAndGetResponse(data, userId, chat_room);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "audio/mpeg"); // or the appropriate MIME type for your audio

        // Return response to the client
        return new ResponseEntity<>(responseData, headers, HttpStatus.OK);
    }

    // 채팅방을 입력받아 서버로 application/x-www-form-urlencoded 형식으로 전송
    @PostMapping("/topic_suggest")
    public ResponseEntity<?> sendChatRoom(
            @RequestParam("chat_room_id") String chat_room_id
    ) throws IOException {
        // Forward data to AI server and get response
        byte[] responseData = aiService.sendChatRoom(chat_room_id);
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "audio/wav"); // or the appropriate MIME type for your audio

        return new ResponseEntity<>(responseData, headers, HttpStatus.OK);
    }

}