package com.flrxnt.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour la représentation d'un article de commande en réponse API
 */
@Schema(description = "Représentation d'un article de commande")
public class OrderItemDTO {

    @Schema(description = "Identifiant unique de l'article", example = "1")
    private Long id;

    @Schema(description = "Identifiant du produit", example = "456")
    @JsonProperty("product_id")
    private Long productId;

    @Schema(description = "Nom du produit", example = "Ordinateur portable")
    @JsonProperty("product_name")
    private String productName;

    @Schema(description = "Informations détaillées du produit")
    private ProductDTO product;

    @Schema(description = "Quantité commandée", example = "2")
    private Integer quantite;

    @Schema(description = "Prix unitaire du produit", example = "750.00")
    @JsonProperty("prix_unitaire")
    private BigDecimal prixUnitaire;

    @Schema(description = "Prix total de cet article (quantité × prix unitaire)", example = "1500.00")
    @JsonProperty("prix_total")
    private BigDecimal prixTotal;

    @Schema(description = "Date et heure de création de l'article")
    @JsonProperty("date_creation")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateCreation;

    @Schema(description = "Date et heure de dernière modification")
    @JsonProperty("date_modification")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateModification;

    @Schema(description = "Version de l'entité pour le contrôle d'optimistic locking")
    private Long version;

    // Constructeurs
    public OrderItemDTO() {
    }

    public OrderItemDTO(Long id, Long productId, String productName,
                       Integer quantite, BigDecimal prixUnitaire, BigDecimal prixTotal) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.prixTotal = prixTotal;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimal getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "OrderItemDTO{" +
                "id=" + id +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantite=" + quantite +
                ", prixUnitaire=" + prixUnitaire +
                ", prixTotal=" + prixTotal +
                '}';
    }
}
