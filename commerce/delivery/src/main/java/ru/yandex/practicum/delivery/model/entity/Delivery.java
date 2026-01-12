package ru.yandex.practicum.delivery.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.interaction_api.model.delivery.dto.DeliveryState;

import java.util.UUID;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Delivery {

    @Id
    @UuidGenerator
    @Column(name = "delivery_id")
    private UUID deliveryId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="country", column=@Column(name="from_country")),
            @AttributeOverride(name="city", column=@Column(name="from_city")),
            @AttributeOverride(name="street", column=@Column(name="from_street")),
            @AttributeOverride(name="house", column=@Column(name="from_house")),
            @AttributeOverride(name="flat", column=@Column(name="from_flat"))
    })
    @Column(name = "from_address", nullable = false)
    private DeliveryAddress fromAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="country", column=@Column(name="to_country")),
            @AttributeOverride(name="city", column=@Column(name="to_city")),
            @AttributeOverride(name="street", column=@Column(name="to_street")),
            @AttributeOverride(name="house", column=@Column(name="to_house")),
            @AttributeOverride(name="flat", column=@Column(name="to_flat"))
    })
    @Column(name = "to_address", nullable = false)
    private DeliveryAddress toAddress;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_state", nullable = false)
    @Builder.Default
    private DeliveryState deliveryState = DeliveryState.CREATED;
}
