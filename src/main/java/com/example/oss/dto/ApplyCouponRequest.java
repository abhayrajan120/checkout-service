package com.example.oss.dto;


import jakarta.validation.constraints.NotBlank;

public record ApplyCouponRequest(@NotBlank String code) {}
