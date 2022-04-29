package ru.pshiblo.payment.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.payment.mappings.OrderMapper;
import ru.pshiblo.payment.service.OrderService;
import ru.pshiblo.payment.web.dto.request.CreateOrderDto;
import ru.pshiblo.payment.web.dto.response.OrderResponseDto;
import ru.pshiblo.payment.web.dto.response.OrderStatusResponseDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("public/order")
public class OrderController {

    private final OrderService service;
    private final OrderMapper mapper;

    @PostMapping
    public OrderResponseDto create(@Valid @RequestBody CreateOrderDto request, @RequestHeader("Authorization") String apiKey) {
        return mapper.toDTO(
                service.create(
                        mapper.toEntity(request),
                        apiKey
                )
        );
    }

    @PutMapping("{id}/confirm")
    public OrderResponseDto confirm(@PathVariable long id, @RequestHeader("Authorization") String apiKey) {
        return mapper.toDTO(
                service.confirm(id, apiKey)
        );
    }

    @PutMapping("{id}/cancel")
    public OrderResponseDto cancel(@PathVariable long id, @RequestHeader("Authorization") String apiKey) {
        return mapper.toDTO(
                service.cancel(id, apiKey)
        );
    }

    @GetMapping("{id}/status")
    public OrderStatusResponseDto getStatus(@PathVariable long id, @RequestHeader("Authorization") String apiKey) {
        return mapper.toOrderStatus(
                service.findById(id, apiKey)
        );
    }

    @GetMapping("{id}")
    public OrderResponseDto getOrder(@PathVariable long id) {
        return mapper.toDTO(
                service.findById(id)
        );
    }

}
