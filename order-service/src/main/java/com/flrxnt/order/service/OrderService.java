package com.flrxnt.order.service;

import com.flrxnt.order.dto.OrderCreateDTO;
import com.flrxnt.order.dto.OrderDTO;
import com.flrxnt.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface du service de gestion des commandes
 */
public interface OrderService {

    /**
     * Crée une nouvelle commande
     * @param orderCreateDTO les données de création de la commande
     * @return la commande créée
     */
    OrderDTO createOrder(OrderCreateDTO orderCreateDTO);

    /**
     * Trouve une commande par son identifiant
     * @param id l'identifiant de la commande
     * @return la commande trouvée
     */
    OrderDTO findById(Long id);

    /**
     * Trouve toutes les commandes avec pagination
     * @param pageable pagination
     * @return page des commandes
     */
    Page<OrderDTO> findAll(Pageable pageable);

    /**
     * Trouve toutes les commandes d'un client
     * @param clientId l'identifiant du client
     * @return la liste des commandes du client
     */
    List<OrderDTO> findByClientId(Long clientId);

    /**
     * Trouve toutes les commandes d'un client avec pagination
     * @param clientId l'identifiant du client
     * @param pageable pagination
     * @return page des commandes du client
     */
    Page<OrderDTO> findByClientId(Long clientId, Pageable pageable);

    /**
     * Trouve toutes les commandes par statut
     * @param statut le statut recherché
     * @return la liste des commandes avec ce statut
     */
    List<OrderDTO> findByStatut(Order.OrderStatus statut);

    /**
     * Trouve toutes les commandes par statut avec pagination
     * @param statut le statut recherché
     * @param pageable pagination
     * @return page des commandes avec ce statut
     */
    Page<OrderDTO> findByStatut(Order.OrderStatus statut, Pageable pageable);

    /**
     * Trouve les commandes créées entre deux dates
     * @param dateDebut date de début
     * @param dateFin date de fin
     * @return la liste des commandes dans cette période
     */
    List<OrderDTO> findByDateRange(LocalDateTime dateDebut, LocalDateTime dateFin);

    /**
     * Met à jour le statut d'une commande
     * @param id l'identifiant de la commande
     * @param nouveauStatut le nouveau statut
     * @return la commande mise à jour
     */
    OrderDTO updateStatus(Long id, Order.OrderStatus nouveauStatut);

    /**
     * Met à jour le commentaire d'une commande
     * @param id l'identifiant de la commande
     * @param commentaire le nouveau commentaire
     * @return la commande mise à jour
     */
    OrderDTO updateComment(Long id, String commentaire);

    /**
     * Annule une commande
     * @param id l'identifiant de la commande
     * @return la commande annulée
     */
    OrderDTO cancelOrder(Long id);

    /**
     * Supprime une commande
     * @param id l'identifiant de la commande
     */
    void deleteOrder(Long id);

    /**
     * Calcule le montant total des commandes d'un client
     * @param clientId l'identifiant du client
     * @return le montant total
     */
    Double calculateTotalAmountByClientId(Long clientId);

    /**
     * Compte le nombre de commandes d'un client
     * @param clientId l'identifiant du client
     * @return le nombre de commandes
     */
    long countByClientId(Long clientId);

    /**
     * Vérifie si un client a des commandes en cours
     * @param clientId l'identifiant du client
     * @return true si le client a des commandes en cours
     */
    boolean hasActiveOrders(Long clientId);

    /**
     * Trouve les commandes récentes (dernières 24h)
     * @return la liste des commandes récentes
     */
    List<OrderDTO> findRecentOrders();

    /**
     * Trouve les commandes qui nécessitent une attention
     * @return la liste des commandes nécessitant attention
     */
    List<OrderDTO> findOrdersNeedingAttention();

    /**
     * Trouve les top clients par nombre de commandes
     * @param limit nombre maximum de résultats
     * @return la liste des clients avec leur nombre de commandes
     */
    List<Object[]> findTopClientsByOrderCount(int limit);

    /**
     * Trouve les commandes contenant un produit spécifique
     * @param productId l'identifiant du produit
     * @return la liste des commandes contenant ce produit
     */
    List<OrderDTO> findByProductId(Long productId);

    /**
     * Obtient les statistiques des commandes par jour
     * @param dateDebut date de début
     * @param dateFin date de fin
     * @return les statistiques par jour
     */
    List<Object[]> getOrderStatisticsByDay(LocalDateTime dateDebut, LocalDateTime dateFin);

    /**
     * Valide qu'une commande peut être modifiée
     * @param orderId l'identifiant de la commande
     * @return true si la commande peut être modifiée
     */
    boolean canBeModified(Long orderId);

    /**
     * Valide qu'une commande peut être annulée
     * @param orderId l'identifiant de la commande
     * @return true si la commande peut être annulée
     */
    boolean canBeCancelled(Long orderId);
}
