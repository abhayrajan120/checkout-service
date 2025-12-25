package com.example.oss.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.oss.entity.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
  Optional<Product> findBySku(String sku);
}
