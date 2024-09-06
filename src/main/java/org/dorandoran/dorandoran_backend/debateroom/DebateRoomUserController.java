package org.dorandoran.dorandoran_backend.debateroom;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.dorandoran.dorandoran_backend.customexception.ErrorResponseHandler;
import org.dorandoran.dorandoran_backend.component.UserTokenStorage;
import org.dorandoran.dorandoran_backend.user.UserInfo;
import org.dorandoran.dorandoran_backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "토론 API")
public class DebateRoomUserController {

    private final DebateRoomUserRepository debateroomUserRepository;
    private final UserTokenStorage userTokenStorage;
    private final DebateRoomRepository debateRoomRepository;
    private final UserRepository userRepository;

    @Autowired
    public DebateRoomUserController(DebateRoomUserRepository debateroomUserRepository, UserTokenStorage userTokenStorage, DebateRoomRepository debateRoomRepository, UserRepository userRepository) {
        this.debateroomUserRepository = debateroomUserRepository;
        this.userTokenStorage = userTokenStorage;
        this.debateRoomRepository = debateRoomRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/api/debate-room-user")
    @Operation(summary = "토론방 참가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Debateroom을 찾을 수 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(example = "{ \"error\": \"Debateroom을 찾을 수 없습니다.\" }"))),
            @ApiResponse(responseCode = "201", description = "토론방 참가 성공", content = @Content(mediaType = "text/plain", schema = @Schema(example = "토론방 참가 성공")))})
    public ResponseEntity<?> joinDebateRoomUser(@RequestParam("debateRoomNo") Long debateRoomNo, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        Long userNo = userTokenStorage.getToken(authorizationHeader);
        if (userNo == null) {
            return ErrorResponseHandler.get(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        try {
            DebateRoom debateRoom = debateRoomRepository.findById(debateRoomNo).orElseThrow(() -> new IllegalArgumentException("Debateroom을 찾을 수 없습니다."));
            UserInfo user = userRepository.findById(userNo).orElseThrow(() -> new IllegalArgumentException("User를 찾을 수 없습니다."));
            DebateRoomUser debateroomUser = new DebateRoomUser(debateRoom, user);
            debateroomUserRepository.save(debateroomUser);
        } catch (Exception e) {
            return ErrorResponseHandler.get(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("토론방 참가 성공");
    }
}
