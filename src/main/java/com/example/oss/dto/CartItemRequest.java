package com.example.oss.dto;

import jakarta.validation.constraints.*;

public record CartItemRequest(
@NotNull Long productId,
@NotNull @Positive Integer qty
) {}
