package com.flrxnt.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO pour la création d'un article de commande
 */
@Schema(description = "DTO pour créer un article de commande")
public class OrderItemCreateDTO {

    @Schema(description = "Identifiant du produit", example = "456", required = true)
    @NotNull(message = "L'ID du produit est obligatoire")
    @Positive(message = "L'ID du produit doit être positif")
    @JsonProperty("product_id")
    private Long productId;

    @Schema(description = "Quantité à commander", example = "2", required = true)
    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    private Integer quantite;

    // Constructeurs
    public OrderItemCreateDTO() {
    }

    public OrderItemCreateDTO(Long productId, Integer quantite) {
        this.productId = productId;
        this.quantite = quantite;
    }

    // Getters et Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return "OrderItemCreateDTO{" +
                "productId=" + productId +
                ", quantite=" + quantite +
                '}';
    }
}
