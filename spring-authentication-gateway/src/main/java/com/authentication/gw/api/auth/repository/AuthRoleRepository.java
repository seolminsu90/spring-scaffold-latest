package com.authentication.gw.api.auth.repository;

import com.authentication.gw.api.auth.entity.AuthRole;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AuthRoleRepository extends ReactiveCrudRepository<AuthRole, String> {
}
