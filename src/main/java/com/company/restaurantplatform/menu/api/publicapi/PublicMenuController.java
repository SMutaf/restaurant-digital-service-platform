package com.company.restaurantplatform.menu.api.publicapi;

import com.company.restaurantplatform.menu.api.dto.PublicMenuCategoryResponse;
import com.company.restaurantplatform.menu.api.dto.PublicMenuResponse;
import com.company.restaurantplatform.menu.application.PublicMenuQueryService;
import com.company.restaurantplatform.menu.mapper.MenuMapper;
import java.util.List;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/qr/{token}/menu")
public class PublicMenuController {

    private final PublicMenuQueryService publicMenuQueryService;

    @GetMapping
    @Transactional
    public PublicMenuResponse getMenu(@PathVariable String token) {
        PublicMenuQueryService.PublicMenuView view = publicMenuQueryService.getMenuByQrToken(token);

        List<PublicMenuCategoryResponse> categories = view.categories().stream()
                .map(category -> new PublicMenuCategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getDisplayOrder(),
                        view.products().stream()
                                .filter(product -> product.getCategory().getId().equals(category.getId()))
                                .map(MenuMapper::toProductResponse)
                                .toList()
                ))
                .toList();

        return new PublicMenuResponse(
                view.qrCode().getRestaurantTable().getRestaurant().getId(),
                view.qrCode().getRestaurantTable().getRestaurant().getName(),
                view.qrCode().getRestaurantTable().getId(),
                view.qrCode().getRestaurantTable().getTableNumber(),
                categories
        );
    }
}
