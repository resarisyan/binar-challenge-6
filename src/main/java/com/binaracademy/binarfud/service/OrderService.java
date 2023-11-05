package com.binaracademy.binarfud.service;

import com.binaracademy.binarfud.dto.request.OrderRequest;
import com.binaracademy.binarfud.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse makeOrder(OrderRequest request);
    Page<OrderResponse> getAllOrderWithPagination(String username, Pageable pageable);
}
