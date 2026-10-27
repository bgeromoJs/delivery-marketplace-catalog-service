package com.delivery.catalog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8083}")
    private String serverPort;

    @Bean
    public OpenAPI catalogOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Catalog Service")
                        .version("v1")
                        .description("""
                                Cardápio, categorias e pratos das lojas.

                                - `/api/catalog/**` — API pública de catálogo
                                - `/api/store/me/dishes/**` — CRUD usado pelo app do vendedor
                                  (foto, horários de venda e opcionais)
                                """))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("API Gateway"),
                        new Server().url("http://localhost:" + serverPort).description("Serviço direto")));
    }
}
