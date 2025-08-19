package com.flrxnt.product.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "DTO pour représenter un produit dans les réponses API")
public class ProductDTO {

    @Schema(description = "Identifiant unique du produit", example = "1")
    private Long id;

    @Schema(description = "Nom du produit", example = "Smartphone Samsung Galaxy")
    private String nom;

    @Schema(description = "Description détaillée du produit", example = "Smartphone Android avec écran AMOLED 6.1 pouces")
    private String description;

    @Schema(description = "Prix unitaire du produit", example = "299.99")
    private BigDecimal prix;

    @Schema(description = "Quantité disponible en stock", example = "150")
    private Integer quantiteStock;

    @Schema(description = "Catégorie du produit", example = "Électronique")
    private String categorie;

    @Schema(description = "Date de création du produit")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateCreation;

    @Schema(description = "Date de dernière modification du produit")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateModification;

    // Constructeurs
    public ProductDTO() {
    }

    public ProductDTO(Long id, String nom, String description, BigDecimal prix,
                     Integer quantiteStock, String categorie, LocalDateTime dateCreation,
                     LocalDateTime dateModification) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        this.categorie = categorie;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
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
                ", dateCreation=" + dateCreation +
                ", dateModification=" + dateModification +
                '}';
    }
}
