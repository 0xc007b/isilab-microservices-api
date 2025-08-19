package com.flrxnt.order.client;

import com.flrxnt.order.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client Feign pour la communication avec le service Product.
 *
 * contextId explicite afin d'éviter toute collision de noms de beans
 * et chemin de base défini pour une meilleure maintenabilité.
 */
@FeignClient(
        name = "product-service",
        contextId = "productServiceClient",
        url = "${app.services.product-service.url:http://localhost:8082}",
        path = "/api/products"
)
public interface ProductServiceClient {

    /**
     * Récupère un produit par son identifiant.
     *
     * @param id l'identifiant du produit
     * @return le DTO du produit
     */
    @GetMapping("/{id}")
    ProductDTO findById(@PathVariable("id") Long id);
}
