package com.kartly.order_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class OrderStatusEvent {
    private Long orderId;
    private Long UserId;
    private BigDecimal totalAmount;
    private String status;
}
