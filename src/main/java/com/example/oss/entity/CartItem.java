package com.example.oss.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="cart_items",
uniqueConstraints = @UniqueConstraint(name="uq_cart_product", columnNames={"cart_id","product_id"}))
@Getter @Setter
public class CartItem {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
private Long id;

@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="cart_id", nullable=false)
private Cart cart;

@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="product_id", nullable=false)
private Product product;

@Column(nullable=false) private Integer qty;
@Column(name="unit_price_cents", nullable=false) private Long unitPriceCents;
@Column(name="discount_cents", nullable=false) private Long discountCents = 0L;
@Column(name="total_cents", nullable=false) private Long totalCents;
}

