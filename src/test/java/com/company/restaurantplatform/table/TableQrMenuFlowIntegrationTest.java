package com.company.restaurantplatform.table;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.restaurantplatform.core.domain.entity.Restaurant;
import com.company.restaurantplatform.core.domain.entity.RestaurantUser;
import com.company.restaurantplatform.core.domain.entity.RestaurantUserRole;
import com.company.restaurantplatform.core.domain.entity.Role;
import com.company.restaurantplatform.core.domain.entity.User;
import com.company.restaurantplatform.core.domain.enums.MembershipStatus;
import com.company.restaurantplatform.core.domain.enums.RestaurantStatus;
import com.company.restaurantplatform.core.domain.enums.UserStatus;
import com.company.restaurantplatform.core.repository.RestaurantRepository;
import com.company.restaurantplatform.core.repository.RestaurantUserRepository;
import com.company.restaurantplatform.core.repository.RestaurantUserRoleRepository;
import com.company.restaurantplatform.core.repository.RoleRepository;
import com.company.restaurantplatform.core.repository.UserRepository;
import com.company.restaurantplatform.identity.api.dto.LoginRequest;
import com.company.restaurantplatform.identity.api.dto.LoginResponse;
import com.company.restaurantplatform.menu.api.dto.CategoryResponse;
import com.company.restaurantplatform.menu.api.dto.CreateCategoryRequest;
import com.company.restaurantplatform.menu.api.dto.CreateProductRequest;
import com.company.restaurantplatform.menu.api.dto.ProductResponse;
import com.company.restaurantplatform.menu.api.dto.PublicMenuResponse;
import com.company.restaurantplatform.qr.api.dto.PublicQrResolveResponse;
import com.company.restaurantplatform.qr.api.dto.QrCodeResponse;
import com.company.restaurantplatform.shared.security.RestaurantAccessGuard;
import com.company.restaurantplatform.table.api.dto.CreateTableRequest;
import com.company.restaurantplatform.table.api.dto.TableResponse;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIfEnvironmentVariable(named = "DB_USERNAME", matches = ".+")
class TableQrMenuFlowIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantUserRepository restaurantUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RestaurantUserRoleRepository restaurantUserRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldManageTablesGenerateQrAndServePublicMenu() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        Restaurant restaurant = new Restaurant();
        restaurant.setName("Phase 2 Restaurant " + suffix);
        restaurant.setSlug("phase-2-restaurant-" + suffix);
        restaurant.setStatus(RestaurantStatus.ACTIVE);
        restaurant = restaurantRepository.save(restaurant);

        String password = "Password123!";
        User adminUser = new User();
        adminUser.setEmail("admin-" + suffix + "@example.com");
        adminUser.setPasswordHash(passwordEncoder.encode(password));
        adminUser.setFullName("Admin " + suffix);
        adminUser.setStatus(UserStatus.ACTIVE);
        adminUser = userRepository.save(adminUser);

        RestaurantUser restaurantUser = new RestaurantUser();
        restaurantUser.setRestaurant(restaurant);
        restaurantUser.setUser(adminUser);
        restaurantUser.setMembershipStatus(MembershipStatus.ACTIVE);
        restaurantUser = restaurantUserRepository.save(restaurantUser);

        Role adminRole = roleRepository.findByCode(RestaurantAccessGuard.ROLE_ADMIN).orElseGet(() -> {
            Role role = new Role();
            role.setCode(RestaurantAccessGuard.ROLE_ADMIN);
            role.setName("Restaurant Admin");
            return roleRepository.save(role);
        });

        RestaurantUserRole restaurantUserRole = new RestaurantUserRole();
        restaurantUserRole.setRestaurantUser(restaurantUser);
        restaurantUserRole.setRole(adminRole);
        restaurantUserRoleRepository.save(restaurantUserRole);

        String accessToken = login(adminUser.getEmail(), password);

        ResponseEntity<TableResponse> createTableResponse = restTemplate.exchange(
                url("/api/admin/restaurants/" + restaurant.getId() + "/tables"),
                HttpMethod.POST,
                authorizedEntity(accessToken, new CreateTableRequest("A1-" + suffix, "Patio Table", 4, null)),
                TableResponse.class
        );
        assertThat(createTableResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createTableResponse.getBody()).isNotNull();

        Long tableId = createTableResponse.getBody().id();

        ResponseEntity<QrCodeResponse> qrResponse = restTemplate.exchange(
                url("/api/admin/restaurants/" + restaurant.getId() + "/tables/" + tableId + "/qr-codes"),
                HttpMethod.POST,
                authorizedEntity(accessToken, null),
                QrCodeResponse.class
        );
        assertThat(qrResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(qrResponse.getBody()).isNotNull();

        String token = qrResponse.getBody().token();

        ResponseEntity<CategoryResponse> categoryResponse = restTemplate.exchange(
                url("/api/admin/restaurants/" + restaurant.getId() + "/menu/categories"),
                HttpMethod.POST,
                authorizedEntity(accessToken, new CreateCategoryRequest("Desserts " + suffix, 1, true)),
                CategoryResponse.class
        );
        assertThat(categoryResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(categoryResponse.getBody()).isNotNull();

        ResponseEntity<ProductResponse> productResponse = restTemplate.exchange(
                url("/api/admin/restaurants/" + restaurant.getId() + "/menu/products"),
                HttpMethod.POST,
                authorizedEntity(accessToken, new CreateProductRequest(
                        categoryResponse.getBody().id(),
                        "Baklava " + suffix,
                        "Fresh baklava",
                        new BigDecimal("180.00"),
                        true,
                        true
                )),
                ProductResponse.class
        );
        assertThat(productResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(productResponse.getBody()).isNotNull();

        ResponseEntity<PublicQrResolveResponse> resolveResponse = restTemplate.getForEntity(
                url("/api/public/qr/" + token),
                PublicQrResolveResponse.class
        );
        assertThat(resolveResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resolveResponse.getBody()).isNotNull();
        assertThat(resolveResponse.getBody().restaurantId()).isEqualTo(restaurant.getId());
        assertThat(resolveResponse.getBody().tableId()).isEqualTo(tableId);

        ResponseEntity<PublicMenuResponse> publicMenuResponse = restTemplate.getForEntity(
                url("/api/public/qr/" + token + "/menu"),
                PublicMenuResponse.class
        );
        assertThat(publicMenuResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(publicMenuResponse.getBody()).isNotNull();
        assertThat(publicMenuResponse.getBody().restaurantId()).isEqualTo(restaurant.getId());
        assertThat(publicMenuResponse.getBody().tableId()).isEqualTo(tableId);
        assertThat(publicMenuResponse.getBody().categories()).hasSize(1);
        assertThat(publicMenuResponse.getBody().categories().get(0).products()).hasSize(1);
        assertThat(publicMenuResponse.getBody().categories().get(0).products().get(0).name())
                .isEqualTo("Baklava " + suffix);
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private String login(String email, String password) {
        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                url("/api/public/auth/login"),
                new LoginRequest(email, password),
                LoginResponse.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody().accessToken();
    }

    private <T> HttpEntity<T> authorizedEntity(String accessToken, T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
