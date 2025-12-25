package com.example.oss.dto;


import java.util.*;

public record CartResponse(
Long id,
String status,
String currency,
List<CartItemResponse> items,
Long subtotalCents,
Long couponTotalCents,
Long grandTotalCents
) {}
