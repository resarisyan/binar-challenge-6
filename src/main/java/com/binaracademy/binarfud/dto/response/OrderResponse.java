package com.binaracademy.binarfud.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
    private Date orderTime;
    private String destinationAddress;
    private String note;
    private Boolean completed;
    private RegisterCustomerResponse user;
    private List<OrderDetailResponse> orderDetails;
}
