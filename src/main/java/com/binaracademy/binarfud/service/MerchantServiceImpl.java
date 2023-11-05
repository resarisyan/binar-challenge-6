package com.binaracademy.binarfud.service;

import com.binaracademy.binarfud.config.JwtService;
import com.binaracademy.binarfud.dto.request.UpdateStatusMerchantRequest;
import com.binaracademy.binarfud.dto.response.MerchantResponse;
import com.binaracademy.binarfud.entity.Merchant;
import com.binaracademy.binarfud.entity.User;
import com.binaracademy.binarfud.exception.DataNotFoundException;
import com.binaracademy.binarfud.exception.ServiceBusinessException;
import com.binaracademy.binarfud.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService{
    private final MerchantRepository merchantRepository;
    private final JwtService jwtService;
    @Override
    public void updateStatusMerchant(UpdateStatusMerchantRequest request) {
        try {
            log.info("Updating merchant");
            User user = jwtService.getUser();
            Merchant merchant = merchantRepository.findFirstByMerchantName(user.getMerchant().getMerchantName())
                    .orElseThrow(() -> new DataNotFoundException("Merchant not found"));
            merchant.setOpen(request.getOpen());
            merchantRepository.save(merchant);
            log.info("Merchant {} successfully updated", merchant.getMerchantName());
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to update user");
            throw new ServiceBusinessException("Failed to update user");
        }
    }

    @Override
    public Page<MerchantResponse> getAllMerchantByOpen(Boolean open, Pageable pageable) {
        try {
            log.info("Getting all merchant");
            Page<Merchant> merchantPage = Optional.of(merchantRepository.findByOpen(open, pageable))
                    .filter(Page::hasContent)
                    .orElseThrow(() -> new DataNotFoundException("No merchant found"));
            return  merchantPage.map(merchant -> MerchantResponse.builder()
                    .merchantName(merchant.getMerchantName())
                    .merchantLocation(merchant.getMerchantLocation())
                    .open(merchant.getOpen())
                    .build());
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get all merchant");
            throw new ServiceBusinessException("Failed to get all merchant");
        }
    }
}
