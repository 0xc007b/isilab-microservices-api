package com.flrxnt.order.controller;

import com.flrxnt.order.dto.OrderCreateDTO;
import com.flrxnt.order.dto.OrderDTO;
import com.flrxnt.order.entity.Order;
import com.flrxnt.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des commandes
 */
@Tag(name = "Order Management", description = "API pour la gestion des commandes")
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Crée une nouvelle commande
     */
    @Operation(summary = "Créer une nouvelle commande", description = "Crée une nouvelle commande avec les articles spécifiés")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Commande créée avec succès",
                    content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Données de création invalides"),
            @ApiResponse(responseCode = "404", description = "Client ou produit non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @Parameter(description = "Données de création de la commande", required = true)
            @Valid @RequestBody OrderCreateDTO orderCreateDTO) {

        logger.info("Création d'une nouvelle commande pour le client: {}", orderCreateDTO.getClientId());

        OrderDTO createdOrder = orderService.createOrder(orderCreateDTO);

        logger.info("Commande créée avec succès avec l'ID: {}", createdOrder.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    /**
     * Récupère une commande par son ID
     */
    @Operation(summary = "Récupérer une commande", description = "Récupère une commande par son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande trouvée",
                    content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(
            @Parameter(description = "Identifiant de la commande", required = true, example = "1")
            @PathVariable Long id) {

        logger.debug("Récupération de la commande avec l'ID: {}", id);

        OrderDTO order = orderService.findById(id);

        return ResponseEntity.ok(order);
    }

    /**
     * Récupère toutes les commandes avec pagination
     */
    @Operation(summary = "Lister toutes les commandes", description = "Récupère toutes les commandes avec pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des commandes récupérée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @Parameter(description = "Numéro de page (commence à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Critère de tri", example = "dateCommande")
            @RequestParam(defaultValue = "dateCommande") String sortBy,
            @Parameter(description = "Direction du tri", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir) {

        logger.debug("Récupération de toutes les commandes - page: {}, size: {}", page, size);

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderDTO> orders = orderService.findAll(pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * Récupère les commandes d'un client spécifique
     */
    @Operation(summary = "Lister les commandes d'un client", description = "Récupère toutes les commandes d'un client spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des commandes du client récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByClientId(
            @Parameter(description = "Identifiant du client", required = true, example = "1")
            @PathVariable Long clientId) {

        logger.debug("Récupération des commandes du client: {}", clientId);

        List<OrderDTO> orders = orderService.findByClientId(clientId);

        return ResponseEntity.ok(orders);
    }

    /**
     * Récupère les commandes d'un client avec pagination
     */
    @Operation(summary = "Lister les commandes d'un client avec pagination", description = "Récupère les commandes d'un client avec pagination")
    @GetMapping("/client/{clientId}/paged")
    public ResponseEntity<Page<OrderDTO>> getOrdersByClientIdPaged(
            @Parameter(description = "Identifiant du client", required = true, example = "1")
            @PathVariable Long clientId,
            @Parameter(description = "Numéro de page (commence à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Critère de tri", example = "dateCommande")
            @RequestParam(defaultValue = "dateCommande") String sortBy,
            @Parameter(description = "Direction du tri", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir) {

        logger.debug("Récupération des commandes du client {} - page: {}, size: {}", clientId, page, size);

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<OrderDTO> orders = orderService.findByClientId(clientId, pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * Récupère les commandes par statut
     */
    @Operation(summary = "Lister les commandes par statut", description = "Récupère toutes les commandes ayant un statut spécifique")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(
            @Parameter(description = "Statut des commandes", required = true, example = "PENDING")
            @PathVariable Order.OrderStatus status) {

        logger.debug("Récupération des commandes avec le statut: {}", status);

        List<OrderDTO> orders = orderService.findByStatut(status);

        return ResponseEntity.ok(orders);
    }

    /**
     * Récupère les commandes par statut avec pagination
     */
    @Operation(summary = "Lister les commandes par statut avec pagination")
    @GetMapping("/status/{status}/paged")
    public ResponseEntity<Page<OrderDTO>> getOrdersByStatusPaged(
            @Parameter(description = "Statut des commandes", required = true, example = "PENDING")
            @PathVariable Order.OrderStatus status,
            @Parameter(description = "Numéro de page (commence à 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        logger.debug("Récupération des commandes avec le statut {} - page: {}, size: {}", status, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateCommande"));
        Page<OrderDTO> orders = orderService.findByStatut(status, pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * Récupère les commandes par période
     */
    @Operation(summary = "Lister les commandes par période", description = "Récupère les commandes créées dans une période donnée")
    @GetMapping("/period")
    public ResponseEntity<List<OrderDTO>> getOrdersByPeriod(
            @Parameter(description = "Date de début", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @Parameter(description = "Date de fin", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {

        logger.debug("Récupération des commandes entre {} et {}", dateDebut, dateFin);

        List<OrderDTO> orders = orderService.findByDateRange(dateDebut, dateFin);

        return ResponseEntity.ok(orders);
    }

    /**
     * Met à jour le statut d'une commande
     */
    @Operation(summary = "Mettre à jour le statut d'une commande", description = "Met à jour le statut d'une commande existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Transition de statut invalide"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
            @ApiResponse(responseCode = "409", description = "Conflit - opération non autorisée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @Parameter(description = "Identifiant de la commande", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouveau statut", required = true)
            @RequestBody Map<String, String> request) {

        logger.info("Mise à jour du statut de la commande: {}", id);

        String statusString = request.get("status");
        if (statusString == null || statusString.trim().isEmpty()) {
            throw new IllegalArgumentException("Le statut est requis");
        }

        Order.OrderStatus newStatus;
        try {
            newStatus = Order.OrderStatus.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Statut invalide: " + statusString);
        }

        OrderDTO updatedOrder = orderService.updateStatus(id, newStatus);

        logger.info("Statut de la commande {} mis à jour vers: {}", id, newStatus);

        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Met à jour le commentaire d'une commande
     */
    @Operation(summary = "Mettre à jour le commentaire d'une commande", description = "Met à jour le commentaire d'une commande existante")
    @PutMapping("/{id}/comment")
    public ResponseEntity<OrderDTO> updateOrderComment(
            @Parameter(description = "Identifiant de la commande", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouveau commentaire", required = true)
            @RequestBody Map<String, String> request) {

        logger.info("Mise à jour du commentaire de la commande: {}", id);

        String commentaire = request.get("commentaire");

        OrderDTO updatedOrder = orderService.updateComment(id, commentaire);

        logger.info("Commentaire de la commande {} mis à jour", id);

        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Annule une commande
     */
    @Operation(summary = "Annuler une commande", description = "Annule une commande existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande annulée avec succès",
                    content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
            @ApiResponse(responseCode = "409", description = "La commande ne peut pas être annulée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(
            @Parameter(description = "Identifiant de la commande", required = true, example = "1")
            @PathVariable Long id) {

        logger.info("Annulation de la commande: {}", id);

        OrderDTO cancelledOrder = orderService.cancelOrder(id);

        logger.info("Commande {} annulée avec succès", id);

        return ResponseEntity.ok(cancelledOrder);
    }

    /**
     * Supprime une commande
     */
    @Operation(summary = "Supprimer une commande", description = "Supprime définitivement une commande")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Commande supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
            @ApiResponse(responseCode = "409", description = "La commande ne peut pas être supprimée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Identifiant de la commande", required = true, example = "1")
            @PathVariable Long id) {

        logger.info("Suppression de la commande: {}", id);

        orderService.deleteOrder(id);

        logger.info("Commande {} supprimée avec succès", id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Récupère les commandes récentes (dernières 24h)
     */
    @Operation(summary = "Lister les commandes récentes", description = "Récupère les commandes des dernières 24 heures")
    @GetMapping("/recent")
    public ResponseEntity<List<OrderDTO>> getRecentOrders() {
        logger.debug("Récupération des commandes récentes");

        List<OrderDTO> orders = orderService.findRecentOrders();

        return ResponseEntity.ok(orders);
    }

    /**
     * Récupère les commandes nécessitant une attention
     */
    @Operation(summary = "Lister les commandes nécessitant une attention", description = "Récupère les commandes anciennes en attente")
    @GetMapping("/attention")
    public ResponseEntity<List<OrderDTO>> getOrdersNeedingAttention() {
        logger.debug("Récupération des commandes nécessitant une attention");

        List<OrderDTO> orders = orderService.findOrdersNeedingAttention();

        return ResponseEntity.ok(orders);
    }

    /**
     * Récupère les commandes contenant un produit spécifique
     */
    @Operation(summary = "Lister les commandes contenant un produit", description = "Récupère toutes les commandes contenant un produit spécifique")
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByProductId(
            @Parameter(description = "Identifiant du produit", required = true, example = "1")
            @PathVariable Long productId) {

        logger.debug("Récupération des commandes contenant le produit: {}", productId);

        List<OrderDTO> orders = orderService.findByProductId(productId);

        return ResponseEntity.ok(orders);
    }

    /**
     * Récupère les statistiques d'un client
     */
    @Operation(summary = "Statistiques d'un client", description = "Récupère les statistiques de commandes d'un client")
    @GetMapping("/client/{clientId}/stats")
    public ResponseEntity<Map<String, Object>> getClientStats(
            @Parameter(description = "Identifiant du client", required = true, example = "1")
            @PathVariable Long clientId) {

        logger.debug("Récupération des statistiques du client: {}", clientId);

        long orderCount = orderService.countByClientId(clientId);
        Double totalAmount = orderService.calculateTotalAmountByClientId(clientId);
        boolean hasActiveOrders = orderService.hasActiveOrders(clientId);

        Map<String, Object> stats = Map.of(
                "clientId", clientId,
                "totalOrders", orderCount,
                "totalAmount", totalAmount != null ? totalAmount : 0.0,
                "hasActiveOrders", hasActiveOrders
        );

        return ResponseEntity.ok(stats);
    }

    /**
     * Récupère les statistiques des commandes par jour
     */
    @Operation(summary = "Statistiques par jour", description = "Récupère les statistiques des commandes par jour pour une période")
    @GetMapping("/stats/daily")
    public ResponseEntity<List<Object[]>> getDailyOrderStats(
            @Parameter(description = "Date de début", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @Parameter(description = "Date de fin", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {

        logger.debug("Récupération des statistiques journalières entre {} et {}", dateDebut, dateFin);

        List<Object[]> stats = orderService.getOrderStatisticsByDay(dateDebut, dateFin);

        return ResponseEntity.ok(stats);
    }

    /**
     * Vérifie si une commande peut être modifiée
     */
    @Operation(summary = "Vérifier si une commande peut être modifiée")
    @GetMapping("/{id}/can-modify")
    public ResponseEntity<Map<String, Boolean>> canModifyOrder(
            @Parameter(description = "Identifiant de la commande", required = true, example = "1")
            @PathVariable Long id) {

        logger.debug("Vérification si la commande {} peut être modifiée", id);

        boolean canModify = orderService.canBeModified(id);
        Map<String, Boolean> response = Map.of("canModify", canModify);

        return ResponseEntity.ok(response);
    }

    /**
     * Vérifie si une commande peut être annulée
     */
    @Operation(summary = "Vérifier si une commande peut être annulée")
    @GetMapping("/{id}/can-cancel")
    public ResponseEntity<Map<String, Boolean>> canCancelOrder(
            @Parameter(description = "Identifiant de la commande", required = true, example = "1")
            @PathVariable Long id) {

        logger.debug("Vérification si la commande {} peut être annulée", id);

        boolean canCancel = orderService.canBeCancelled(id);
        Map<String, Boolean> response = Map.of("canCancel", canCancel);

        return ResponseEntity.ok(response);
    }
}
