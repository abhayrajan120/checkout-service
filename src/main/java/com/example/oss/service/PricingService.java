package com.example.oss.service;



import org.springframework.stereotype.Service;

import com.example.oss.entity.Product;

@Service
public class PricingService {
/** Placeholder for discount rules; currently returns 0 discount. */
public long computeLineDiscount(Product p, int qty) {
 // Example rule: buy >=3 get $1 off each
 // if (qty >= 3) return 100L * qty;
 return 0L;
}
}
