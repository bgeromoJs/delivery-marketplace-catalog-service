package com.delivery.catalog.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dishes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String category;

    @Column(name = "image_url")
    private String imageUrl;

    @Builder.Default
    private Boolean available = true;

    @Column(name = "options_json", columnDefinition = "TEXT")
    private String optionsJson;

    /** Tags alimentares: VEGETARIAN, VEGAN, GLUTEN_FREE, LACTOSE_FREE. */
    @Column(name = "tags_json", columnDefinition = "TEXT")
    private String tagsJson;

    @Column(name = "sales_days_json", columnDefinition = "TEXT")
    private String salesDaysJson;

    @Column(name = "sales_windows_json", columnDefinition = "TEXT")
    private String salesWindowsJson;

    @Builder.Default
    private Integer preparationTimeMin = 25;

    /**
     * Esforço de cozinha por unidade, em minutos.
     * É o valor usado para reservar a agenda de produção quando o cliente faz o pedido.
     */
    @Builder.Default
    @Column(name = "effort_minutes", nullable = false)
    private Integer effortMinutes = 20;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
