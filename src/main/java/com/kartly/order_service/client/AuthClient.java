package com.kartly.order_service.client;

import com.kartly.order_service.dto.AuthResponse;
import com.kartly.order_service.dto.LoginRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", contextId = "authClient")
public interface AuthClient {

    @PostMapping("/auth/login")
    AuthResponse login(@RequestBody LoginRequest request);
}
