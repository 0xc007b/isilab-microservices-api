package com.flrxnt.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entité représentant un article d'une commande
 */
@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_item_order_id", columnList = "order_id"),
    @Index(name = "idx_order_item_product_id", columnList = "product_id")
})
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'ID du produit est obligatoire")
    @Positive(message = "L'ID du produit doit être positif")
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull(message = "Le nom du produit est obligatoire")
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix unitaire doit être positif")
    @Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "prix_total", precision = 10, scale = 2)
    private BigDecimal prixTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @CreationTimestamp
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Version
    private Long version;

    // Constructeurs
    public OrderItem() {
    }

    public OrderItem(Long productId, String productName, Integer quantite, BigDecimal prixUnitaire) {
        this.productId = productId;
        this.productName = productName;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        calculateTotalPrice();
    }

    // Méthodes utilitaires
    public BigDecimal getTotalPrice() {
        if (prixTotal == null) {
            calculateTotalPrice();
        }
        return prixTotal;
    }

    public void calculateTotalPrice() {
        if (quantite != null && prixUnitaire != null) {
            this.prixTotal = prixUnitaire.multiply(BigDecimal.valueOf(quantite));
        } else {
            this.prixTotal = BigDecimal.ZERO;
        }
    }

    public void updateQuantity(Integer newQuantity) {
        this.quantite = newQuantity;
        calculateTotalPrice();
        if (order != null) {
            order.calculateTotal();
        }
    }

    public void updatePrice(BigDecimal newPrice) {
        this.prixUnitaire = newPrice;
        calculateTotalPrice();
        if (order != null) {
            order.calculateTotal();
        }
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

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
        calculateTotalPrice();
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
        calculateTotalPrice();
    }

    public BigDecimal getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantite=" + quantite +
                ", prixUnitaire=" + prixUnitaire +
                ", prixTotal=" + prixTotal +
                '}';
    }
}
