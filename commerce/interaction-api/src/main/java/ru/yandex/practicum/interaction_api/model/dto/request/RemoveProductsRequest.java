package ru.yandex.practicum.interaction_api.model.dto.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RemoveProductsRequest {
    private List<UUID> productIds;
}
