package com.flrxnt.order.service.impl;

import com.flrxnt.order.client.ClientServiceClient;
import com.flrxnt.order.client.ProductServiceClient;
import com.flrxnt.order.dto.*;
import com.flrxnt.order.entity.Order;
import com.flrxnt.order.entity.OrderItem;
import com.flrxnt.order.exception.ClientNotValidException;
import com.flrxnt.order.exception.OrderNotFoundException;
import com.flrxnt.order.exception.ProductNotValidException;
import com.flrxnt.order.mapper.OrderMapper;
import com.flrxnt.order.repository.OrderRepository;
import com.flrxnt.order.service.OrderService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des commandes
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ClientServiceClient clientServiceClient;
    private final ProductServiceClient productServiceClient;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                          OrderMapper orderMapper,
                          ClientServiceClient clientServiceClient,
                          ProductServiceClient productServiceClient) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.clientServiceClient = clientServiceClient;
        this.productServiceClient = productServiceClient;
    }

    @Override
    public OrderDTO createOrder(OrderCreateDTO orderCreateDTO) {
        logger.info("Création d'une nouvelle commande pour le client {}", orderCreateDTO.getClientId());

        // Validation du client
        ClientDTO client = validateClient(orderCreateDTO.getClientId());

        // Validation des produits et création des items
        List<OrderItem> orderItems = validateAndCreateOrderItems(orderCreateDTO.getItems());

        // Création de la commande
        Order order = new Order(orderCreateDTO.getClientId(), orderCreateDTO.getCommentaire());

        // Ajout des items à la commande
        orderItems.forEach(order::addItem);

        // Sauvegarde
        Order savedOrder = orderRepository.save(order);

        logger.info("Commande créée avec succès avec l'ID: {}", savedOrder.getId());

        // Conversion en DTO et enrichissement avec les données externes
        OrderDTO orderDTO = orderMapper.toDTO(savedOrder);
        enrichOrderWithExternalData(orderDTO, client);

        return orderDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
        logger.debug("Recherche de la commande avec l'ID: {}", id);

        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new OrderNotFoundException("Commande non trouvée avec l'ID: " + id));

        OrderDTO orderDTO = orderMapper.toDTO(order);
        enrichOrderWithExternalData(orderDTO, null);

        return orderDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> findAll(Pageable pageable) {
        logger.debug("Recherche de toutes les commandes avec pagination: {}", pageable);

        Page<Order> orders = orderRepository.findAllWithItems(pageable);
        return orders.map(order -> {
            OrderDTO orderDTO = orderMapper.toDTO(order);
            enrichOrderWithExternalData(orderDTO, null);
            return orderDTO;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> findByClientId(Long clientId) {
        logger.debug("Recherche des commandes du client: {}", clientId);

        List<Order> orders = orderRepository.findByClientIdWithItems(clientId);
        return orders.stream()
                .map(orderMapper::toDTO)
                .peek(orderDTO -> enrichOrderWithExternalData(orderDTO, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> findByClientId(Long clientId, Pageable pageable) {
        logger.debug("Recherche des commandes du client {} avec pagination: {}", clientId, pageable);

        Page<Order> orders = orderRepository.findByClientId(clientId, pageable);
        return orders.map(order -> {
            OrderDTO orderDTO = orderMapper.toDTO(order);
            enrichOrderWithExternalData(orderDTO, null);
            return orderDTO;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> findByStatut(Order.OrderStatus statut) {
        logger.debug("Recherche des commandes avec le statut: {}", statut);

        List<Order> orders = orderRepository.findByStatutOrderByDateCommandeDesc(statut);
        return orders.stream()
                .map(orderMapper::toDTO)
                .peek(orderDTO -> enrichOrderWithExternalData(orderDTO, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> findByStatut(Order.OrderStatus statut, Pageable pageable) {
        logger.debug("Recherche des commandes avec le statut {} et pagination: {}", statut, pageable);

        Page<Order> orders = orderRepository.findByStatut(statut, pageable);
        return orders.map(order -> {
            OrderDTO orderDTO = orderMapper.toDTO(order);
            enrichOrderWithExternalData(orderDTO, null);
            return orderDTO;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> findByDateRange(LocalDateTime dateDebut, LocalDateTime dateFin) {
        logger.debug("Recherche des commandes entre {} et {}", dateDebut, dateFin);

        List<Order> orders = orderRepository.findByDateCommandeBetweenOrderByDateCommandeDesc(dateDebut, dateFin);
        return orders.stream()
                .map(orderMapper::toDTO)
                .peek(orderDTO -> enrichOrderWithExternalData(orderDTO, null))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO updateStatus(Long id, Order.OrderStatus nouveauStatut) {
        logger.info("Mise à jour du statut de la commande {} vers {}", id, nouveauStatut);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Commande non trouvée avec l'ID: " + id));

        // Validation des transitions de statut
        validateStatusTransition(order.getStatut(), nouveauStatut);

        order.setStatut(nouveauStatut);
        Order savedOrder = orderRepository.save(order);

        logger.info("Statut de la commande {} mis à jour vers {}", id, nouveauStatut);

        OrderDTO orderDTO = orderMapper.toDTO(savedOrder);
        enrichOrderWithExternalData(orderDTO, null);

        return orderDTO;
    }

    @Override
    public OrderDTO updateComment(Long id, String commentaire) {
        logger.info("Mise à jour du commentaire de la commande {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Commande non trouvée avec l'ID: " + id));

        if (!order.canBeModified()) {
            throw new IllegalStateException("La commande ne peut plus être modifiée dans son état actuel: " + order.getStatut());
        }

        order.setCommentaire(commentaire);
        Order savedOrder = orderRepository.save(order);

        OrderDTO orderDTO = orderMapper.toDTO(savedOrder);
        enrichOrderWithExternalData(orderDTO, null);

        return orderDTO;
    }

    @Override
    public OrderDTO cancelOrder(Long id) {
        logger.info("Annulation de la commande {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Commande non trouvée avec l'ID: " + id));

        if (!order.canBeCancelled()) {
            throw new IllegalStateException("La commande ne peut plus être annulée dans son état actuel: " + order.getStatut());
        }

        order.setStatut(Order.OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);

        logger.info("Commande {} annulée avec succès", id);

        OrderDTO orderDTO = orderMapper.toDTO(savedOrder);
        enrichOrderWithExternalData(orderDTO, null);

        return orderDTO;
    }

    @Override
    public void deleteOrder(Long id) {
        logger.info("Suppression de la commande {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Commande non trouvée avec l'ID: " + id));

        if (!order.canBeModified()) {
            throw new IllegalStateException("La commande ne peut pas être supprimée dans son état actuel: " + order.getStatut());
        }

        orderRepository.delete(order);
        logger.info("Commande {} supprimée avec succès", id);
    }


    @Override

    @Transactional(readOnly = true)

    public Double calculateTotalAmountByClientId(Long clientId) {

        return orderRepository.calculateTotalAmountByClientId(clientId)
                .map(BigDecimal::doubleValue)
                .orElse(0.0d);

    }


    @Override
    @Transactional(readOnly = true)
    public long countByClientId(Long clientId) {
        return orderRepository.countByClientId(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveOrders(Long clientId) {
        List<Order.OrderStatus> activeStatuses = List.of(
                Order.OrderStatus.PENDING,
                Order.OrderStatus.CONFIRMED,
                Order.OrderStatus.PROCESSING,
                Order.OrderStatus.SHIPPED
        );
        return orderRepository.existsByClientIdAndStatutIn(clientId, activeStatuses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> findRecentOrders() {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<Order> orders = orderRepository.findRecentOrders(since);
        return orders.stream()
                .map(orderMapper::toDTO)
                .peek(orderDTO -> enrichOrderWithExternalData(orderDTO, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> findOrdersNeedingAttention() {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        List<Order.OrderStatus> statusesToCheck = List.of(
                Order.OrderStatus.PENDING,
                Order.OrderStatus.CONFIRMED
        );

        List<Order> orders = orderRepository.findOrdersNeedingAttention(threeDaysAgo, statusesToCheck);
        return orders.stream()
                .map(orderMapper::toDTO)
                .peek(orderDTO -> enrichOrderWithExternalData(orderDTO, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> findTopClientsByOrderCount(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return orderRepository.findTopClientsByOrderCount(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> findByProductId(Long productId) {
        List<Order> orders = orderRepository.findByProductId(productId);
        return orders.stream()
                .map(orderMapper::toDTO)
                .peek(orderDTO -> enrichOrderWithExternalData(orderDTO, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getOrderStatisticsByDay(LocalDateTime dateDebut, LocalDateTime dateFin) {
        return orderRepository.getOrderStatisticsByDay(dateDebut, dateFin);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canBeModified(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Commande non trouvée avec l'ID: " + orderId));
        return order.canBeModified();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canBeCancelled(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Commande non trouvée avec l'ID: " + orderId));
        return order.canBeCancelled();
    }

    // Méthodes privées utilitaires

    private ClientDTO validateClient(Long clientId) {
        try {
            ClientDTO client = clientServiceClient.findById(clientId);
            if (client == null) {
                throw new ClientNotValidException("Client non trouvé avec l'ID: " + clientId);
            }
            if (!"ACTIVE".equals(client.getStatut())) {
                throw new ClientNotValidException("Le client avec l'ID " + clientId + " n'est pas actif");
            }
            return client;
        } catch (FeignException.NotFound e) {
            throw new ClientNotValidException("Client non trouvé avec l'ID: " + clientId);
        } catch (FeignException e) {
            logger.error("Erreur lors de la validation du client {}: {}", clientId, e.getMessage());
            throw new ClientNotValidException("Impossible de valider le client avec l'ID: " + clientId);
        }
    }

    private List<OrderItem> validateAndCreateOrderItems(List<OrderItemCreateDTO> itemCreateDTOs) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemCreateDTO itemCreateDTO : itemCreateDTOs) {
            ProductDTO product = validateProduct(itemCreateDTO.getProductId(), itemCreateDTO.getQuantite());

            OrderItem orderItem = new OrderItem(
                    product.getId(),
                    product.getNom(),
                    itemCreateDTO.getQuantite(),
                    product.getPrix()
            );

            orderItems.add(orderItem);
        }

        return orderItems;
    }

    private ProductDTO validateProduct(Long productId, Integer requestedQuantity) {
        try {
            ProductDTO product = productServiceClient.findById(productId);
            if (product == null) {
                throw new ProductNotValidException("Produit non trouvé avec l'ID: " + productId);
            }

            if (!product.isAvailable()) {
                throw new ProductNotValidException("Le produit avec l'ID " + productId + " n'est pas disponible");
            }

            if (!product.hasStock(requestedQuantity)) {
                throw new ProductNotValidException(
                        String.format("Stock insuffisant pour le produit %d. Stock disponible: %d, demandé: %d",
                                productId, product.getQuantiteStock(), requestedQuantity));
            }

            return product;
        } catch (FeignException.NotFound e) {
            throw new ProductNotValidException("Produit non trouvé avec l'ID: " + productId);
        } catch (FeignException e) {
            logger.error("Erreur lors de la validation du produit {}: {}", productId, e.getMessage());
            throw new ProductNotValidException("Impossible de valider le produit avec l'ID: " + productId);
        }
    }

    private void validateStatusTransition(Order.OrderStatus currentStatus, Order.OrderStatus newStatus) {
        // Définition des transitions valides
        boolean isValidTransition = switch (currentStatus) {
            case PENDING -> newStatus == Order.OrderStatus.CONFIRMED ||
                          newStatus == Order.OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == Order.OrderStatus.PROCESSING ||
                             newStatus == Order.OrderStatus.CANCELLED;
            case PROCESSING -> newStatus == Order.OrderStatus.SHIPPED ||
                              newStatus == Order.OrderStatus.CANCELLED;
            case SHIPPED -> newStatus == Order.OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false; // États finaux
        };

        if (!isValidTransition) {
            throw new IllegalStateException(
                    String.format("Transition de statut invalide: de %s vers %s", currentStatus, newStatus));
        }
    }

    private void enrichOrderWithExternalData(OrderDTO orderDTO, ClientDTO existingClient) {
        // Enrichissement avec les données du client
        if (existingClient != null) {
            orderDTO.setClient(existingClient);
        } else {
            try {
                ClientDTO client = clientServiceClient.findById(orderDTO.getClientId());
                orderDTO.setClient(client);
            } catch (FeignException e) {
                logger.warn("Impossible de récupérer les données du client {}: {}",
                           orderDTO.getClientId(), e.getMessage());
            }
        }

        // Enrichissement avec les données des produits pour chaque item
        if (orderDTO.getItems() != null) {
            for (OrderItemDTO item : orderDTO.getItems()) {
                try {
                    ProductDTO product = productServiceClient.findById(item.getProductId());
                    item.setProduct(product);
                } catch (FeignException e) {
                    logger.warn("Impossible de récupérer les données du produit {}: {}",
                               item.getProductId(), e.getMessage());
                }
            }
        }
    }
}
