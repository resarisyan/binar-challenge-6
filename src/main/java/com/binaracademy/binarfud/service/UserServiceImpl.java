package com.binaracademy.binarfud.service;

import com.binaracademy.binarfud.config.JwtService;
import com.binaracademy.binarfud.dto.request.UpdateUserRequest;
import com.binaracademy.binarfud.entity.User;
import com.binaracademy.binarfud.exception.DataNotFoundException;
import com.binaracademy.binarfud.exception.ServiceBusinessException;
import com.binaracademy.binarfud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    public void updateUser(UpdateUserRequest request) {
        try {
            log.info("Updating user");
            User user = jwtService.getUser();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            userRepository.save(user);
            log.info("User {} successfully updated", user.getUsername());
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to update user");
            throw new ServiceBusinessException("Failed to update user");
        }
    }

    public void deleteUser() {
        log.info("Deleting user");
        try {
            User user = jwtService.getUser();
            userRepository.delete(user);
            log.info("User {} successfully deleted", user.getUsername());
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to delete user");
            throw new ServiceBusinessException("Failed to delete user");
        }
    }
}
