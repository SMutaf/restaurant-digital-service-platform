package com.company.restaurantplatform.table;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.restaurantplatform.core.domain.entity.Restaurant;
import com.company.restaurantplatform.core.domain.enums.RestaurantStatus;
import com.company.restaurantplatform.core.repository.RestaurantRepository;
import com.company.restaurantplatform.menu.api.dto.CategoryResponse;
import com.company.restaurantplatform.menu.api.dto.CreateCategoryRequest;
import com.company.restaurantplatform.menu.api.dto.CreateProductRequest;
import com.company.restaurantplatform.menu.api.dto.ProductResponse;
import com.company.restaurantplatform.menu.api.dto.PublicMenuResponse;
import com.company.restaurantplatform.qr.api.dto.PublicQrResolveResponse;
import com.company.restaurantplatform.qr.api.dto.QrCodeResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIfEnvironmentVariable(named = "DB_USERNAME", matches = ".+")
class TableQrMenuFlowIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    void shouldManageTablesGenerateQrAndServePublicMenu() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        Restaurant restaurant = new Restaurant();
        restaurant.setName("Phase 2 Restaurant " + suffix);
        restaurant.setSlug("phase-2-restaurant-" + suffix);
        restaurant.setStatus(RestaurantStatus.ACTIVE);
        restaurant = restaurantRepository.save(restaurant);

        ResponseEntity<TableResponse> createTableResponse = restTemplate.postForEntity(
                url("/api/admin/restaurants/" + restaurant.getId() + "/tables"),
                new CreateTableRequest("A1-" + suffix, "Patio Table", 4, null),
                TableResponse.class
        );
        assertThat(createTableResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createTableResponse.getBody()).isNotNull();

        Long tableId = createTableResponse.getBody().id();

        ResponseEntity<QrCodeResponse> qrResponse = restTemplate.postForEntity(
                url("/api/admin/restaurants/" + restaurant.getId() + "/tables/" + tableId + "/qr-codes"),
                null,
                QrCodeResponse.class
        );
        assertThat(qrResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(qrResponse.getBody()).isNotNull();

        String token = qrResponse.getBody().token();

        ResponseEntity<CategoryResponse> categoryResponse = restTemplate.postForEntity(
                url("/api/admin/restaurants/" + restaurant.getId() + "/menu/categories"),
                new CreateCategoryRequest("Desserts " + suffix, 1, true),
                CategoryResponse.class
        );
        assertThat(categoryResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(categoryResponse.getBody()).isNotNull();

        ResponseEntity<ProductResponse> productResponse = restTemplate.postForEntity(
                url("/api/admin/restaurants/" + restaurant.getId() + "/menu/products"),
                new CreateProductRequest(
                        categoryResponse.getBody().id(),
                        "Baklava " + suffix,
                        "Fresh baklava",
                        new BigDecimal("180.00"),
                        true,
                        true
                ),
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
}
