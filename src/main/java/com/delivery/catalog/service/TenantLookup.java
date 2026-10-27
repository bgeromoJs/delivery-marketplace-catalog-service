package com.delivery.catalog.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Descobre o tenant (loja) atual consultando o tenant-service.
 * Como o app do vendedor ainda não envia o tenant no token, usamos a primeira loja ativa.
 */
@Component
@Slf4j
public class TenantLookup {

    @Value("${services.tenant.url:http://localhost:8082}")
    private String tenantServiceUrl;

    public UUID resolve() {
        try {
            RestClient client = RestClient.create();
            List<Map<String, Object>> tenants = client.get()
                    .uri(tenantServiceUrl + "/api/tenants")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    });
            if (tenants != null && !tenants.isEmpty()) {
                Object id = tenants.get(0).get("id");
                if (id != null) {
                    return UUID.fromString(id.toString());
                }
            }
        } catch (Exception e) {
            log.warn("Não foi possível consultar o tenant-service: {}", e.getMessage());
        }
        return null;
    }
}
