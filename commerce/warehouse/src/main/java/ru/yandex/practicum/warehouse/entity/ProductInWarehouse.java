package ru.yandex.practicum.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.interaction_api.model.dto.warehouse.DimensionDto;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "warehouse_products")
public class ProductInWarehouse {

    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(nullable = false)
    private Boolean fragile;

    @Embedded
    @Column(nullable = false)
    private DimensionDto dimension;

    @Column(nullable = false)
    private Double weight;

    @Builder.Default
    private Integer quantity = 0;
}
