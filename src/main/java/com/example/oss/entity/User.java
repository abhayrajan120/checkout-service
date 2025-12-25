package com.example.oss.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="users")
@Getter @Setter
public class User {
@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
private Long id;

@Column(name="external_id", unique=true)
private String externalId;

@Column(unique=true)
private String email;
}

