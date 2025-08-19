package com.flrxnt.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "DTO pour mettre à jour un produit existant")
public class ProductUpdateDTO {

    @Size(max = 255, message = "Le nom ne peut pas dépasser 255 caractères")
    @Schema(description = "Nom du produit", example = "Smartphone Samsung Galaxy S24")
    private String nom;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    @Schema(description = "Description détaillée du produit", example = "Smartphone Android avec écran AMOLED 6.1 pouces et 5G")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    @Schema(description = "Prix unitaire du produit", example = "349.99")
    private BigDecimal prix;

    @Min(value = 0, message = "La quantité en stock ne peut pas être négative")
    @Schema(description = "Quantité en stock", example = "200")
    private Integer quantiteStock;

    @Size(max = 100, message = "La catégorie ne peut pas dépasser 100 caractères")
    @Schema(description = "Catégorie du produit", example = "Électronique")
    private String categorie;

    // Constructeurs
    public ProductUpdateDTO() {
    }

    public ProductUpdateDTO(String nom, String description, BigDecimal prix, Integer quantiteStock, String categorie) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.quantiteStock = quantiteStock;
        this.categorie = categorie;
    }

    // Getters et Setters
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

    // Méthode utilitaire pour vérifier si au moins un champ est fourni pour la mise à jour
    public boolean hasUpdates() {
        return nom != null || description != null || prix != null ||
               quantiteStock != null || categorie != null;
    }

    @Override
    public String toString() {
        return "ProductUpdateDTO{" +
                "nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", quantiteStock=" + quantiteStock +
                ", categorie='" + categorie + '\'' +
                '}';
    }
}
