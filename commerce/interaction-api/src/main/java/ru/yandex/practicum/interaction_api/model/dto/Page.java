package ru.yandex.practicum.interaction_api.model.dto;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Page<T> {
    private List<T> content;
}
