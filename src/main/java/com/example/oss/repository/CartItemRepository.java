package com.example.oss.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.oss.entity.CartItem;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}
