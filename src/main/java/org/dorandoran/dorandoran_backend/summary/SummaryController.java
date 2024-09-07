package org.dorandoran.dorandoran_backend.summary;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.dorandoran.dorandoran_backend.debateroom.DebateRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Optionals;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/summary")
@Tag(name="AI 요청 API")
@Slf4j
public class SummaryController {

    private final SummaryService summaryService;
    private final DebateRoomRepository debateRoomRepository;
    // private final LogFac

    @Autowired
    public SummaryController(SummaryService summaryService, DebateRoomRepository debateRoomRepository) {
        this.summaryService = summaryService;
        this.debateRoomRepository = debateRoomRepository;
    }

    @PostMapping
    @Operation(summary = "전체 토론 요약")
    @Transactional
    public ResponseEntity<?> getSummary(@RequestParam("chat_room_id") String debateRoomNo) {

        ResponseEntity<Map> response = summaryService.requestSummaryFromAIServer(debateRoomNo);

        if (response.getStatusCode().is2xxSuccessful()){
            Optional.ofNullable((String) response.getBody().get("message")).ifPresentOrElse(
                    summary -> {
                        debateRoomRepository.updateSummary(debateRoomNo, summary);
                        // debateRoomRepository.updateSummary(Long.parseLong(debateRoomNo), summary);
                    },
                    () -> log.error("AI 요청은 성공했지만 요약 글이 없음")
            );
        }

        return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
    }

}
