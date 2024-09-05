package org.dorandoran.dorandoran_backend.summary;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/summary")
public class SummaryController {

    @Autowired
    private SummaryService summaryService;

    @PostMapping
    public ResponseEntity<?> getSummary(@RequestParam("chat_room_id") String chatRoomId) {

        ResponseEntity<String> response = summaryService.requestSummaryFromAIServer(chatRoomId);

        return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
    }

}
