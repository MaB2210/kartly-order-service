package com.kartly.order_service.service;

import com.kartly.order_service.dto.OrderStatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderStatus(OrderStatusEvent event){
        String key = event.getOrderId().toString();
        kafkaTemplate.send("order-events",key,event);
        log.info("Published OrderStatusEvent for order {} with status {}", event.getOrderId(), event.getStatus());
    }
}
