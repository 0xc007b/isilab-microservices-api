package com.flrxnt.order.mapper;

import com.flrxnt.order.dto.OrderDTO;
import com.flrxnt.order.dto.OrderItemDTO;
import com.flrxnt.order.entity.Order;
import com.flrxnt.order.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour la conversion entre les entités Order/OrderItem et leurs DTOs
 */
@Component
public class OrderMapper {

    /**
     * Convertit une entité Order en OrderDTO
     * @param order l'entité Order à convertir
     * @return le DTO correspondant
     */
    public OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setClientId(order.getClientId());
        orderDTO.setDateCommande(order.getDateCommande());
        orderDTO.setDateModification(order.getDateModification());
        orderDTO.setMontantTotal(order.getMontantTotal());
        orderDTO.setStatut(order.getStatut());
        orderDTO.setCommentaire(order.getCommentaire());
        orderDTO.setVersion(order.getVersion());

        // Conversion des items
        if (order.getItems() != null) {
            List<OrderItemDTO> itemDTOs = order.getItems().stream()
                    .map(this::toItemDTO)
                    .collect(Collectors.toList());
            orderDTO.setItems(itemDTOs);
            orderDTO.setNombreItems(itemDTOs.size());
        } else {
            orderDTO.setNombreItems(0);
        }

        return orderDTO;
    }

    /**
     * Convertit une liste d'entités Order en liste de OrderDTO
     * @param orders la liste des entités à convertir
     * @return la liste des DTOs correspondants
     */
    public List<OrderDTO> toDTOList(List<Order> orders) {
        if (orders == null) {
            return null;
        }

        return orders.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une entité OrderItem en OrderItemDTO
     * @param orderItem l'entité OrderItem à convertir
     * @return le DTO correspondant
     */
    public OrderItemDTO toItemDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setId(orderItem.getId());
        itemDTO.setProductId(orderItem.getProductId());
        itemDTO.setProductName(orderItem.getProductName());
        itemDTO.setQuantite(orderItem.getQuantite());
        itemDTO.setPrixUnitaire(orderItem.getPrixUnitaire());
        itemDTO.setPrixTotal(orderItem.getPrixTotal());
        itemDTO.setDateCreation(orderItem.getDateCreation());
        itemDTO.setDateModification(orderItem.getDateModification());
        itemDTO.setVersion(orderItem.getVersion());

        return itemDTO;
    }

    /**
     * Convertit une liste d'entités OrderItem en liste de OrderItemDTO
     * @param orderItems la liste des entités à convertir
     * @return la liste des DTOs correspondants
     */
    public List<OrderItemDTO> toItemDTOList(List<OrderItem> orderItems) {
        if (orderItems == null) {
            return null;
        }

        return orderItems.stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());
    }

    /**
     * Met à jour une entité Order avec les données d'un DTO (pour les mises à jour partielles)
     * @param order l'entité à mettre à jour
     * @param orderDTO le DTO contenant les nouvelles données
     * @return l'entité mise à jour
     */
    public Order updateEntity(Order order, OrderDTO orderDTO) {
        if (order == null || orderDTO == null) {
            return order;
        }

        if (orderDTO.getStatut() != null) {
            order.setStatut(orderDTO.getStatut());
        }

        if (orderDTO.getCommentaire() != null) {
            order.setCommentaire(orderDTO.getCommentaire());
        }

        return order;
    }

    /**
     * Crée un OrderDTO minimal (sans les items) pour les listes
     * @param order l'entité Order
     * @return le DTO minimal
     */
    public OrderDTO toMinimalDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setClientId(order.getClientId());
        orderDTO.setDateCommande(order.getDateCommande());
        orderDTO.setDateModification(order.getDateModification());
        orderDTO.setMontantTotal(order.getMontantTotal());
        orderDTO.setStatut(order.getStatut());
        orderDTO.setCommentaire(order.getCommentaire());
        orderDTO.setVersion(order.getVersion());

        // Juste le nombre d'items sans les détails
        if (order.getItems() != null) {
            orderDTO.setNombreItems(order.getItems().size());
        } else {
            orderDTO.setNombreItems(0);
        }

        return orderDTO;
    }

    /**
     * Convertit une liste d'entités Order en liste de OrderDTO minimaux
     * @param orders la liste des entités à convertir
     * @return la liste des DTOs minimaux correspondants
     */
    public List<OrderDTO> toMinimalDTOList(List<Order> orders) {
        if (orders == null) {
            return null;
        }

        return orders.stream()
                .map(this::toMinimalDTO)
                .collect(Collectors.toList());
    }
}
