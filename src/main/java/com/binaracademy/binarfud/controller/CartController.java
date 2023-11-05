package com.binaracademy.binarfud.controller;

import com.binaracademy.binarfud.dto.request.CreateCartRequest;
import com.binaracademy.binarfud.dto.response.CartResponse;
import com.binaracademy.binarfud.dto.response.base.APIResultResponse;
import com.binaracademy.binarfud.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/carts", produces = "application/json")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping
    @Schema(name = "CreateCartRequest", description = "Create cart request body")
    @Operation(summary = "Endpoint to handle create new cart")
    public ResponseEntity<APIResultResponse<CartResponse>> createNewUser(@RequestBody @Valid CreateCartRequest createCartRequest) {
        CartResponse cartResponse = cartService.addNewCart(createCartRequest);
        APIResultResponse<CartResponse> responseDTO =  new APIResultResponse<>(
                HttpStatus.CREATED,
                "Merchant successfully created",
                cartResponse
        );
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}
