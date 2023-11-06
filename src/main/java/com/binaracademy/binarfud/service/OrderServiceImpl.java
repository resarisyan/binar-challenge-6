package com.binaracademy.binarfud.service;

import com.binaracademy.binarfud.config.JwtService;
import com.binaracademy.binarfud.dto.request.OrderRequest;
import com.binaracademy.binarfud.dto.response.*;
import com.binaracademy.binarfud.entity.*;
import com.binaracademy.binarfud.exception.DataNotFoundException;
import com.binaracademy.binarfud.exception.ServiceBusinessException;
import com.binaracademy.binarfud.repository.CartRepository;
import com.binaracademy.binarfud.repository.OrderDetailRepository;
import com.binaracademy.binarfud.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final JwtService jwtService;

    @Override
    public OrderResponse makeOrder(OrderRequest request) {
        try{
            User user = jwtService.getUser();
            List<Cart> carts = cartRepository.findByUser(user);
            if (carts.isEmpty()) {
                throw new DataNotFoundException("Cart is empty");
            }
            Order order = Order.builder()
                    .user(user)
                    .completed(false)
                    .destinationAddress(request.getDestinationAddress())
                    .note(request.getNote())
                    .orderTime(new Date())
                    .build();
            Order newOrder = orderRepository.save(order);
            List<OrderDetail> orderDetails = new ArrayList<>();
            carts.forEach(cart -> {
                OrderDetail orderDetail = OrderDetail.builder()
                        .order(newOrder)
                        .product(cart.getProduct())
                        .quantity(cart.getQuantity())
                        .totalPrice(cart.getTotalPrice())
                        .build();
                OrderDetail newOrderDetail =  orderDetailRepository.save(orderDetail);
                orderDetails.add(newOrderDetail);
            });
            order.setOrderDetails(orderDetails);
            return OrderResponse.builder()
                    .orderTime(order.getOrderTime())
                    .destinationAddress(order.getDestinationAddress())
                    .note(order.getNote())
                    .completed(order.getCompleted())
                    .orderDetails(
                            order.getOrderDetails().stream().map(orderDetail -> OrderDetailResponse.builder()
                                            .product(
                                                    ProductResponse.builder()
                                                            .productName(orderDetail.getProduct().getProductName())
                                                            .price(orderDetail.getProduct().getPrice())
                                                            .build()
                                            )
                                    .quantity(orderDetail.getQuantity())
                                    .totalPrice(orderDetail.getTotalPrice())
                                    .build()).toList()
                    )
                    .build();
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to make order");
            throw new ServiceBusinessException(e.getMessage());
        }
    }

    @Override
    public Page<OrderResponse> getAllOrderWithPagination(String username, Pageable pageable) {
        try {
            log.info("Getting all order");
            User user = jwtService.getUser();
            Page<Order> orderPage = Optional.of(orderRepository.findAllByUser(user, pageable))
                    .filter(Page::hasContent)
                    .orElseThrow(() -> new DataNotFoundException("No order found"));
            return  orderPage.map(order -> OrderResponse.builder()
                    .orderTime(order.getOrderTime())
                    .destinationAddress(order.getDestinationAddress())
                    .note(order.getNote())
                    .completed(order.getCompleted())
                    .orderDetails(
                            order.getOrderDetails().stream().map(orderDetail -> OrderDetailResponse.builder()
                                            .product(
                                                    ProductResponse.builder()
                                                            .productName(orderDetail.getProduct().getProductName())
                                                            .price(orderDetail.getProduct().getPrice())
                                                            .build()
                                            )
                                    .quantity(orderDetail.getQuantity())
                                    .totalPrice(orderDetail.getTotalPrice())
                                    .build()).toList()
                    )
                    .build());
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get all order");
            throw new ServiceBusinessException("Failed to get all order");
        }
    }

}
