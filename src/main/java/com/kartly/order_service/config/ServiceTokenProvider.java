package com.kartly.order_service.config;

import com.kartly.order_service.client.AuthClient;
import com.kartly.order_service.dto.AuthResponse;
import com.kartly.order_service.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ServiceTokenProvider {

    private final AuthClient authClient;

    @Value("${service.account.email}")
    private String serviceEmail;

    @Value("${service.account.password}")
    private String servicePassword;

    private String cachedToken;

    public ServiceTokenProvider(AuthClient authClient){
        this.authClient = authClient;
    }

    public synchronized String getServiceToken() {
        if (cachedToken == null){
            LoginRequest request = new LoginRequest();
            request.setEmail(serviceEmail);
            request.setPassword(servicePassword);

            AuthResponse response = authClient.login(request);
            cachedToken = response.getAccessToken();
        }
        return cachedToken;
    }
}
