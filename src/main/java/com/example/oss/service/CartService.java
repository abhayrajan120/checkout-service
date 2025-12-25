package com.example.oss.service;



import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.example.oss.dto.CartItemResponse;
import com.example.oss.dto.CartResponse;
import com.example.oss.entity.Cart;
import com.example.oss.entity.CartItem;
import com.example.oss.entity.Product;
import com.example.oss.entity.User;
import com.example.oss.repository.CartItemRepository;
import com.example.oss.repository.CartRepository;
import com.example.oss.repository.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartService {

private final CartRepository cartRepo;
private final CartItemRepository itemRepo;
private final ProductRepository productRepo;
private final PricingService pricingService;

public CartService(CartRepository cartRepo, CartItemRepository itemRepo,
                  ProductRepository productRepo, PricingService pricingService) {
 this.cartRepo = cartRepo; this.itemRepo = itemRepo; this.productRepo = productRepo;
 this.pricingService = pricingService;
}

/** Get or create active cart for a user or session. */
@Transactional
public Cart getOrCreateCart(Long userId, String sessionId) {
 Optional<Cart> found = (userId != null)
   ? cartRepo.findByUserIdAndStatus(userId, Cart.Status.ACTIVE)
   : cartRepo.findBySessionIdAndStatus(sessionId, Cart.Status.ACTIVE);

 if (found.isPresent()) return found.get();

 Cart cart = new Cart();
 cart.setStatus(Cart.Status.ACTIVE);
 cart.setCurrency("USD");
 if (userId != null) {
   User u = new User(); u.setId(userId);
   cart.setUser(u); // assumes user exists; in production fetch/validate
 } else {
   cart.setSessionId(sessionId);
 }
 return cartRepo.save(cart);
}

/** Add or update item quantity. */
@Transactional
public CartResponse addItem(Long cartId, Long productId, int qty) {
 Assert.isTrue(qty > 0, "Quantity must be > 0");
 Cart cart = cartRepo.findById(cartId).orElseThrow(() -> new NoSuchElementException("Cart not found"));
 Product p = productRepo.findById(productId).orElseThrow(() -> new NoSuchElementException("Product not found"));

 // Stock check (optional)
 if (p.getStockQty() != null && p.getStockQty() < qty) {
   throw new IllegalArgumentException("Insufficient stock");
 }

 CartItem item = itemRepo.findByCartIdAndProductId(cart.getId(), p.getId()).orElseGet(() -> {
   CartItem ci = new CartItem();
   ci.setCart(cart);
   ci.setProduct(p);
   ci.setQty(0);
   ci.setUnitPriceCents(p.getPriceCents());
   ci.setDiscountCents(0L);
   ci.setTotalCents(0L);
   return ci;
 });

 int newQty = item.getQty() + qty;
 item.setQty(newQty);
 long discount = pricingService.computeLineDiscount(p, newQty);
 item.setDiscountCents(discount);
 item.setUnitPriceCents(p.getPriceCents());
 item.setTotalCents((item.getUnitPriceCents() - item.getDiscountCents()) * newQty);

 itemRepo.save(item);
 return toCartResponse(cartRepo.findById(cart.getId()).orElseThrow());
}

/** Update quantity (absolute set). */
@Transactional
public CartResponse setQuantity(Long cartId, Long productId, int qty) {
 if (qty <= 0) {
   removeItem(cartId, productId);
   return toCartResponse(cartRepo.findById(cartId).orElseThrow());
 }
 Cart cart = cartRepo.findById(cartId).orElseThrow();
 Product p = productRepo.findById(productId).orElseThrow();

 CartItem item = itemRepo.findByCartIdAndProductId(cart.getId(), p.getId())
   .orElseThrow(() -> new NoSuchElementException("Item not in cart"));

 item.setQty(qty);
 long discount = pricingService.computeLineDiscount(p, qty);
 item.setDiscountCents(discount);
 item.setUnitPriceCents(p.getPriceCents());
 item.setTotalCents((item.getUnitPriceCents() - item.getDiscountCents()) * qty);
 itemRepo.save(item);

 return toCartResponse(cartRepo.findById(cart.getId()).orElseThrow());
}

/** Remove item. */
@Transactional
public void removeItem(Long cartId, Long productId) {
 Cart cart = cartRepo.findById(cartId).orElseThrow();
 CartItem item = itemRepo.findByCartIdAndProductId(cart.getId(), productId)
   .orElseThrow(() -> new NoSuchElementException("Item not in cart"));
 itemRepo.delete(item);
}

/** Merge anonymous cart into user's cart upon login. */
@Transactional
public CartResponse mergeCarts(String sessionId, Long userId) {
 Optional<Cart> anonOpt = cartRepo.findBySessionIdAndStatus(sessionId, Cart.Status.ACTIVE);
 Cart userCart = getOrCreateCart(userId, null);

 if (anonOpt.isPresent()) {
   Cart anon = anonOpt.get();
   for (CartItem ai : anon.getItems()) {
     addItem(userCart.getId(), ai.getProduct().getId(), ai.getQty());
   }
   // mark anonymous cart abandoned
   anon.setStatus(Cart.Status.ABANDONED);
   cartRepo.save(anon);
 }
 return toCartResponse(userCart);
}

/** Compute cart totals and map to DTO. */
public CartResponse toCartResponse(Cart cart) {
 List<CartItemResponse> items = cart.getItems().stream().map(ci -> new CartItemResponse(
   ci.getId(),
   ci.getProduct().getId(),
   ci.getProduct().getSku(),
   ci.getProduct().getName(),
   ci.getQty(),
   ci.getUnitPriceCents(),
   ci.getDiscountCents(),
   ci.getTotalCents()
 )).collect(Collectors.toList());

 long subtotal = items.stream().mapToLong(CartItemResponse::totalCents).sum();
 long couponTotal = 0L; // extend: sum cart_coupons
 long grand = Math.max(0L, subtotal - couponTotal);
 return new CartResponse(cart.getId(), cart.getStatus().name(), cart.getCurrency(), items, subtotal, couponTotal, grand);
}
}
