package com.flrxnt.order.client;

import com.flrxnt.order.dto.ClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client Feign pour la communication avec le service Customer.
 *
 * L'attribut 'contextId' est explicitement défini afin d'éviter toute collision de noms de beans
 * en cas de multiple scans ou de configurations Feign recouvrantes.
 */
@FeignClient(
        name = "customer-service",
        contextId = "customerServiceClient",
        url = "${app.services.customer-service.url:http://localhost:8081}",
        path = "/api/customers"
)
public interface ClientServiceClient {

    /**
     * Récupère un client par son identifiant.
     *
     * @param id l'identifiant du client
     * @return le DTO du client
     */
    @GetMapping("/{id}")
    ClientDTO findById(@PathVariable("id") Long id);
}
