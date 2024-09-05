package org.dorandoran.dorandoran_backend.user;

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
public class UserController {


    @Autowired
    private UserRepository userRepository;


    @PostMapping("/signup")
    public void signUp(
            @RequestParam("name") String name,
            @RequestParam("userId") String userId,
            @RequestParam("password") String password,
            @RequestParam("nickName") String nickName) {

        UserInfo userInfo = new UserInfo(
                name,
                userId,
                BCrypt.hashpw(password, BCrypt.gensalt()),
                nickName,
                LocalDateTime.now()
        );

        userRepository.save(userInfo);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam("userId") String userId,
            @RequestParam("password") String password) {
        UserInfo userInfo = userRepository.findByUserId(userId);
        if (userInfo == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 실패: 사용자 없음");
        }
        if (!BCrypt.checkpw(password, userInfo.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 실패: 비밀번호 불일치");
        }
        // JWT 생성
        String token = JwtUtil.generateToken(userId);

        // 응답 헤더에 JWT 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        // 로그인 성공 메시지와 함께 응답
        return ResponseEntity.ok()
                .headers(headers)
                .body("로그인 성공");
    }

}
