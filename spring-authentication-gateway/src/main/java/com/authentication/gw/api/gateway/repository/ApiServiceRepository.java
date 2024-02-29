package com.authentication.gw.api.gateway.repository;

import com.authentication.gw.api.gateway.entity.ApiService;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiServiceRepository extends ReactiveCrudRepository<ApiService, String> {
}
