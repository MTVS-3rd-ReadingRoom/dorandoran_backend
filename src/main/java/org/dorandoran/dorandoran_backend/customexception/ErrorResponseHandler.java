package org.dorandoran.dorandoran_backend.customexception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponseHandler {

    public static ResponseEntity<?> get(HttpStatus code, String message){
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("error", message);
        return ResponseEntity.status(code).contentType(MediaType.APPLICATION_JSON).body(responseBody);
    }
}

