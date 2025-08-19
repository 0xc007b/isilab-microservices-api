package com.flrxnt.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO pour représenter un produit (utilisé pour la communication avec le service produit)
 */
@Schema(description = "Représentation d'un produit")
public class ProductDTO {

    @Schema(description = "Identifiant unique du produit", example = "1")
    private Long id;

    @Schema(description = "Nom du produit", example = "Ordinateur portable")
    private String nom;

    @Schema(description = "Description du produit", example = "Ordinateur portable 15 pouces avec 16GB RAM")
    private String description;

    @Schema(description = "Prix du produit", example = "750.00")
    private BigDecimal prix;

    @Schema(description = "Quantité en stock", example = "25")
    @JsonProperty("quantite_stock")
    private Integer quantiteStock;

    @Schema(description = "Catégorie du produit", example = "Informatique")
    private String categorie;

    @Schema(description = "SKU du produit", example = "PC-HP-15-001")
    private String sku;

    @Schema(description = "Marque du produit", example = "HP")
    private String marque;

    @Schema(description = "Statut du produit", example = "ACTIVE")
    private String statut;

    @Schema(description = "Date de création du produit")
    @JsonProperty("date_creation")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateCreation;

    @Schema(description = "Date de dernière modification")
    @JsonProperty("date_modification")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateModification;

    // Constructeurs
    public ProductDTO() {
    }

    public ProductDTO(Long id, String nom, String description, BigDecimal prix, Integer quantiteStock, String categorie) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        this.categorie = categorie;
    }

    // Méthodes utilitaires
    public boolean isAvailable() {
        return "ACTIVE".equals(statut) && quantiteStock != null && quantiteStock > 0;
    }

    public boolean hasStock(Integer requestedQuantity) {
        return quantiteStock != null && quantiteStock >= requestedQuantity;
    }

    public String getDisplayName() {
        return (marque != null ? marque + " " : "") + (nom != null ? nom : "");
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public Integer getQuantiteStock() {
        return quantiteStock;
    }

    public void setQuantiteStock(Integer quantiteStock) {
        this.quantiteStock = quantiteStock;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
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

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", quantiteStock=" + quantiteStock +
                ", categorie='" + categorie + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}
