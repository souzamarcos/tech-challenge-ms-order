package com.fiap.burger.api.dto.order.response;

import com.fiap.burger.entity.customer.Customer;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public record OrderCustomerResponseDto(
    @NotNull
    String id,
    String name
) {
    public static OrderCustomerResponseDto toResponseDto(Customer customer) {
        return Optional.ofNullable(customer)
            .map(c -> new OrderCustomerResponseDto(c.getId(), c.getName()))
            .orElse(null);
    }
}
