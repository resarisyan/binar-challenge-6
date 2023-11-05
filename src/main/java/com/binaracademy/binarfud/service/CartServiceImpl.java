package com.binaracademy.binarfud.service;

import com.binaracademy.binarfud.config.JwtService;
import com.binaracademy.binarfud.dto.request.CreateCartRequest;
import com.binaracademy.binarfud.dto.response.CartResponse;
import com.binaracademy.binarfud.dto.response.ProductResponse;
import com.binaracademy.binarfud.entity.Cart;
import com.binaracademy.binarfud.entity.Product;
import com.binaracademy.binarfud.entity.User;
import com.binaracademy.binarfud.exception.DataNotFoundException;
import com.binaracademy.binarfud.exception.ServiceBusinessException;
import com.binaracademy.binarfud.repository.CartRepository;
import com.binaracademy.binarfud.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final JwtService jwtService;
    private static final String PRODUCT_NOT_FOUND = "Product not found";
    public CartResponse addNewCart(CreateCartRequest cart) {
        CartResponse cartResponse;
        try {
            User user = jwtService.getUser();
            Product product = productRepository.findByProductName(
                    cart.getProductName()).orElseThrow(() -> new DataNotFoundException(PRODUCT_NOT_FOUND)
            );
            Cart existingCart = cartRepository.findByProductAndUser(product, user);
            if (existingCart != null) {
                int newQuantity = existingCart.getQuantity() + cart.getQuantity();
                existingCart.setQuantity(newQuantity);
                double newTotalPrice = existingCart.getTotalPrice() + (product.getPrice() * cart.getQuantity());
                existingCart.setTotalPrice(newTotalPrice);
                cartRepository.save(existingCart);
            } else {
                Cart newCart = Cart.builder()
                        .quantity(cart.getQuantity())
                        .totalPrice(product.getPrice() * cart.getQuantity())
                        .product(product)
                        .user(user)
                        .build();
                cartRepository.save(newCart);
            }
            cartResponse = CartResponse.builder()
                    .product(ProductResponse.builder()
                            .productName(product.getProductName())
                            .price(product.getPrice())
                            .build())
                    .quantity(cart.getQuantity())
                    .totalPrice(product.getPrice() * cart.getQuantity())
                    .build();
            log.info("Cart {} successfully added", cartResponse);
            return cartResponse;
        } catch (DataNotFoundException e) {
            throw new DataNotFoundException(PRODUCT_NOT_FOUND);
        }
        catch (Exception e) {
            throw new ServiceBusinessException("Failed to add new cart");
        }
    }
}
