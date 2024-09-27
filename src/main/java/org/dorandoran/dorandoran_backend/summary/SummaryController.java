package org.dorandoran.dorandoran_backend.summary;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.dorandoran.dorandoran_backend.component.UserTokenStorage;
import org.dorandoran.dorandoran_backend.debateroom.DebateRoomRepository;
import org.dorandoran.dorandoran_backend.debateroom.DebateRoomUserRepository;
import org.dorandoran.dorandoran_backend.metric.MetricService;
import org.dorandoran.dorandoran_backend.metric.RequestClassification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Optionals;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/summary")
@Tag(name="AI 요청 API")
@Slf4j
public class SummaryController {

    private final SummaryService summaryService;
    private final DebateRoomRepository debateRoomRepository;
    private final DebateRoomUserRepository debateRoomUserRepository;
    private final UserTokenStorage userTokenStorage;
    private final MetricService metricService;
    // private final LogFac

    @Autowired
    public SummaryController(SummaryService summaryService, DebateRoomRepository debateRoomRepository, DebateRoomUserRepository debateRoomUserRepository, UserTokenStorage userTokenStorage, MetricService metricService) {
        this.summaryService = summaryService;
        this.debateRoomRepository = debateRoomRepository;
        this.debateRoomUserRepository = debateRoomUserRepository;
        this.userTokenStorage = userTokenStorage;
        this.metricService = metricService;
    }

    @GetMapping
    @Operation(summary = "사용자 토론 요약")
    public ResponseEntity<?> getSummaryByUserId(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        Long userNo = userTokenStorage.getToken(authorizationHeader);
        if (userNo == null) {
            metricService.incrementErrorCount(RequestClassification.AI.label(), "UserNotFound");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{'error': 로그인이 필요합니다.}");
        }
        metricService.incrementRequestCount(RequestClassification.AI.label(), "getSummaryByUserId");
        return ResponseEntity.ok(debateRoomUserRepository.findByUserNoWithJoin(userNo));
        // return null;
    }

    @PostMapping
    @Operation(summary = "전체 토론 요약")
    @Transactional
    public ResponseEntity<?> getSummary(@RequestParam("chat_room_id") String debateRoomNo) {

        ResponseEntity<Map> response = summaryService.requestSummaryFromAIServer(debateRoomNo);

        if (response.getStatusCode().is2xxSuccessful()){
            Optional.ofNullable((String) response.getBody().get("message")).ifPresentOrElse(
                    summary -> {
                        // debateRoomRepository.updateSummary(debateRoomNo, summary);
                        debateRoomRepository.updateSummary(Long.parseLong(debateRoomNo), summary, LocalDate.now(), LocalTime.now());
                    },
                    () -> log.error("AI 요청은 성공했지만 요약 글이 없음")
            );
        }

        metricService.incrementRequestCount(RequestClassification.AI.label(), "getSummary");
        return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
    }

}
