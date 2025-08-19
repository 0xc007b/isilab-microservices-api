package com.flrxnt.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO pour les opérations de mise à jour du stock")
public class StockUpdateDTO {

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 0, message = "La quantité ne peut pas être négative")
    @Schema(description = "Quantité à utiliser pour l'opération de stock", example = "50", required = true)
    private Integer quantite;

    @Schema(description = "Type d'opération sur le stock", example = "SET",
           allowableValues = {"SET", "INCREMENT", "DECREMENT"})
    private StockOperationType operation;

    @Schema(description = "Raison de la modification du stock", example = "Réapprovisionnement")
    private String raison;

    // Enum pour les types d'opérations
    public enum StockOperationType {
        SET("Définir la quantité"),
        INCREMENT("Incrémenter le stock"),
        DECREMENT("Décrémenter le stock");

        private final String description;

        StockOperationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Constructeurs
    public StockUpdateDTO() {
        this.operation = StockOperationType.SET;
    }

    public StockUpdateDTO(Integer quantite) {
        this.quantite = quantite;
        this.operation = StockOperationType.SET;
    }

    public StockUpdateDTO(Integer quantite, StockOperationType operation) {
        this.quantite = quantite;
        this.operation = operation;
    }

    public StockUpdateDTO(Integer quantite, StockOperationType operation, String raison) {
        this.quantite = quantite;
        this.operation = operation;
        this.raison = raison;
    }

    // Getters et Setters
    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public StockOperationType getOperation() {
        return operation;
    }

    public void setOperation(StockOperationType operation) {
        this.operation = operation;
    }

    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }

    // Méthodes utilitaires
    public boolean isSetOperation() {
        return StockOperationType.SET.equals(this.operation);
    }

    public boolean isIncrementOperation() {
        return StockOperationType.INCREMENT.equals(this.operation);
    }

    public boolean isDecrementOperation() {
        return StockOperationType.DECREMENT.equals(this.operation);
    }

    @Override
    public String toString() {
        return "StockUpdateDTO{" +
                "quantite=" + quantite +
                ", operation=" + operation +
                ", raison='" + raison + '\'' +
                '}';
    }
}
