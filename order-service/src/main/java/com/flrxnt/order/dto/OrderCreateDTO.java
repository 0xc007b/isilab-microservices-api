package com.flrxnt.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO pour la création d'une nouvelle commande
 */
@Schema(description = "DTO pour créer une nouvelle commande")
public class OrderCreateDTO {

    @Schema(description = "Identifiant du client", example = "123", required = true)
    @NotNull(message = "L'ID du client est obligatoire")
    @Positive(message = "L'ID du client doit être positif")
    @JsonProperty("client_id")
    private Long clientId;

    @Schema(description = "Commentaire sur la commande", example = "Livraison urgente")
    @Size(max = 500, message = "Le commentaire ne peut pas dépasser 500 caractères")
    private String commentaire;

    @Schema(description = "Liste des articles à commander", required = true)
    @NotEmpty(message = "La commande doit contenir au moins un article")
    @Valid
    private List<OrderItemCreateDTO> items;

    // Constructeurs
    public OrderCreateDTO() {
    }

    public OrderCreateDTO(Long clientId, String commentaire, List<OrderItemCreateDTO> items) {
        this.clientId = clientId;
        this.commentaire = commentaire;
        this.items = items;
    }

    // Getters et Setters
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public List<OrderItemCreateDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemCreateDTO> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "OrderCreateDTO{" +
                "clientId=" + clientId +
                ", commentaire='" + commentaire + '\'' +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                '}';
    }
}
