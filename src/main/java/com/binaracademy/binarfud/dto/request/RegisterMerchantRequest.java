package com.binaracademy.binarfud.dto.request;

import com.binaracademy.binarfud.validation.FieldExistence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterMerchantRequest {
    @NotBlank
    @FieldExistence(tableName = "users", fieldName = "username", shouldExist = false, message = "Username already exists")
    private String username;

    @NotBlank
    @Email
    @FieldExistence(tableName = "users", fieldName = "email", shouldExist = false, message = "Email already exists")
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    @FieldExistence(tableName = "merchant", fieldName = "merchant_name", shouldExist = false, message = "Merchant name already exists")
    private String merchantName;

    @NotBlank
    private String merchantLocation;

    @NotNull
    private Boolean open;
}
