package com.flrxnt.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entité représentant une commande dans le système
 */
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_client_id", columnList = "client_id"),
    @Index(name = "idx_order_date_commande", columnList = "date_commande"),
    @Index(name = "idx_order_statut", columnList = "statut")
})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'ID du client est obligatoire")
    @Positive(message = "L'ID du client doit être positif")
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @CreationTimestamp
    @Column(name = "date_commande", nullable = false, updatable = false)
    private LocalDateTime dateCommande;

    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @NotNull(message = "Le montant total est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant total doit être positif")
    @Column(name = "montant_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private OrderStatus statut;

    @Column(name = "commentaire", length = 500)
    private String commentaire;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Version
    private Long version;

    // Constructeurs
    public Order() {
        this.statut = OrderStatus.PENDING;
        this.montantTotal = BigDecimal.ZERO;
    }

    public Order(Long clientId, String commentaire) {
        this();
        this.clientId = clientId;
        this.commentaire = commentaire;
    }

    // Méthodes utilitaires
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        calculateTotal();
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
        calculateTotal();
    }

    public void calculateTotal() {
        this.montantTotal = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean canBeModified() {
        return statut == OrderStatus.PENDING || statut == OrderStatus.CONFIRMED;
    }

    public boolean canBeCancelled() {
        return statut != OrderStatus.DELIVERED && statut != OrderStatus.CANCELLED;
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

    public OrderStatus getStatut() {
        return statut;
    }

    public void setStatut(OrderStatus statut) {
        this.statut = statut;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        calculateTotal();
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
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", dateCommande=" + dateCommande +
                ", montantTotal=" + montantTotal +
                ", statut=" + statut +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                '}';
    }

    /**
     * Enumération des statuts possibles d'une commande
     */
    public enum OrderStatus {
        PENDING("En attente"),
        CONFIRMED("Confirmée"),
        PROCESSING("En cours de traitement"),
        SHIPPED("Expédiée"),
        DELIVERED("Livrée"),
        CANCELLED("Annulée");

        private final String description;

        OrderStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}
