package com.company.restaurantplatform.shared.security;

import com.company.restaurantplatform.core.domain.entity.RestaurantUser;
import com.company.restaurantplatform.core.domain.enums.MembershipStatus;
import com.company.restaurantplatform.core.repository.RestaurantUserRepository;
import com.company.restaurantplatform.core.repository.RestaurantUserRoleRepository;
import com.company.restaurantplatform.shared.exception.BusinessException;
import com.company.restaurantplatform.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Transactional
public class RestaurantAccessGuard {

    public static final String ROLE_ADMIN = "RESTAURANT_ADMIN";
    public static final String ROLE_WAITER = "WAITER";
    public static final String ROLE_MUSIC_MANAGER = "MUSIC_MANAGER";

    private final RestaurantUserRepository restaurantUserRepository;
    private final RestaurantUserRoleRepository restaurantUserRoleRepository;

    public void requireRestaurantAdmin(Long userId, Long restaurantId) {
        requireAnyRole(userId, restaurantId, Set.of(ROLE_ADMIN));
    }

    public void requireMusicAccess(Long userId, Long restaurantId) {
        requireAnyRole(userId, restaurantId, Set.of(ROLE_ADMIN, ROLE_MUSIC_MANAGER));
    }

    public void requireWaiter(Long userId, Long restaurantId) {
        requireAnyRole(userId, restaurantId, Set.of(ROLE_WAITER));
    }

    public void requireWaiterMembership(Long userId, Long restaurantId, Long restaurantUserId) {
        RestaurantUser restaurantUser = requireMembership(userId, restaurantId);
        if (!restaurantUser.getId().equals(restaurantUserId)) {
            throw new BusinessException("Authenticated waiter cannot use another restaurant membership");
        }

        boolean hasWaiterRole = restaurantUserRoleRepository.findAllByRestaurantUserId(restaurantUserId)
                .stream()
                .anyMatch(restaurantUserRole -> ROLE_WAITER.equals(restaurantUserRole.getRole().getCode()));
        if (!hasWaiterRole) {
            throw new BusinessException("Authenticated user does not have waiter role");
        }
    }

    private void requireAnyRole(Long userId, Long restaurantId, Set<String> allowedRoles) {
        RestaurantUser restaurantUser = requireMembership(userId, restaurantId);
        boolean hasRole = restaurantUserRoleRepository.findAllByRestaurantUserId(restaurantUser.getId())
                .stream()
                .map(restaurantUserRole -> restaurantUserRole.getRole().getCode())
                .anyMatch(allowedRoles::contains);
        if (!hasRole) {
            throw new BusinessException("Authenticated user is not allowed for this restaurant action");
        }
    }

    private RestaurantUser requireMembership(Long userId, Long restaurantId) {
        RestaurantUser restaurantUser = restaurantUserRepository.findByRestaurantIdAndUserId(restaurantId, userId)
                .orElseThrow(() -> new NotFoundException("Restaurant membership not found"));
        if (restaurantUser.getMembershipStatus() != MembershipStatus.ACTIVE) {
            throw new BusinessException("Restaurant membership is not active");
        }
        return restaurantUser;
    }
}
