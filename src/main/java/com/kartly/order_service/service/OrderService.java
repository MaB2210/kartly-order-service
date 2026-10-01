package com.kartly.order_service.service;

import com.kartly.order_service.client.PaymentClient;
import com.kartly.order_service.client.ProductClient;
import com.kartly.order_service.client.UserClient;
import com.kartly.order_service.dto.*;
import com.kartly.order_service.entity.OrderEntity;
import com.kartly.order_service.entity.OrderItemEntity;
import com.kartly.order_service.entity.OrderStatus;
import com.kartly.order_service.exception.PaymentFailedException;
import com.kartly.order_service.exception.ResourceNotFoundException;
import com.kartly.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final UserClient userClient;
    private final PaymentClient paymentClient;
    private final OrderEventPublisher orderEventPublisher;

    public OrderService(OrderRepository orderRepository, ProductClient productClient, UserClient userClient, PaymentClient paymentClient, OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.userClient = userClient;
        this.paymentClient = paymentClient;
        this.orderEventPublisher = orderEventPublisher;
    }

    public List<OrderEntity> getAllOrders() {
        return orderRepository.findAll();
    }

    public OrderEntity getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }

    public List<OrderEntity> getOrdersForUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public OrderEntity createOrder(CreateOrderRequest request) {
        UserResponse user = userClient.getUserById(request.getUserId());
        if (user == null) {
            throw new ResourceNotFoundException("User not found: " + request.getUserId());
        }

        OrderEntity order = new OrderEntity();
        order.setUserId(user.getId());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.getItems()) {
            ProductResponse product = productClient.getProductById(itemRequest.getProductId());

            OrderItemEntity item = new OrderItemEntity();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setQuantity(itemRequest.getQuantity());
            item.setOrder(order);
            order.getItems().add(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        order.setTotalAmount(total);

        OrderEntity savedOrder = orderRepository.save(order);

        ProcessPaymentRequest paymentRequest = new ProcessPaymentRequest();
        paymentRequest.setOrderId(savedOrder.getId());
        paymentRequest.setAmount(total);

        try{
            paymentClient.processPayment(paymentRequest);
            savedOrder.setStatus(OrderStatus.CONFIRMED);

            OrderStatusEvent event = new OrderStatusEvent();
            event.setOrderId(savedOrder.getId());
            event.setUserId(savedOrder.getUserId());
            event.setTotalAmount(savedOrder.getTotalAmount());
            event.setStatus(OrderStatus.CONFIRMED.name());
            orderEventPublisher.publishOrderStatus(event);

        } catch(Exception ex){
            savedOrder.setStatus(OrderStatus.PAYMENT_FAILED);
            orderRepository.save(savedOrder);

            OrderStatusEvent event = new OrderStatusEvent();
            event.setOrderId(savedOrder.getId());
            event.setUserId(savedOrder.getUserId());
            event.setTotalAmount(savedOrder.getTotalAmount());
            event.setStatus(OrderStatus.PAYMENT_FAILED.name());
            orderEventPublisher.publishOrderStatus(event);

            throw new PaymentFailedException("Payment failed for order" +savedOrder.getId() + ": " + ex.getMessage());
        }

        return orderRepository.save(savedOrder);
    }
}
