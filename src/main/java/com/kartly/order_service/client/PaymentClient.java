package com.kartly.order_service.client;

import com.kartly.order_service.config.FeignClientConfig;
import com.kartly.order_service.dto.PaymentResponse;
import com.kartly.order_service.dto.ProcessPaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", configuration = FeignClientConfig.class)
public interface PaymentClient {
    @PostMapping("/payments")
    PaymentResponse processPayment(@RequestBody ProcessPaymentRequest request);
}
