package org.dorandoran.dorandoran_backend.debateroom;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.dorandoran.dorandoran_backend.ErrorResponseHandler;
import org.dorandoran.dorandoran_backend.component.UserTokenStorage;
import org.dorandoran.dorandoran_backend.user.UserInfo;
import org.dorandoran.dorandoran_backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "토론 API")
public class DebateroomUserController {

    private final DebateroomUserRepository debateroomUserRepository;
    private final UserTokenStorage userTokenStorage;
    private final DebateroomRepository debateroomRepository;
    private final UserRepository userRepository;

    @Autowired
    public DebateroomUserController(DebateroomUserRepository debateroomUserRepository, UserTokenStorage userTokenStorage, DebateroomRepository debateroomRepository, UserRepository userRepository) {
        this.debateroomUserRepository = debateroomUserRepository;
        this.userTokenStorage = userTokenStorage;
        this.debateroomRepository = debateroomRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/api/debate-room-user")
    @Operation(summary = "토론방 참가",
            parameters = {
                    @Parameter(name = "Authorization", description = "토큰", in = ParameterIn.HEADER, required = true, schema = @Schema(type = "string", example = "Bearer your-api-key")),
                    @Parameter(name = "debateroom_no", description = "토론방 번호", in = ParameterIn.QUERY, required = true, schema = @Schema(type = "integer", example = "1"))

            })
    public ResponseEntity<?> joinDebateroomUser(@RequestParam("debateroom_no") Long debateroomNo, @RequestHeader("Authorization") String authorizationHeader) {
        // String authorizationHeader = request.getHeader("Authorization");

        System.out.println(authorizationHeader);

        Long userNo = userTokenStorage.getToken(authorizationHeader);
        if (userNo == null) {
            return ErrorResponseHandler.get(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        try{
            Debateroom debateroom = debateroomRepository.findById(debateroomNo).orElseThrow(
                () -> new IllegalArgumentException("Debateroom을 찾을 수 없습니다."));
            UserInfo user = userRepository.findById(userNo).orElseThrow(
                () -> new IllegalArgumentException("User를 찾을 수 없습니다."));
            DebateroomUser debateroomUser = new DebateroomUser(debateroom, user);
            debateroomUserRepository.save(debateroomUser);
        }catch (Exception e){
            return ErrorResponseHandler.get(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("토론방 참가 성공");
    }
}
