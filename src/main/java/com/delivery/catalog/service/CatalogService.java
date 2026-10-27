package com.delivery.catalog.service;

import com.delivery.catalog.model.Dish;
import com.delivery.catalog.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final DishRepository dishRepository;

    public List<Dish> getDishesByTenant(UUID tenantId, Boolean onlyAvailable) {
        return getDishesByTenant(tenantId, onlyAvailable, null);
    }

    /**
     * Filtra por tags alimentares quando o app do cliente envia as preferencias do perfil.
     * A correspondencia e "qualquer tag" para nao esconder um prato seguro por causa de outra tag.
     */
    public List<Dish> getDishesByTenant(UUID tenantId, Boolean onlyAvailable, String tags) {
        List<Dish> dishes = Boolean.TRUE.equals(onlyAvailable)
                ? dishRepository.findByTenantIdAndAvailable(tenantId, true)
                : dishRepository.findByTenantId(tenantId);
        if (tags == null || tags.isBlank()) {
            return dishes;
        }
        List<String> wanted = java.util.Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(value -> value.toUpperCase(java.util.Locale.ROOT))
                .toList();
        if (wanted.isEmpty()) {
            return dishes;
        }
        return dishes.stream()
                .filter(dish -> matchesAnyTag(dish.getTagsJson(), wanted))
                .toList();
    }

    private boolean matchesAnyTag(String tagsJson, List<String> wanted) {
        if (tagsJson == null || tagsJson.isBlank()) {
            return false;
        }
        String normalized = tagsJson.toUpperCase(java.util.Locale.ROOT);
        return wanted.stream().anyMatch(normalized::contains);
    }

    public List<String> getCategoriesByTenant(UUID tenantId) {
        return dishRepository.findCategoriesByTenantId(tenantId);
    }

    public Dish getDishById(UUID id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prato não encontrado com ID: " + id));
    }

    public Dish createDish(Dish dish) {
        return dishRepository.save(dish);
    }

    public Dish updateAvailability(UUID id, boolean available) {
        Dish dish = getDishById(id);
        dish.setAvailable(available);
        return dishRepository.save(dish);
    }

    public Dish updateDish(UUID id, Dish updatedDish) {
        Dish dish = getDishById(id);
        if (updatedDish.getName() != null) {
            dish.setName(updatedDish.getName());
        }
        if (updatedDish.getDescription() != null) {
            dish.setDescription(updatedDish.getDescription());
        }
        if (updatedDish.getPrice() != null) {
            dish.setPrice(updatedDish.getPrice());
        }
        if (updatedDish.getCategory() != null) {
            dish.setCategory(updatedDish.getCategory());
        }
        if (updatedDish.getImageUrl() != null) {
            dish.setImageUrl(updatedDish.getImageUrl());
        }
        if (updatedDish.getAvailable() != null) {
            dish.setAvailable(updatedDish.getAvailable());
        }
        if (updatedDish.getOptionsJson() != null) {
            dish.setOptionsJson(updatedDish.getOptionsJson());
        }
        if (updatedDish.getTagsJson() != null) {
            dish.setTagsJson(updatedDish.getTagsJson());
        }
        if (updatedDish.getSalesDaysJson() != null) {
            dish.setSalesDaysJson(updatedDish.getSalesDaysJson());
        }
        if (updatedDish.getSalesWindowsJson() != null) {
            dish.setSalesWindowsJson(updatedDish.getSalesWindowsJson());
        }
        if (updatedDish.getPreparationTimeMin() != null) {
            dish.setPreparationTimeMin(updatedDish.getPreparationTimeMin());
        }
        if (updatedDish.getEffortMinutes() != null) {
            dish.setEffortMinutes(Math.max(0, Math.min(updatedDish.getEffortMinutes(), 24 * 60)));
        }
        if (updatedDish.getTenantId() != null) {
            dish.setTenantId(updatedDish.getTenantId());
        }
        return dishRepository.save(dish);
    }
}
