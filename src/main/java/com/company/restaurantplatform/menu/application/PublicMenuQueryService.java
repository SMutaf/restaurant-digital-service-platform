package com.company.restaurantplatform.menu.application;

import com.company.restaurantplatform.core.domain.entity.MenuCategory;
import com.company.restaurantplatform.core.domain.entity.Product;
import com.company.restaurantplatform.core.domain.entity.TableQrCode;
import com.company.restaurantplatform.core.repository.MenuCategoryRepository;
import com.company.restaurantplatform.core.repository.ProductRepository;
import com.company.restaurantplatform.qr.application.PublicQrResolveService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublicMenuQueryService {

    private final PublicQrResolveService publicQrResolveService;
    private final MenuCategoryRepository menuCategoryRepository;
    private final ProductRepository productRepository;

    @Transactional
    public PublicMenuView getMenuByQrToken(String token) {
        TableQrCode qrCode = publicQrResolveService.resolve(token);
        Long restaurantId = qrCode.getRestaurantTable().getRestaurant().getId();

        List<MenuCategory> categories = menuCategoryRepository.findAllByRestaurantIdOrderByDisplayOrderAsc(restaurantId)
                .stream()
                .filter(MenuCategory::isActive)
                .toList();

        List<Product> products = productRepository.findAllByRestaurantIdAndActiveTrueAndVisibleToCustomerTrueOrderByNameAsc(restaurantId);

        return new PublicMenuView(qrCode, categories, products);
    }

    public record PublicMenuView(
            TableQrCode qrCode,
            List<MenuCategory> categories,
            List<Product> products
    ) {
    }
}
