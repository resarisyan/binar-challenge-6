package com.binaracademy.binarfud.service;

import com.binaracademy.binarfud.dto.request.LoginRequest;
import com.binaracademy.binarfud.dto.request.RegisterCustomerRequest;
import com.binaracademy.binarfud.dto.request.RegisterMerchantRequest;
import com.binaracademy.binarfud.dto.response.LoginResponse;
import com.binaracademy.binarfud.dto.response.RefreshTokenResponse;
import com.binaracademy.binarfud.dto.response.RegisterCustomerResponse;
import com.binaracademy.binarfud.dto.response.RegisterMerchantResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    RegisterCustomerResponse registerCustomer(RegisterCustomerRequest request);
    RegisterMerchantResponse registerMerchant(RegisterMerchantRequest request);

    LoginResponse login(LoginRequest request);
    RefreshTokenResponse refreshToken(HttpServletRequest request, HttpServletResponse response);
}
