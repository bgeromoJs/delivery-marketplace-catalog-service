package com.delivery.catalog.controller;

import com.delivery.catalog.dto.DishRequest;
import com.delivery.catalog.model.Dish;
import com.delivery.catalog.repository.DishRepository;
import com.delivery.catalog.service.TenantLookup;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints do cardápio usados pelo app do vendedor (prefixo /api/store/me/dishes).
 */
@Tag(name = "Cardápio (app vendedor)", description = "CRUD de pratos do app: foto, horários de venda e opcionais")
@RestController
@RequestMapping("/api/store/me/dishes")
@RequiredArgsConstructor
public class StoreCatalogController {

    private final DishRepository dishRepository;
    private final TenantLookup tenantLookup;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> list() {
        List<Map<String, Object>> resp = new ArrayList<>();
        for (Dish dish : dishRepository.findAll()) {
            resp.add(toDto(dish));
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody DishRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome do prato é obrigatório");
        }

        UUID tenantId = tenantLookup.resolve();
        if (tenantId == null) {
            tenantId = dishRepository.findAll().stream()
                    .map(Dish::getTenantId)
                    .filter(java.util.Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }
        if (tenantId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Não foi possível identificar a loja. Verifique se o tenant-service está no ar.");
        }

        Dish dish = Dish.builder()
                .tenantId(tenantId)
                .name(request.getName().trim())
                .category(request.getCategory() == null || request.getCategory().isBlank()
                        ? "Geral" : request.getCategory().trim())
                .price(request.getPrice() == null ? BigDecimal.ZERO : request.getPrice())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .available(request.getActive() == null || request.getActive())
                .optionsJson(request.getOptionsJson())
                .tagsJson(request.getTagsJson())
                .salesDaysJson(request.getSalesDaysJson())
                .salesWindowsJson(request.getSalesWindowsJson())
                .effortMinutes(normalizeEffort(request.getEffortMinutes()))
                .build();

        return ResponseEntity.ok(toDto(dishRepository.save(dish)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable UUID id, @RequestBody DishRequest request) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prato não encontrado"));

        if (request.getName() != null && !request.getName().isBlank()) {
            dish.setName(request.getName().trim());
        }
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            dish.setCategory(request.getCategory().trim());
        }
        if (request.getPrice() != null) {
            dish.setPrice(request.getPrice());
        }
        if (request.getActive() != null) {
            dish.setAvailable(request.getActive());
        }
        if (request.getDescription() != null) {
            dish.setDescription(request.getDescription());
        }
        if (request.getImageUrl() != null) {
            dish.setImageUrl(request.getImageUrl());
        }
        if (request.getOptionsJson() != null) {
            dish.setOptionsJson(request.getOptionsJson());
        }
        if (request.getTagsJson() != null) {
            dish.setTagsJson(request.getTagsJson());
        }
        if (request.getSalesDaysJson() != null) {
            dish.setSalesDaysJson(request.getSalesDaysJson());
        }
        if (request.getSalesWindowsJson() != null) {
            dish.setSalesWindowsJson(request.getSalesWindowsJson());
        }
        if (request.getEffortMinutes() != null) {
            dish.setEffortMinutes(normalizeEffort(request.getEffortMinutes()));
        }

        return ResponseEntity.ok(toDto(dishRepository.save(dish)));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<Map<String, Object>> toggle(@PathVariable UUID id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prato não encontrado"));
        dish.setAvailable(!Boolean.TRUE.equals(dish.getAvailable()));
        return ResponseEntity.ok(toDto(dishRepository.save(dish)));
    }

    private Map<String, Object> toDto(Dish dish) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", dish.getId().toString());
        dto.put("name", dish.getName());
        dto.put("category", dish.getCategory());
        dto.put("price", dish.getPrice());
        dto.put("active", Boolean.TRUE.equals(dish.getAvailable()));
        dto.put("unavailableNote", null);
        dto.put("days", readIntArray(dish.getSalesDaysJson(), List.of(1, 2, 3, 4, 5, 6)));
        dto.put("windows", readStringArray(dish.getSalesWindowsJson(), List.of("11:00-15:00", "18:00-23:00")));
        dto.put("imageUrl", dish.getImageUrl());
        dto.put("description", dish.getDescription());
        dto.put("optionsJson", dish.getOptionsJson());
        dto.put("tagsJson", dish.getTagsJson());
        dto.put("salesDaysJson", dish.getSalesDaysJson());
        dto.put("salesWindowsJson", dish.getSalesWindowsJson());
        dto.put("effortMinutes", dish.getEffortMinutes() == null ? 20 : dish.getEffortMinutes());
        return dto;
    }

    private int normalizeEffort(Integer effortMinutes) {
        if (effortMinutes == null || effortMinutes < 0) {
            return 20;
        }
        return Math.min(effortMinutes, 24 * 60);
    }

    private List<Integer> readIntArray(String json, List<Integer> fallback) {
        if (json == null || json.isBlank()) {
            return fallback;
        }
        String cleaned = json.replace("[", "").replace("]", "").replace("\"", "").trim();
        if (cleaned.isEmpty()) {
            return fallback;
        }
        List<Integer> values = new ArrayList<>();
        for (String part : cleaned.split(",")) {
            try {
                values.add(Integer.parseInt(part.trim()));
            } catch (NumberFormatException ignored) {
                // ignora valores inválidos
            }
        }
        return values.isEmpty() ? fallback : values;
    }

    private List<String> readStringArray(String json, List<String> fallback) {
        if (json == null || json.isBlank()) {
            return fallback;
        }
        String cleaned = json.replace("[", "").replace("]", "").trim();
        if (cleaned.isEmpty()) {
            return fallback;
        }
        List<String> values = new ArrayList<>();
        for (String part : cleaned.split(",")) {
            String value = part.trim();
            if (value.startsWith("\"")) {
                value = value.substring(1);
            }
            if (value.endsWith("\"")) {
                value = value.substring(0, value.length() - 1);
            }
            if (!value.isBlank()) {
                values.add(value);
            }
        }
        return values.isEmpty() ? fallback : values;
    }
}
