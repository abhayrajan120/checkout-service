package com.example.oss.repository;


import org.springframework.data.jpa.repository.*;

import com.example.oss.entity.Cart;

import java.util.*;

public interface CartRepository extends JpaRepository<Cart, Long> {
  Optional<Cart> findByUserIdAndStatus(Long userId, Cart.Status status);
  Optional<Cart> findBySessionIdAndStatus(String sessionId, Cart.Status status);
}
