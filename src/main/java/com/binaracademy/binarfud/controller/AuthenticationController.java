package com.binaracademy.binarfud.controller;

import com.binaracademy.binarfud.dto.request.LoginRequest;
import com.binaracademy.binarfud.dto.request.RegisterCustomerRequest;
import com.binaracademy.binarfud.dto.request.RegisterMerchantRequest;
import com.binaracademy.binarfud.dto.response.LoginResponse;
import com.binaracademy.binarfud.dto.response.RefreshTokenResponse;
import com.binaracademy.binarfud.dto.response.RegisterCustomerResponse;
import com.binaracademy.binarfud.dto.response.RegisterMerchantResponse;
import com.binaracademy.binarfud.dto.response.base.APIResultResponse;
import com.binaracademy.binarfud.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/auth", produces = "application/json")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register/customer")
    public ResponseEntity<APIResultResponse<RegisterCustomerResponse>> registerCustomer(@RequestBody @Valid RegisterCustomerRequest request) {
        RegisterCustomerResponse customerResponse = authenticationService.registerCustomer(request);
        APIResultResponse<RegisterCustomerResponse> responseDTO =  new APIResultResponse<>(
                HttpStatus.CREATED,
                "Customer successfully created",
                customerResponse
        );
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/register/merchant")
    public ResponseEntity<APIResultResponse<RegisterMerchantResponse>> registerMerchant(@RequestBody @Valid RegisterMerchantRequest request) {
        RegisterMerchantResponse merchantResponse = authenticationService.registerMerchant(request);
        APIResultResponse<RegisterMerchantResponse> responseDTO =  new APIResultResponse<>(
                HttpStatus.CREATED,
                "Merchant successfully created",
                merchantResponse
        );
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<APIResultResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse loginResponse = authenticationService.login(request);
        APIResultResponse<LoginResponse> responseDTO =  new APIResultResponse<>(
                HttpStatus.CREATED,
                "Login success",
                loginResponse
        );
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<APIResultResponse<RefreshTokenResponse>> refreshToken(HttpServletRequest request, HttpServletResponse response)  {
        RefreshTokenResponse refreshTokenResponse = authenticationService.refreshToken(request, response);
        APIResultResponse<RefreshTokenResponse> responseDTO =  new APIResultResponse<>(
                HttpStatus.CREATED,
                "Refresh token success",
                refreshTokenResponse
        );
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}
