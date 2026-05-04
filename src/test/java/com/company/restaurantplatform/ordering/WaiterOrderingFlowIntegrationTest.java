package com.company.restaurantplatform.ordering;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.restaurantplatform.core.domain.entity.MenuCategory;
import com.company.restaurantplatform.core.domain.entity.Product;
import com.company.restaurantplatform.core.domain.entity.Restaurant;
import com.company.restaurantplatform.core.domain.entity.RestaurantTable;
import com.company.restaurantplatform.core.domain.entity.RestaurantUser;
import com.company.restaurantplatform.core.domain.entity.RestaurantUserRole;
import com.company.restaurantplatform.core.domain.entity.Role;
import com.company.restaurantplatform.core.domain.entity.User;
import com.company.restaurantplatform.core.domain.enums.MembershipStatus;
import com.company.restaurantplatform.core.domain.enums.OrderStatus;
import com.company.restaurantplatform.core.domain.enums.RestaurantStatus;
import com.company.restaurantplatform.core.domain.enums.TableStatus;
import com.company.restaurantplatform.core.domain.enums.UserStatus;
import com.company.restaurantplatform.core.repository.MenuCategoryRepository;
import com.company.restaurantplatform.core.repository.ProductRepository;
import com.company.restaurantplatform.core.repository.RestaurantRepository;
import com.company.restaurantplatform.core.repository.RestaurantTableRepository;
import com.company.restaurantplatform.core.repository.RestaurantUserRepository;
import com.company.restaurantplatform.core.repository.RestaurantUserRoleRepository;
import com.company.restaurantplatform.core.repository.RoleRepository;
import com.company.restaurantplatform.core.repository.UserRepository;
import com.company.restaurantplatform.identity.api.dto.LoginRequest;
import com.company.restaurantplatform.identity.api.dto.LoginResponse;
import com.company.restaurantplatform.ordering.api.dto.AddOrderItemRequest;
import com.company.restaurantplatform.ordering.api.dto.CreateOrderRequest;
import com.company.restaurantplatform.ordering.api.dto.OrderResponse;
import com.company.restaurantplatform.ordering.api.dto.UpdateOrderItemQuantityRequest;
import com.company.restaurantplatform.ordering.api.dto.UpdateOrderStatusRequest;
import com.company.restaurantplatform.shared.security.RestaurantAccessGuard;
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
class WaiterOrderingFlowIntegrationTest {

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
    private RestaurantTableRepository restaurantTableRepository;

    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RestaurantUserRoleRepository restaurantUserRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldCreateAndProgressWaiterOrderFlow() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        Restaurant restaurant = new Restaurant();
        restaurant.setName("Ordering Restaurant " + suffix);
        restaurant.setSlug("ordering-restaurant-" + suffix);
        restaurant.setStatus(RestaurantStatus.ACTIVE);
        restaurant = restaurantRepository.save(restaurant);

        Role role = roleRepository.findByCode(RestaurantAccessGuard.ROLE_WAITER).orElseGet(() -> {
            Role waiterRole = new Role();
            waiterRole.setCode(RestaurantAccessGuard.ROLE_WAITER);
            waiterRole.setName("Waiter");
            return roleRepository.save(waiterRole);
        });

        String password = "Password123!";
        User user = new User();
        user.setEmail("waiter-" + suffix + "@example.com");
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setFullName("Waiter " + suffix);
        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);

        RestaurantUser restaurantUser = new RestaurantUser();
        restaurantUser.setRestaurant(restaurant);
        restaurantUser.setUser(user);
        restaurantUser.setMembershipStatus(MembershipStatus.ACTIVE);
        restaurantUser = restaurantUserRepository.save(restaurantUser);

        RestaurantUserRole restaurantUserRole = new RestaurantUserRole();
        restaurantUserRole.setRestaurantUser(restaurantUser);
        restaurantUserRole.setRole(role);
        restaurantUserRoleRepository.save(restaurantUserRole);

        RestaurantTable table = new RestaurantTable();
        table.setRestaurant(restaurant);
        table.setTableNumber("W-" + suffix);
        table.setName("Waiter Table");
        table.setCapacity(4);
        table.setStatus(TableStatus.ACTIVE);
        table = restaurantTableRepository.save(table);

        MenuCategory category = new MenuCategory();
        category.setRestaurant(restaurant);
        category.setName("Meals " + suffix);
        category.setDisplayOrder(1);
        category.setActive(true);
        category = menuCategoryRepository.save(category);

        Product product = new Product();
        product.setRestaurant(restaurant);
        product.setCategory(category);
        product.setName("Burger " + suffix);
        product.setDescription("Burger");
        product.setPrice(new BigDecimal("320.00"));
        product.setActive(true);
        product.setVisibleToCustomer(true);
        product = productRepository.save(product);

        String accessToken = login(user.getEmail(), password);

        ResponseEntity<OrderResponse> createOrderResponse = restTemplate.postForEntity(
                url("/api/waiter/restaurants/" + restaurant.getId() + "/orders?waiterRestaurantUserId=" + restaurantUser.getId()),
                authorizedEntity(accessToken, new CreateOrderRequest(table.getId())),
                OrderResponse.class
        );
        assertThat(createOrderResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createOrderResponse.getBody()).isNotNull();
        assertThat(createOrderResponse.getBody().status()).isEqualTo(OrderStatus.DRAFT);

        Long orderId = createOrderResponse.getBody().id();

        ResponseEntity<OrderResponse> addItemResponse = restTemplate.postForEntity(
                url("/api/waiter/restaurants/" + restaurant.getId() + "/orders/" + orderId + "/items?waiterRestaurantUserId=" + restaurantUser.getId()),
                authorizedEntity(accessToken, new AddOrderItemRequest(product.getId(), 2, "extra cheese")),
                OrderResponse.class
        );
        assertThat(addItemResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(addItemResponse.getBody()).isNotNull();
        assertThat(addItemResponse.getBody().items()).hasSize(1);
        assertThat(addItemResponse.getBody().totalAmount()).isEqualByComparingTo("640.00");

        Long orderItemId = addItemResponse.getBody().items().get(0).id();

        restTemplate.put(
                url("/api/waiter/restaurants/" + restaurant.getId() + "/orders/" + orderId + "/items/" + orderItemId + "?waiterRestaurantUserId=" + restaurantUser.getId()),
                authorizedEntity(accessToken, new UpdateOrderItemQuantityRequest(3))
        );

        ResponseEntity<OrderResponse> getAfterUpdateResponse = restTemplate.exchange(
                url("/api/waiter/restaurants/" + restaurant.getId() + "/orders/" + orderId),
                HttpMethod.GET,
                authorizedEntity(accessToken, null),
                OrderResponse.class
        );
        assertThat(getAfterUpdateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getAfterUpdateResponse.getBody()).isNotNull();
        assertThat(getAfterUpdateResponse.getBody().items().get(0).quantity()).isEqualTo(3);
        assertThat(getAfterUpdateResponse.getBody().totalAmount()).isEqualByComparingTo("960.00");

        ResponseEntity<OrderResponse> submitResponse = restTemplate.postForEntity(
                url("/api/waiter/restaurants/" + restaurant.getId() + "/orders/" + orderId + "/submit?waiterRestaurantUserId=" + restaurantUser.getId()),
                authorizedEntity(accessToken, null),
                OrderResponse.class
        );
        assertThat(submitResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(submitResponse.getBody()).isNotNull();
        assertThat(submitResponse.getBody().status()).isEqualTo(OrderStatus.RECEIVED);

        restTemplate.exchange(
                url("/api/waiter/restaurants/" + restaurant.getId() + "/orders/" + orderId + "/status"),
                HttpMethod.PATCH,
                authorizedEntity(accessToken, new UpdateOrderStatusRequest(OrderStatus.PREPARING)),
                OrderResponse.class
        );
        restTemplate.exchange(
                url("/api/waiter/restaurants/" + restaurant.getId() + "/orders/" + orderId + "/status"),
                HttpMethod.PATCH,
                authorizedEntity(accessToken, new UpdateOrderStatusRequest(OrderStatus.READY)),
                OrderResponse.class
        );
        restTemplate.exchange(
                url("/api/waiter/restaurants/" + restaurant.getId() + "/orders/" + orderId + "/status"),
                HttpMethod.PATCH,
                authorizedEntity(accessToken, new UpdateOrderStatusRequest(OrderStatus.DELIVERED_TO_TABLE)),
                OrderResponse.class
        );

        ResponseEntity<OrderResponse> finalOrderResponse = restTemplate.exchange(
                url("/api/waiter/restaurants/" + restaurant.getId() + "/orders/" + orderId),
                HttpMethod.GET,
                authorizedEntity(accessToken, null),
                OrderResponse.class
        );
        assertThat(finalOrderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(finalOrderResponse.getBody()).isNotNull();
        assertThat(finalOrderResponse.getBody().status()).isEqualTo(OrderStatus.DELIVERED_TO_TABLE);
        assertThat(finalOrderResponse.getBody().totalAmount()).isEqualByComparingTo("960.00");
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
