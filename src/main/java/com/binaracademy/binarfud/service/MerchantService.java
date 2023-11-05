package com.binaracademy.binarfud.service;

import com.binaracademy.binarfud.dto.request.UpdateStatusMerchantRequest;
import com.binaracademy.binarfud.dto.response.MerchantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MerchantService {
    void updateStatusMerchant(UpdateStatusMerchantRequest request);
    Page<MerchantResponse> getAllMerchantByOpen(Boolean open, Pageable pageable);
}
