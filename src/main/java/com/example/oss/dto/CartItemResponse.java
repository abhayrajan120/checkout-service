package com.example.oss.dto;



public record CartItemResponse(
Long id,
Long productId,
String sku,
String name,
Integer qty,
Long unitPriceCents,
Long discountCents,
Long totalCents
) {}

