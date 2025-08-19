package com.flrxnt.order.repository;

import com.flrxnt.order.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour la gestion des articles de commande
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Trouve tous les articles d'une commande spécifique
     * @param orderId l'identifiant de la commande
     * @return la liste des articles de la commande
     */
    List<OrderItem> findByOrderIdOrderByIdAsc(Long orderId);

    /**
     * Trouve tous les articles contenant un produit spécifique
     * @param productId l'identifiant du produit
     * @return la liste des articles contenant ce produit
     */
    List<OrderItem> findByProductIdOrderByDateCreationDesc(Long productId);

    /**
     * Trouve tous les articles contenant un produit spécifique avec pagination
     * @param productId l'identifiant du produit
     * @param pageable pagination
     * @return page des articles contenant ce produit
     */
    Page<OrderItem> findByProductId(Long productId, Pageable pageable);

    /**
     * Compte le nombre d'articles pour une commande
     * @param orderId l'identifiant de la commande
     * @return le nombre d'articles
     */
    long countByOrderId(Long orderId);

    /**
     * Compte le nombre total d'un produit vendu
     * @param productId l'identifiant du produit
     * @return la quantité totale vendue
     */
    @Query("SELECT COALESCE(SUM(oi.quantite), 0) FROM OrderItem oi WHERE oi.productId = :productId")
    Long getTotalQuantitySoldByProductId(@Param("productId") Long productId);

    /**
     * Trouve les articles d'une commande par nom de produit
     * @param orderId l'identifiant de la commande
     * @param productName le nom du produit (recherche partielle)
     * @return la liste des articles correspondants
     */
    List<OrderItem> findByOrderIdAndProductNameContainingIgnoreCase(Long orderId, String productName);

    /**
     * Trouve les articles créés entre deux dates
     * @param dateDebut date de début
     * @param dateFin date de fin
     * @return la liste des articles dans cette période
     */
    List<OrderItem> findByDateCreationBetweenOrderByDateCreationDesc(
            LocalDateTime dateDebut, LocalDateTime dateFin);

    /**
     * Trouve les produits les plus vendus
     * @param pageable pagination
     * @return la liste des produits avec leur quantité totale vendue
     */
    @Query("SELECT oi.productId, oi.productName, SUM(oi.quantite) as totalQuantity, COUNT(DISTINCT oi.order.id) as orderCount " +
           "FROM OrderItem oi " +
           "GROUP BY oi.productId, oi.productName " +
           "ORDER BY SUM(oi.quantite) DESC")
    List<Object[]> findTopSellingProducts(Pageable pageable);

    /**
     * Trouve les articles d'un client spécifique via les commandes
     * @param clientId l'identifiant du client
     * @return la liste des articles commandés par ce client
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.clientId = :clientId ORDER BY oi.dateCreation DESC")
    List<OrderItem> findByClientId(@Param("clientId") Long clientId);

    /**
     * Calcule le chiffre d'affaires d'un produit
     * @param productId l'identifiant du produit
     * @return le montant total généré par ce produit
     */
    @Query("SELECT COALESCE(SUM(oi.prixTotal), 0) FROM OrderItem oi WHERE oi.productId = :productId")
    Double calculateRevenueByProductId(@Param("productId") Long productId);

    /**
     * Trouve les articles avec une quantité supérieure à un seuil
     * @param quantiteMinimale la quantité minimale
     * @return la liste des articles avec quantité >= seuil
     */
    List<OrderItem> findByQuantiteGreaterThanEqualOrderByQuantiteDesc(Integer quantiteMinimale);

    /**
     * Statistiques des ventes par produit dans une période
     * @param dateDebut date de début
     * @param dateFin date de fin
     * @return les statistiques par produit
     */
    @Query("SELECT oi.productId, oi.productName, " +
           "SUM(oi.quantite) as totalQuantity, " +
           "SUM(oi.prixTotal) as totalRevenue, " +
           "AVG(oi.prixUnitaire) as avgPrice, " +
           "COUNT(DISTINCT oi.order.id) as distinctOrders " +
           "FROM OrderItem oi " +
           "WHERE oi.dateCreation BETWEEN :dateDebut AND :dateFin " +
           "GROUP BY oi.productId, oi.productName " +
           "ORDER BY SUM(oi.prixTotal) DESC")
    List<Object[]> getProductSalesStatistics(
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin);

    /**
     * Trouve les articles d'une commande avec une quantité spécifique
     * @param orderId l'identifiant de la commande
     * @param quantite la quantité recherchée
     * @return la liste des articles correspondants
     */
    List<OrderItem> findByOrderIdAndQuantite(Long orderId, Integer quantite);

    /**
     * Supprime tous les articles d'une commande
     * @param orderId l'identifiant de la commande
     */
    void deleteByOrderId(Long orderId);

    /**
     * Vérifie si un produit est présent dans des commandes
     * @param productId l'identifiant du produit
     * @return true si le produit est utilisé dans des commandes
     */
    boolean existsByProductId(Long productId);

    /**
     * Trouve le dernier article ajouté pour un produit
     * @param productId l'identifiant du produit
     * @return le dernier article ou null
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.productId = :productId ORDER BY oi.dateCreation DESC LIMIT 1")
    OrderItem findLatestByProductId(@Param("productId") Long productId);

    /**
     * Calcule la valeur moyenne d'un panier (articles par commande)
     * @return la valeur moyenne d'un panier
     */
    @Query("SELECT AVG(orderTotal.total) FROM " +
           "(SELECT SUM(oi.prixTotal) as total FROM OrderItem oi GROUP BY oi.order.id) orderTotal")
    Double calculateAverageBasketValue();

    /**
     * Trouve les doublons potentiels dans une commande (même produit plusieurs fois)
     * @param orderId l'identifiant de la commande
     * @return la liste des produits en doublon
     */
    @Query("SELECT oi.productId, COUNT(oi) as itemCount FROM OrderItem oi " +
           "WHERE oi.order.id = :orderId " +
           "GROUP BY oi.productId HAVING COUNT(oi) > 1")
    List<Object[]> findDuplicateProductsInOrder(@Param("orderId") Long orderId);
}
