package com.binaracademy.binarfud.repository;

import com.binaracademy.binarfud.entity.Merchant;
import com.binaracademy.binarfud.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, UUID> {
    Optional<Merchant> findFirstByMerchantName(String username);
    Optional<Merchant> findFirstByUser(User user);
    Page<Merchant> findByOpen(Boolean open, Pageable pageable);
}
