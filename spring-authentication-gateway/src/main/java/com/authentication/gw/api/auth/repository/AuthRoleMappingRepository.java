package com.authentication.gw.api.auth.repository;

import com.authentication.gw.api.auth.entity.AuthRoleMapping;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRoleMappingRepository extends ReactiveCrudRepository<AuthRoleMapping, String>, AuthRoleMappingCustomRepository {
}
