package com.example.oss.entity;


import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity @Table(name="carts")
@Getter @Setter
public class Cart {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
private Long id;

@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id")
private User user;

@Column(name="session_id") private String sessionId;

@Enumerated(EnumType.STRING)
private Status status = Status.ACTIVE;

@Column(length=3) private String currency = "USD";

@OneToMany(mappedBy="cart", cascade=CascadeType.ALL, orphanRemoval=true)
private List<CartItem> items = new ArrayList<>();

public enum Status { ACTIVE, CHECKED_OUT, ABANDONED }
}
