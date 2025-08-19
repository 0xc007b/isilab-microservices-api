package com.flrxnt.order.repository;


import com.flrxnt.order.entity.Order;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;



import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;

import java.util.Optional;


/**
 * Repository pour la gestion des commandes
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Trouve toutes les commandes d'un client spécifique
     * @param clientId l'identifiant du client
     * @return la liste des commandes du client
     */
    List<Order> findByClientIdOrderByDateCommandeDesc(Long clientId);

    /**
     * Trouve toutes les commandes d'un client avec pagination
     * @param clientId l'identifiant du client
     * @param pageable pagination
     * @return page des commandes du client
     */
    Page<Order> findByClientId(Long clientId, Pageable pageable);

    /**
     * Trouve toutes les commandes par statut
     * @param statut le statut recherché
     * @return la liste des commandes avec ce statut
     */
    List<Order> findByStatutOrderByDateCommandeDesc(Order.OrderStatus statut);

    /**
     * Trouve toutes les commandes par statut avec pagination
     * @param statut le statut recherché
     * @param pageable pagination
     * @return page des commandes avec ce statut
     */
    Page<Order> findByStatut(Order.OrderStatus statut, Pageable pageable);

    /**
     * Trouve les commandes créées entre deux dates
     * @param dateDebut date de début
     * @param dateFin date de fin
     * @return la liste des commandes dans cette période
     */
    List<Order> findByDateCommandeBetweenOrderByDateCommandeDesc(
            LocalDateTime dateDebut, LocalDateTime dateFin);

    /**
     * Trouve les commandes créées entre deux dates avec pagination
     * @param dateDebut date de début
     * @param dateFin date de fin
     * @param pageable pagination
     * @return page des commandes dans cette période
     */
    Page<Order> findByDateCommandeBetween(
            LocalDateTime dateDebut, LocalDateTime dateFin, Pageable pageable);

    /**
     * Trouve les commandes d'un client avec un statut spécifique
     * @param clientId l'identifiant du client
     * @param statut le statut recherché
     * @return la liste des commandes
     */
    List<Order> findByClientIdAndStatutOrderByDateCommandeDesc(Long clientId, Order.OrderStatus statut);

    /**
     * Compte le nombre de commandes d'un client
     * @param clientId l'identifiant du client
     * @return le nombre de commandes
     */
    long countByClientId(Long clientId);

    /**
     * Compte le nombre de commandes par statut
     * @param statut le statut
     * @return le nombre de commandes
     */
    long countByStatut(Order.OrderStatus statut);

    /**
     * Vérifie si un client a des commandes en cours
     * @param clientId l'identifiant du client
     * @return true si le client a des commandes en cours
     */
    boolean existsByClientIdAndStatutIn(Long clientId, List<Order.OrderStatus> statuts);


    /**

     * Trouve les commandes avec leurs items (pagination-safe via EntityGraph)
     * @param pageable pagination

     * @return page des commandes avec leurs items

     */

    @EntityGraph(attributePaths = "items")
    @Query("SELECT o FROM Order o")
    Page<Order> findAllWithItems(Pageable pageable);


    /**
     * Trouve une commande avec ses items
     * @param id l'identifiant de la commande
     * @return la commande avec ses items
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    /**
     * Trouve les commandes d'un client avec leurs items
     * @param clientId l'identifiant du client
     * @return la liste des commandes avec leurs items
     */
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.clientId = :clientId ORDER BY o.dateCommande DESC")
    List<Order> findByClientIdWithItems(@Param("clientId") Long clientId);


    /**

     * Calcule le montant total des commandes d'un client

     * @param clientId l'identifiant du client

     * @return le montant total ou null si aucune commande

     */

    @Query("SELECT SUM(o.montantTotal) FROM Order o WHERE o.clientId = :clientId")

    Optional<BigDecimal> calculateTotalAmountByClientId(@Param("clientId") Long clientId);



    /**

     * Calcule le montant total des commandes par statut

     * @param statut le statut des commandes

     * @return le montant total ou null si aucune commande

     */

    @Query("SELECT SUM(o.montantTotal) FROM Order o WHERE o.statut = :statut")

    Optional<BigDecimal> calculateTotalAmountByStatut(@Param("statut") Order.OrderStatus statut);


    /**
     * Trouve les commandes récentes (dernières 24h)
     * @return la liste des commandes récentes
     */
    @Query("SELECT o FROM Order o WHERE o.dateCommande >= :dateDebut ORDER BY o.dateCommande DESC")
    List<Order> findRecentOrders(@Param("dateDebut") LocalDateTime dateDebut);

    /**
     * Trouve les commandes qui nécessitent une attention (anciennes et en attente)
     * @param dateMax date limite
     * @param statuts statuts à vérifier
     * @return la liste des commandes nécessitant attention
     */
    @Query("SELECT o FROM Order o WHERE o.dateCommande <= :dateMax AND o.statut IN :statuts ORDER BY o.dateCommande ASC")
    List<Order> findOrdersNeedingAttention(
            @Param("dateMax") LocalDateTime dateMax,
            @Param("statuts") List<Order.OrderStatus> statuts);

    /**
     * Trouve les top clients par nombre de commandes
     * @param limit nombre maximum de résultats
     * @return la liste des clients avec leur nombre de commandes
     */
    @Query("SELECT o.clientId, COUNT(o) as orderCount FROM Order o " +
           "GROUP BY o.clientId ORDER BY COUNT(o) DESC")
    List<Object[]> findTopClientsByOrderCount(Pageable pageable);

    /**
     * Trouve les commandes contenant un produit spécifique
     * @param productId l'identifiant du produit
     * @return la liste des commandes contenant ce produit
     */
    @Query("SELECT DISTINCT o FROM Order o JOIN o.items oi WHERE oi.productId = :productId ORDER BY o.dateCommande DESC")
    List<Order> findByProductId(@Param("productId") Long productId);

    /**
     * Statistiques des commandes par jour
     * @param dateDebut date de début
     * @param dateFin date de fin
     * @return les statistiques par jour
     */

        @Query(value = "SELECT DATE(o.date_commande) AS order_date, COUNT(*) AS order_count, SUM(o.montant_total) AS total_amount " +
               "FROM orders o WHERE o.date_commande BETWEEN :dateDebut AND :dateFin " +

               "GROUP BY DATE(o.date_commande) ORDER BY DATE(o.date_commande)", nativeQuery = true)

        List<Object[]> getOrderStatisticsByDay(

                @Param("dateDebut") LocalDateTime dateDebut,

                @Param("dateFin") LocalDateTime dateFin);

}
