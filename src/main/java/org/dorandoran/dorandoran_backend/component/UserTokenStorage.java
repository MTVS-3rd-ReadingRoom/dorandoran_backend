package org.dorandoran.dorandoran_backend.component;

import lombok.extern.slf4j.Slf4j;
import org.dorandoran.dorandoran_backend.utils.JwtUtil;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class UserTokenStorage {

    public final Map<String, Long> tokenStorage;

    public UserTokenStorage() {
        this.tokenStorage = new ConcurrentHashMap<>();
    }

    // Bearer 제거
    public String removeBearer(String token) {
        if (token == null) {
            log.error("removeToken is null");
            return null;
        }
        if (token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    // 토큰 저장
    public void storeToken(String token, Long userNo) {
        if (tokenStorage.containsValue(userNo)) {
            tokenStorage.remove(getKeyByValue(tokenStorage, userNo));
        }
        tokenStorage.put(token, userNo);
    }

    // 토큰 조회
    public Long getToken(String token) {
        if (token == null) {
            log.error("getToken error: is null");
            return null;
        }
        return tokenStorage.get(removeBearer(token));
    }

    private static <K, V> K getKeyByValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null; // 값이 존재하지 않을 경우
    }

    // 토큰 삭제
    public void removeToken(String token) {
        tokenStorage.remove((token));
    }
}
