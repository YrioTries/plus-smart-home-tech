package ru.yandex.practicum.interaction_api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pageable {
    private Integer page;
    private Integer size;
    private List<String> sort;
}
