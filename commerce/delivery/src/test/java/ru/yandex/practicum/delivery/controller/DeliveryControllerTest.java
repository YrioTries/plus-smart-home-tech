package ru.yandex.practicum.delivery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.delivery.service.DeliveryService;
import ru.yandex.practicum.interaction_api.model.delivery.dto.DeliveryDto;
import ru.yandex.practicum.interaction_api.model.order.dto.OrderDto;
import ru.yandex.practicum.interaction_api.model.warehouse.dto.AddressDto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeliveryController.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false"
})
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createDelivery_shouldReturnDto() throws Exception {
        DeliveryDto request = DeliveryDto.builder()
                .fromAddress(AddressDto.builder()
                        .country("RU").city("Samara").street("Lenina").house("1").flat("1").build())
                .toAddress(AddressDto.builder()
                        .country("RU").city("Moscow").street("Tverskaya").house("2").flat("10").build())
                .orderId(UUID.randomUUID())
                .build();

        when(service.createDelivery(any(DeliveryDto.class))).thenReturn(request);

        mockMvc.perform(put("/api/v1/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").exists());

        verify(service).createDelivery(any(DeliveryDto.class));
    }

    @Test
    void successfulDelivery_shouldReturnNoContent() throws Exception {
        UUID deliveryId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/delivery/successful")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryId)))
                .andExpect(status().isNoContent());

        verify(service).successfulDelivery(deliveryId);
    }

    @Test
    void pickedDelivery_shouldReturnNoContent() throws Exception {
        UUID deliveryId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/delivery/picked")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryId)))
                .andExpect(status().isNoContent());

        verify(service).pickedDelivery(deliveryId);
    }

    @Test
    void failedDelivery_shouldReturnNoContent() throws Exception {
        UUID deliveryId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/delivery/failed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deliveryId)))
                .andExpect(status().isNoContent());

        verify(service).failedDelivery(deliveryId);
    }

    @Test
    void calculateDeliveryCost_shouldReturnPrice() throws Exception {
        OrderDto order = OrderDto.builder()
                .orderId(UUID.randomUUID())
                .deliveryId(UUID.randomUUID())
                .fragile(true)
                .deliveryWeight(10.0)
                .deliveryVolume(2.0)
                .products(Map.of(
                        UUID.randomUUID(), 2
                ))
                .build();

        when(service.calculateDeliveryCost(any(OrderDto.class)))
                .thenReturn(BigDecimal.valueOf(15.5));

        mockMvc.perform(post("/api/v1/delivery/cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(content().string("15.5"));

        verify(service).calculateDeliveryCost(any(OrderDto.class));
    }
}
