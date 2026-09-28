package com.kartly.order_service.config;

import com.kartly.order_service.client.AuthClient;
import com.kartly.order_service.dto.AuthResponse;
import com.kartly.order_service.dto.LoginRequest;
import com.kartly.order_service.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ServiceTokenProvider {

    private final AuthClient authClient;
    private final JwtService jwtService;

    @Value("${service.account.email}")
    private String serviceEmail;

    @Value("${service.account.password}")
    private String servicePassword;

    private String cachedToken;

    public ServiceTokenProvider(AuthClient authClient, JwtService jwtService){
        this.authClient = authClient;
        this.jwtService = jwtService;
    }

    public synchronized String getServiceToken() {
        if (cachedToken == null || jwtService.isTokenValid(cachedToken)){
            LoginRequest request = new LoginRequest();
            request.setEmail(serviceEmail);
            request.setPassword(servicePassword);

            AuthResponse response = authClient.login(request);
            cachedToken = response.getAccessToken();
        }
        return cachedToken;
    }
}
