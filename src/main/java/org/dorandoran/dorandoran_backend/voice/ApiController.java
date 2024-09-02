package org.dorandoran.dorandoran_backend.voice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/send-data")
    public ResponseEntity<byte[]> receiveAndForwardData(@RequestBody byte[] data) {
    // public ResponseEntity<byte[]> receiveAndForwardData(
    //         @RequestParam("voice") MultipartFile data,
    //         @RequestParam("user") Long userId,
    //         @RequestParam("chat_room") Long chat_room
    // ){
        // Forward data to AI server and get response
        byte[] responseData = aiService.sendBinaryDataAndGetResponse(data);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "audio/mpeg"); // or the appropriate MIME type for your audio

        // Return response to the client
        return new ResponseEntity<>(responseData, headers, HttpStatus.OK);
    }
}