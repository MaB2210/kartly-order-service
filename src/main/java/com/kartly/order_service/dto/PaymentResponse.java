package com.kartly.order_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private String status;
}
