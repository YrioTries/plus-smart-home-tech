package ru.yandex.practicum.warehouse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "warehouse_products", schema = "public")
public class WarehouseProductEntity {
    @Id
    @UuidGenerator
    @Column(name = "product_id", length = 36, nullable = false)
    private UUID productId;

    @Column(name = "fragile", nullable = false)
    private boolean fragile;

    @Column(name = "width", nullable = false)
    private Double width;

    @Column(name = "height", nullable = false)
    private Double height;

    @Column(name = "depth", nullable = false)
    private Double depth;

    @Column(name = "weight", nullable = false)
    private Double weight;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
