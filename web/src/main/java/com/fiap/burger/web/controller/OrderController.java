package com.fiap.burger.web.controller;

import com.fiap.burger.domain.entities.order.OrderStatus;
import com.fiap.burger.domain.entities.product.Category;
import com.fiap.burger.domain.misc.exception.OrderNotFoundException;
import com.fiap.burger.domain.service.OrderService;
import com.fiap.burger.web.dto.order.request.OrderInsertRequestDto;
import com.fiap.burger.web.dto.order.request.OrderUpdateStatusRequestDto;
import com.fiap.burger.web.dto.order.response.ListOrderResponseDto;
import com.fiap.burger.web.dto.order.response.OrderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "pedido", description = "API responsável pelo controle de pedidos.")
public class OrderController {

    @Autowired
    OrderService service;

    @Operation(summary = "Criar pedido", description = "Criação de um novo pedido", tags = {"pedido"})
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "Pedido inválido")})
    @PostMapping("/orders")
    public ListOrderResponseDto insert(@RequestBody OrderInsertRequestDto orderDto) {
        // TODO retornar items do pedido inserido
        return ListOrderResponseDto.toResponseDto(service.insert(orderDto.toEntity()));
    }

    @Operation(summary = "Consultar pedido", description = "Consultar pedidos", tags = {"pedido"})
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "Request inválida")})
    @GetMapping("/orders/{orderId}")
    public OrderResponseDto findById(@PathVariable Long orderId) {
        var persistedOrder = service.findById(orderId);
        if (persistedOrder == null) throw new OrderNotFoundException(orderId);
        return OrderResponseDto.toResponseDto(persistedOrder);
    }

    @Operation(summary = "Atualizar status do pedido", description = "Atualizar status de um pedido", tags = {"pedido"})
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "Requisição inválida")})
    @PostMapping("/orders/{orderId}/status")
    public ListOrderResponseDto insert(@PathVariable Long orderId, @RequestBody OrderUpdateStatusRequestDto orderDto) {
        return ListOrderResponseDto.toResponseDto(service.updateStatus(orderId, orderDto.newStatus()));
    }

    @Operation(summary = "Listar pedidos", description = "Listar pedidos", tags = {"pedido"})
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "Request inválida")})
    @GetMapping("/orders")
    public List<ListOrderResponseDto> findAll(@RequestParam @Nullable OrderStatus status) {
        return service.findAllBy(status).stream().map(ListOrderResponseDto::toResponseDto).collect(Collectors.toList());
    }

    @Operation(summary = "Listar pedidos em progresso", description = "Listar pedidos em progresso. Status igual a EM_PREPARAÇÂO ou PRONTO.", tags = {"pedido"})
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = "Request inválida")})
    @GetMapping("/orders/in-progress")
    public List<ListOrderResponseDto> findAllInProgress() {
        return service.findAllInProgress().stream().map(ListOrderResponseDto::toResponseDto).collect(Collectors.toList());
    }
}
