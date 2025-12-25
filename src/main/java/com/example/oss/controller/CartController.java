package com.example.oss.controller;



import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.oss.dto.CartItemRequest;
import com.example.oss.dto.CartResponse;
import com.example.oss.entity.Cart;
import com.example.oss.service.CartService;

//restcontroller
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

private final CartService cartService;

public CartController(CartService cartService) { this.cartService = cartService; }

/** Create/get cart for user or session. */
@PostMapping("/init")
public ResponseEntity<CartResponse> initCart(@RequestParam(required=false) Long userId,
                                            @RequestParam(required=false) String sessionId) {
 Cart cart = cartService.getOrCreateCart(userId, sessionId);
 return ResponseEntity.ok(cartService.toCartResponse(cart));
}

@GetMapping("/{cartId}")
public ResponseEntity<CartResponse> getCart(@PathVariable Long cartId) {
 Cart cart = cartService.getOrCreateCart(null, null); // or fetch → simplified
 cart.setId(cartId); // for demo; in prod load from repo
 return ResponseEntity.ok(cartService.toCartResponse(cart));
}

@PostMapping("/{cartId}/items")
public ResponseEntity<CartResponse> addItem(@PathVariable Long cartId,
                                           @Valid @RequestBody CartItemRequest request) {
 return ResponseEntity.ok(cartService.addItem(cartId, request.productId(), request.qty()));
}

@PutMapping("/{cartId}/items/{productId}")
public ResponseEntity<CartResponse> setQty(@PathVariable Long cartId, @PathVariable Long productId,
                                          @RequestParam int qty) {
 return ResponseEntity.ok(cartService.setQuantity(cartId, productId, qty));
}

@DeleteMapping("/{cartId}/items/{productId}")
public ResponseEntity<Void> removeItem(@PathVariable Long cartId, @PathVariable Long productId) {
 cartService.removeItem(cartId, productId);
 return ResponseEntity.noContent().build();
}

@PostMapping("/merge")
public ResponseEntity<CartResponse> merge(@RequestParam String sessionId, @RequestParam Long userId) {
 return ResponseEntity.ok(cartService.mergeCarts(sessionId, userId));
}
}
