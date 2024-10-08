package org.dorandoran.dorandoran_backend.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.dorandoran.dorandoran_backend.customexception.ErrorResponseHandler;
import org.dorandoran.dorandoran_backend.component.UserTokenStorage;
import org.dorandoran.dorandoran_backend.metric.MetricService;
import org.dorandoran.dorandoran_backend.metric.RequestClassification;
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
    private final MetricService metricService;

    @Autowired
    public UserController(UserRepository userRepository, UserTokenStorage userTokenStorage, MetricService metricService) {
        this.userRepository = userRepository;
        this.userTokenStorage = userTokenStorage;
        this.metricService = metricService;
    }


    @PostMapping("/signup")
    @Operation(summary = "회원가입")
    public ResponseEntity<?> signUp(@RequestParam("name") String name, @RequestParam("userId") String userId, @RequestParam("password") String password, @RequestParam("email") String email) {

        UserInfo userInfo = new UserInfo(name, userId, BCrypt.hashpw(password, BCrypt.gensalt()), email, LocalDateTime.now());

        try{
            userRepository.save(userInfo);
            metricService.incrementRequestCount(RequestClassification.USER.label(), "sign up");
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
        }catch (Exception e){
            metricService.incrementErrorCount(RequestClassification.USER.label(), "sign up");
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
            metricService.incrementErrorCount(RequestClassification.USER.label(), "not found login");
            return ErrorResponseHandler.get(HttpStatus.UNAUTHORIZED, "사용자 없음");
        }
        if (!BCrypt.checkpw(password, userInfo.getPassword())) {
            metricService.incrementErrorCount(RequestClassification.USER.label(), "password not match");
            return ErrorResponseHandler.get(HttpStatus.UNAUTHORIZED, "비밀번호 불일치");
        }

        String token = JwtUtil.generateToken(userId);
        userTokenStorage.storeToken(token,userInfo.getNo());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        metricService.incrementRequestCount(RequestClassification.USER.label(), "login success");
        return ResponseEntity.ok().headers(headers).body("로그인 성공");
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다", content = @Content(mediaType = "application/json", schema = @Schema(example = "{ \"error\": \"로그인이 필요합니다\" }"))),
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(mediaType = "text/plain", schema = @Schema(example = "로그아웃 성공")))})

    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null) {
            metricService.incrementErrorCount(RequestClassification.USER.label(), "not found token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        userTokenStorage.removeToken(authorizationHeader);
        metricService.incrementRequestCount(RequestClassification.USER.label(), "logout success");
        return ResponseEntity.ok("로그아웃 성공");
    }

}
