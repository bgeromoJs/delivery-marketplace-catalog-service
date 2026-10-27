package com.delivery.catalog.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DishRequest {
    private String id;
    private String name;
    private String category;
    private BigDecimal price;
    private Boolean active;
    private String description;
    private String imageUrl;
    private String optionsJson;
    private String tagsJson;
    private String salesDaysJson;
    private String salesWindowsJson;
    /** Esforço de cozinha por unidade, em minutos. */
    private Integer effortMinutes;
}
