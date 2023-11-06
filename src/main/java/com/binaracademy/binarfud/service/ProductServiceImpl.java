package com.binaracademy.binarfud.service;

import com.binaracademy.binarfud.config.JwtService;
import com.binaracademy.binarfud.dto.request.CreateProductRequest;
import com.binaracademy.binarfud.dto.request.UpdateProductReqeust;
import com.binaracademy.binarfud.dto.response.MerchantResponse;
import com.binaracademy.binarfud.dto.response.ProductResponse;
import com.binaracademy.binarfud.entity.Merchant;
import com.binaracademy.binarfud.entity.Product;
import com.binaracademy.binarfud.entity.User;
import com.binaracademy.binarfud.exception.AccessDeniedException;
import com.binaracademy.binarfud.exception.DataNotFoundException;
import com.binaracademy.binarfud.exception.ServiceBusinessException;
import com.binaracademy.binarfud.repository.MerchantRepository;
import com.binaracademy.binarfud.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final MerchantRepository merchantRepository;
    private final JwtService jwtService;
    private static final String PRODUCT_NOT_FOUND = "Product not found";
    private static final String MERCHANT_NOT_FOUND = "Merchant not found";
    private static final String ACCESS_DENIED = "You are not allowed to access this product";
    @Override
    public ProductResponse addNewProduct(CreateProductRequest request) {
        ProductResponse productResponse;
        try{
            log.info("Adding new product");
            User user = jwtService.getUser();
            Merchant merchant = merchantRepository.findFirstByMerchantName(user.getMerchant().getMerchantName())
                    .orElseThrow(() -> new DataNotFoundException(MERCHANT_NOT_FOUND));
            Product product = Product.builder()
                    .productName(request.getProductName())
                    .merchant(merchant)
                    .price(request.getPrice())
                    .build();
            productRepository.save(product);
            productResponse = ProductResponse.builder()
                    .productName(product.getProductName())
                    .merchant(MerchantResponse.builder()
                            .merchantName(merchant.getMerchantName())
                            .merchantLocation(merchant.getMerchantLocation())
                            .open(merchant.getOpen())
                            .build())
                    .price(product.getPrice())
                    .build();
        } catch (Exception e) {
            log.error("Failed to add new product");
            throw new ServiceBusinessException("Failed to add new product");
        }

        log.info("Product {} successfully added", productResponse.getProductName());
        return productResponse;
    }
    @Override
    public void updateProduct(String productName, UpdateProductReqeust request) {
        try {
            log.info("Updating product");
            User user = jwtService.getUser();
            Product existingProduct = productRepository.findByProductName(productName).orElseThrow(() -> new DataNotFoundException(PRODUCT_NOT_FOUND));
            if (!existingProduct.getMerchant().getMerchantName().equals(user.getMerchant().getMerchantName())) {
                throw new AccessDeniedException(ACCESS_DENIED);
            }

            existingProduct.setProductName(request.getProductName());
            existingProduct.setPrice(request.getPrice());
            productRepository.save(existingProduct);
            log.info("Product {} successfully updated", existingProduct.getProductName());
        } catch (AccessDeniedException | DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to update product");
            throw new ServiceBusinessException("Failed to update product");
        }
    }
    @Override
    public void deleteProduct(String productName) {
        try {
            log.info("Deleting product");
            User user = jwtService.getUser();
            Product existingProduct = productRepository.findByProductName(productName).orElseThrow(() -> new DataNotFoundException(PRODUCT_NOT_FOUND));
            if (!existingProduct.getMerchant().getMerchantName().equals(user.getMerchant().getMerchantName())) {
                throw new AccessDeniedException(ACCESS_DENIED);
            }
            productRepository.delete(existingProduct);
            log.info("Product {} successfully deleted", existingProduct.getProductName());
        } catch (AccessDeniedException | DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete product");
            throw new ServiceBusinessException("Failed to delete product");
        }
    }
    @Override
    public ProductResponse getProductDetail(String productName) {
        try {
            log.info("Getting product detail");
            Product product = productRepository.findByProductName(productName).orElseThrow(() -> new DataNotFoundException(PRODUCT_NOT_FOUND));
            return ProductResponse.builder()
                    .productName(product.getProductName())
                    .merchant(MerchantResponse.builder()
                            .merchantName(product.getMerchant().getMerchantName())
                            .merchantLocation(product.getMerchant().getMerchantLocation())
                            .open(product.getMerchant().getOpen())
                            .build())
                    .price(product.getPrice())
                    .build();
        } catch (AccessDeniedException | DataNotFoundException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("Failed to get product detail");
            throw new ServiceBusinessException("Failed to get product detail");
        }
    }
    @Override
    public Page<ProductResponse> getProductsWithPagination(Pageable pageable) {
        try {
            Page<Product> productPage = Optional.of(productRepository.findAll(pageable))
                    .filter(Page::hasContent)
                    .orElseThrow(() -> new DataNotFoundException(PRODUCT_NOT_FOUND));
            return  productPage.map(product -> ProductResponse.builder()
                    .productName(product.getProductName())
                    .merchant(MerchantResponse.builder()
                            .merchantName(product.getMerchant().getMerchantName())
                            .merchantLocation(product.getMerchant().getMerchantLocation())
                            .open(product.getMerchant().getOpen())
                            .build())
                    .price(product.getPrice())
                    .build());
        }  catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get all product");
            throw new ServiceBusinessException(e.getMessage());
        }
    }
}
