package com.example.oss.repository;

import org.springframework.data.jpa.repository.*;
import com.example.oss.entity.User;
import java.util.*;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByExternalId(String externalId);
  Optional<User> findByEmail(String email);
}
