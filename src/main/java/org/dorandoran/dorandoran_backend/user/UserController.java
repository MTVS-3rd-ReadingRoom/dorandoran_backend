package org.dorandoran.dorandoran_backend.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.dorandoran.dorandoran_backend.customexception.ErrorResponseHandler;
import org.dorandoran.dorandoran_backend.component.UserTokenStorage;
import org.dorandoran.dorandoran_backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.mindrot.jbcrypt.BCrypt;


import java.time.LocalDateTime;

@RestController
@Tag(name = "사용자 API")
public class UserController {

    private final UserRepository userRepository;
    private final UserTokenStorage userTokenStorage;


    @Autowired
    public UserController(UserRepository userRepository, UserTokenStorage userTokenStorage) {
        this.userRepository = userRepository;
        this.userTokenStorage = userTokenStorage;
    }


    @PostMapping("/signup")
    @Operation(summary = "회원가입")
    public ResponseEntity<?> signUp(@RequestParam("name") String name, @RequestParam("userId") String userId, @RequestParam("password") String password, @RequestParam("nickName") String nickName) {

        UserInfo userInfo = new UserInfo(name, userId, BCrypt.hashpw(password, BCrypt.gensalt()), nickName, LocalDateTime.now());

        try{
            userRepository.save(userInfo);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
        }catch (Exception e){
            return ErrorResponseHandler.get(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다.");
        }
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", responses = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "text/plain;charset=UTF-8",
                            schema = @Schema(type = "string", example = "로그인 성공"))
            )})
    public ResponseEntity<?> login(@RequestParam("userId") String userId, @RequestParam("password") String password) {
        UserInfo userInfo = userRepository.findByUserId(userId);
        if (userInfo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: 사용자 없음");
        }
        if (!BCrypt.checkpw(password, userInfo.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: 비밀번호 불일치");
        }
        // JWT 생성
        String token = JwtUtil.generateToken(userId);
        userTokenStorage.storeToken(token,userInfo.getNo());

        // 응답 헤더에 JWT 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        // 로그인 성공 메시지와 함께 응답
        return ResponseEntity.ok().headers(headers).body("로그인 성공");
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public void logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        userTokenStorage.removeToken(authorizationHeader);
    }

}
