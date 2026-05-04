package com.company.restaurantplatform.identity.application;

import com.company.restaurantplatform.core.domain.entity.RestaurantUser;
import com.company.restaurantplatform.core.domain.entity.RestaurantUserRole;
import com.company.restaurantplatform.core.domain.entity.User;
import com.company.restaurantplatform.core.domain.enums.MembershipStatus;
import com.company.restaurantplatform.core.domain.enums.UserStatus;
import com.company.restaurantplatform.core.repository.RestaurantUserRepository;
import com.company.restaurantplatform.core.repository.RestaurantUserRoleRepository;
import com.company.restaurantplatform.core.repository.UserRepository;
import com.company.restaurantplatform.identity.api.dto.AccessibleRestaurantResponse;
import com.company.restaurantplatform.identity.api.dto.LoginRequest;
import com.company.restaurantplatform.identity.api.dto.LoginResponse;
import com.company.restaurantplatform.shared.exception.BusinessException;
import com.company.restaurantplatform.shared.security.JwtService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RestaurantUserRepository restaurantUserRepository;
    private final RestaurantUserRoleRepository restaurantUserRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new BusinessException("Invalid email or password"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("User is not active");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException("Invalid email or password");
        }

        List<AccessibleRestaurantResponse> restaurants = restaurantUserRepository.findAllByUserId(user.getId())
                .stream()
                .filter(restaurantUser -> restaurantUser.getMembershipStatus() == MembershipStatus.ACTIVE)
                .map(this::toAccessibleRestaurantResponse)
                .toList();

        return new LoginResponse(
                jwtService.generateToken(user.getId(), user.getEmail()),
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                restaurants
        );
    }

    private AccessibleRestaurantResponse toAccessibleRestaurantResponse(RestaurantUser restaurantUser) {
        List<String> roles = restaurantUserRoleRepository.findAllByRestaurantUserId(restaurantUser.getId())
                .stream()
                .map(RestaurantUserRole::getRole)
                .map(role -> role.getCode())
                .sorted()
                .toList();

        return new AccessibleRestaurantResponse(
                restaurantUser.getRestaurant().getId(),
                restaurantUser.getRestaurant().getName(),
                restaurantUser.getId(),
                roles
        );
    }
}
