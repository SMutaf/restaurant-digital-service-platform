package com.company.restaurantplatform.menu.application;

import com.company.restaurantplatform.core.domain.entity.MenuCategory;
import com.company.restaurantplatform.core.domain.entity.Product;
import com.company.restaurantplatform.core.domain.entity.Restaurant;
import com.company.restaurantplatform.core.repository.MenuCategoryRepository;
import com.company.restaurantplatform.core.repository.ProductRepository;
import com.company.restaurantplatform.core.repository.RestaurantRepository;
import com.company.restaurantplatform.shared.exception.NotFoundException;
import com.company.restaurantplatform.menu.api.dto.CreateCategoryRequest;
import com.company.restaurantplatform.menu.api.dto.CreateProductRequest;
import com.company.restaurantplatform.menu.api.dto.UpdateCategoryRequest;
import com.company.restaurantplatform.menu.api.dto.UpdateProductRequest;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuAdminService {

    private final RestaurantRepository restaurantRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final ProductRepository productRepository;

    @Transactional
    public MenuCategory createCategory(Long restaurantId, CreateCategoryRequest request) {
        Restaurant restaurant = findRestaurant(restaurantId);

        MenuCategory category = new MenuCategory();
        category.setRestaurant(restaurant);
        category.setName(request.name());
        category.setDisplayOrder(request.displayOrder());
        category.setActive(request.active());
        return menuCategoryRepository.save(category);
    }

    public List<MenuCategory> listCategories(Long restaurantId) {
        findRestaurant(restaurantId);
        return menuCategoryRepository.findAllByRestaurantIdOrderByDisplayOrderAsc(restaurantId);
    }

    @Transactional
    public MenuCategory updateCategory(Long restaurantId, Long categoryId, UpdateCategoryRequest request) {
        MenuCategory category = menuCategoryRepository.findByIdAndRestaurantId(categoryId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        category.setName(request.name());
        category.setDisplayOrder(request.displayOrder());
        category.setActive(request.active());
        return menuCategoryRepository.save(category);
    }

    @Transactional
    public Product createProduct(Long restaurantId, CreateProductRequest request) {
        Restaurant restaurant = findRestaurant(restaurantId);
        MenuCategory category = menuCategoryRepository.findByIdAndRestaurantId(request.categoryId(), restaurantId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Product product = new Product();
        product.setRestaurant(restaurant);
        product.setCategory(category);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setActive(request.active());
        product.setVisibleToCustomer(request.visibleToCustomer());
        return productRepository.save(product);
    }

    public List<Product> listProducts(Long restaurantId) {
        findRestaurant(restaurantId);
        return productRepository.findAllByRestaurantIdOrderByNameAsc(restaurantId);
    }

    @Transactional
    public Product updateProduct(Long restaurantId, Long productId, UpdateProductRequest request) {
        Product product = productRepository.findByIdAndRestaurantId(productId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        MenuCategory category = menuCategoryRepository.findByIdAndRestaurantId(request.categoryId(), restaurantId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        product.setCategory(category);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setActive(request.active());
        product.setVisibleToCustomer(request.visibleToCustomer());
        return productRepository.save(product);
    }

    private Restaurant findRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));
    }
}
