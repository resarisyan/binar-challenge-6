package com.binaracademy.binarfud.service;

import com.binaracademy.binarfud.config.JwtService;
import com.binaracademy.binarfud.dto.request.LoginRequest;
import com.binaracademy.binarfud.dto.request.RegisterCustomerRequest;
import com.binaracademy.binarfud.dto.request.RegisterMerchantRequest;
import com.binaracademy.binarfud.dto.response.LoginResponse;
import com.binaracademy.binarfud.dto.response.RefreshTokenResponse;
import com.binaracademy.binarfud.dto.response.RegisterCustomerResponse;
import com.binaracademy.binarfud.dto.response.RegisterMerchantResponse;
import com.binaracademy.binarfud.entity.Merchant;
import com.binaracademy.binarfud.entity.Token;
import com.binaracademy.binarfud.entity.User;
import com.binaracademy.binarfud.enumeration.EnumRole;
import com.binaracademy.binarfud.enumeration.EnumTokenType;
import com.binaracademy.binarfud.exception.DataConflictException;
import com.binaracademy.binarfud.exception.DataNotFoundException;
import com.binaracademy.binarfud.exception.ServiceBusinessException;
import com.binaracademy.binarfud.repository.MerchantRepository;
import com.binaracademy.binarfud.repository.TokenRepository;
import com.binaracademy.binarfud.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MerchantRepository merchantRepository;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findFirstByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return LoginResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
    public RegisterCustomerResponse registerCustomer(RegisterCustomerRequest request) throws ServiceBusinessException {
        RegisterCustomerResponse customerResponse;
        try{
            log.info("Adding new customer");
            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(EnumRole.CUSTOMER)
                    .build();
            userRepository.save(user);
            customerResponse = RegisterCustomerResponse.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build();
        }
        catch (DataConflictException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to add new customer");
            throw new ServiceBusinessException("Failed to add new customer");
        }

        log.info("Customer {} successfully added", customerResponse.getUsername());
        return customerResponse;
    }

    @Transactional
    public RegisterMerchantResponse registerMerchant(RegisterMerchantRequest request) throws ServiceBusinessException {
        RegisterMerchantResponse merchantResponse;
        try{
            log.info("Adding new merchant");
            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(EnumRole.MERCHANT)
                    .build();
            user = userRepository.save(user);

            Merchant merchant = Merchant.builder()
                    .merchantName(request.getMerchantName())
                    .merchantLocation(request.getMerchantLocation())
                    .open(request.getOpen())
                    .user(user)
                    .build();
            merchantRepository.save(merchant);

            merchantResponse = RegisterMerchantResponse.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .merchantName(merchant.getMerchantName())
                    .merchantLocation(merchant.getMerchantLocation())
                    .open(merchant.getOpen())
                    .build();
        }
        catch (DataConflictException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to add new merchant");
            throw new ServiceBusinessException("Failed to add new merchant");
        }

        log.info("Merchant {} successfully added", merchantResponse.getUsername());
        return merchantResponse;
    }

    public RefreshTokenResponse refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        RefreshTokenResponse refreshTokenResponse;

        try {
           final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
           final String refreshToken;
           final String username;
           if (authHeader == null || !authHeader.startsWith("Bearer ")) {
               throw new DataNotFoundException("Refresh token is missing");
           }
           refreshToken = authHeader.substring(7);
           username = jwtService.extractUsername(refreshToken);
           if (username == null) {
               throw new ServiceBusinessException("Refresh token is invalid");
           }

           var user = this.userRepository.findFirstByUsername(username)
                       .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                   var accessToken = jwtService.generateToken(user);
                   revokeAllUserTokens(user);
                   saveUserToken(user, accessToken);
                   refreshTokenResponse =  RefreshTokenResponse.builder()
                           .accessToken(accessToken)
                           .build();
            } else {
                   throw new ServiceBusinessException("Refresh token is invalid");
            }
        } catch (DataNotFoundException | ServiceBusinessException e) {
            throw e;
        } catch (Exception e) {
              throw new ServiceBusinessException("Failed to refresh token");
        }
         return refreshTokenResponse;
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(EnumTokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
}
