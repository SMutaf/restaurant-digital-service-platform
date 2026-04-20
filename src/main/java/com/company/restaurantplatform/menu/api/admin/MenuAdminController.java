package com.company.restaurantplatform.menu.api.admin;

import com.company.restaurantplatform.menu.api.dto.CategoryResponse;
import com.company.restaurantplatform.menu.api.dto.CreateCategoryRequest;
import com.company.restaurantplatform.menu.api.dto.CreateProductRequest;
import com.company.restaurantplatform.menu.api.dto.ProductResponse;
import com.company.restaurantplatform.menu.api.dto.UpdateCategoryRequest;
import com.company.restaurantplatform.menu.api.dto.UpdateProductRequest;
import com.company.restaurantplatform.menu.application.MenuAdminService;
import com.company.restaurantplatform.menu.mapper.MenuMapper;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/restaurants/{restaurantId}/menu")
public class MenuAdminController {

    private final MenuAdminService menuAdminService;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(
            @PathVariable Long restaurantId,
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        return MenuMapper.toCategoryResponse(menuAdminService.createCategory(restaurantId, request));
    }

    @GetMapping("/categories")
    public List<CategoryResponse> listCategories(@PathVariable Long restaurantId) {
        return menuAdminService.listCategories(restaurantId)
                .stream()
                .map(MenuMapper::toCategoryResponse)
                .toList();
    }

    @PutMapping("/categories/{categoryId}")
    public CategoryResponse updateCategory(
            @PathVariable Long restaurantId,
            @PathVariable Long categoryId,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {
        return MenuMapper.toCategoryResponse(menuAdminService.updateCategory(restaurantId, categoryId, request));
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(
            @PathVariable Long restaurantId,
            @Valid @RequestBody CreateProductRequest request
    ) {
        return MenuMapper.toProductResponse(menuAdminService.createProduct(restaurantId, request));
    }

    @GetMapping("/products")
    public List<ProductResponse> listProducts(@PathVariable Long restaurantId) {
        return menuAdminService.listProducts(restaurantId)
                .stream()
                .map(MenuMapper::toProductResponse)
                .toList();
    }

    @PutMapping("/products/{productId}")
    public ProductResponse updateProduct(
            @PathVariable Long restaurantId,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        return MenuMapper.toProductResponse(menuAdminService.updateProduct(restaurantId, productId, request));
    }
}
