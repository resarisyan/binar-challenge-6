package com.binaracademy.binarfud.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterMerchantResponse {
    private String username;
    private String email;
    private String merchantName;
    private String merchantLocation;
    private Boolean open;
}
