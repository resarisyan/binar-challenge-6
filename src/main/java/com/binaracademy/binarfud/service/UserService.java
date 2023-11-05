package com.binaracademy.binarfud.service;

import com.binaracademy.binarfud.dto.request.UpdateUserRequest;

public interface UserService {
    void updateUser(UpdateUserRequest request);
    void deleteUser();
}
