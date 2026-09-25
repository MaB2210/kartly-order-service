package com.kartly.order_service.service;

import com.kartly.order_service.client.ProductClient;
import com.kartly.order_service.client.UserClient;
import com.kartly.order_service.dto.CreateOrderRequest;
import com.kartly.order_service.dto.OrderItemRequest;
import com.kartly.order_service.dto.ProductResponse;
import com.kartly.order_service.dto.UserResponse;
import com.kartly.order_service.entity.OrderEntity;
import com.kartly.order_service.entity.OrderItemEntity;
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

    public OrderService(OrderRepository orderRepository, ProductClient productClient, UserClient userClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.userClient = userClient;
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
        return orderRepository.save(order);
    }
}
