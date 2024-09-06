package org.dorandoran.dorandoran_backend.component;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserTokenStorage {

    public final Map<String, Long> tokenStore;

    public UserTokenStorage() {
        this.tokenStore = new ConcurrentHashMap<>();
    }

    // 토큰 저장
    public void storeToken(String token, Long userNo) {
        if (tokenStore.containsKey(token)) {
            tokenStore.replace(token, userNo);
            return;
        }
        tokenStore.put(token, userNo);
    }

    // 토큰 조회
    public Long getToken(String token) {
        System.out.println(token);
        if (token == null){
            return null;
        }
        return tokenStore.get(token);
    }

    // 토큰 삭제
    public void removeToken(String token) {
        tokenStore.remove(token);
    }
}
