package com.example.oss.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="products")
@Getter @Setter
public class Product {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
private Long id;

@Column(nullable=false, unique=true) private String sku;
@Column(nullable=false) private String name;
@Column(name="price_cents", nullable=false) private Long priceCents;
@Column(nullable=false, length=3) private String currency = "USD";
@Column(nullable=false) private Boolean active = true;
@Column(name="stock_qty", nullable=false) private Integer stockQty = 0;
}

