package com.flrxnt.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.flrxnt.order.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour la représentation d'une commande en réponse API
 */
@Schema(description = "Représentation d'une commande")
public class OrderDTO {

    @Schema(description = "Identifiant unique de la commande", example = "1")
    private Long id;

    @Schema(description = "Identifiant du client", example = "123")
    @JsonProperty("client_id")
    private Long clientId;

    @Schema(description = "Informations du client")
    private ClientDTO client;

    @Schema(description = "Date et heure de création de la commande")
    @JsonProperty("date_commande")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateCommande;

    @Schema(description = "Date et heure de dernière modification")
    @JsonProperty("date_modification")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateModification;

    @Schema(description = "Montant total de la commande", example = "150.50")
    @JsonProperty("montant_total")
    private BigDecimal montantTotal;

    @Schema(description = "Statut de la commande", example = "PENDING")
    private Order.OrderStatus statut;

    @Schema(description = "Commentaire sur la commande", example = "Livraison urgente")
    private String commentaire;

    @Schema(description = "Liste des articles de la commande")
    private List<OrderItemDTO> items;

    @Schema(description = "Nombre total d'articles dans la commande")
    @JsonProperty("nombre_items")
    private Integer nombreItems;

    @Schema(description = "Version de l'entité pour le contrôle d'optimistic locking")
    private Long version;

    // Constructeurs
    public OrderDTO() {
    }

    public OrderDTO(Long id, Long clientId, LocalDateTime dateCommande,
                   BigDecimal montantTotal, Order.OrderStatus statut) {
        this.id = id;
        this.clientId = clientId;
        this.dateCommande = dateCommande;
        this.montantTotal = montantTotal;
        this.statut = statut;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public Order.OrderStatus getStatut() {
        return statut;
    }

    public void setStatut(Order.OrderStatus statut) {
        this.statut = statut;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
        this.nombreItems = items != null ? items.size() : 0;
    }

    public Integer getNombreItems() {
        return nombreItems;
    }

    public void setNombreItems(Integer nombreItems) {
        this.nombreItems = nombreItems;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", dateCommande=" + dateCommande +
                ", montantTotal=" + montantTotal +
                ", statut=" + statut +
                ", nombreItems=" + nombreItems +
                '}';
    }
}
