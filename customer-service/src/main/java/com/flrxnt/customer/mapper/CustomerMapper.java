package com.flrxnt.customer.mapper;

import com.flrxnt.customer.dto.CustomerCreateDTO;
import com.flrxnt.customer.dto.CustomerDTO;
import com.flrxnt.customer.dto.CustomerUpdateDTO;
import com.flrxnt.customer.entity.Customer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerMapper {

    /**
     * Convertit une entité Customer en CustomerDTO
     * @param customer l'entité à convertir
     * @return le DTO correspondant ou null si l'entité est null
     */
    public CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setNom(customer.getNom());
        dto.setEmail(customer.getEmail());
        dto.setTelephone(customer.getTelephone());
        dto.setAdresse(customer.getAdresse());
        dto.setDateCreation(customer.getDateCreation());
        dto.setDateModification(customer.getDateModification());

        return dto;
    }

    /**
     * Convertit un CustomerCreateDTO en entité Customer
     * @param createDTO le DTO de création
     * @return l'entité correspondante ou null si le DTO est null
     */
    public Customer toEntity(CustomerCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setNom(createDTO.getNom());
        customer.setEmail(createDTO.getEmail());
        customer.setTelephone(createDTO.getTelephone());
        customer.setAdresse(createDTO.getAdresse());

        return customer;
    }

    /**
     * Met à jour une entité Customer avec les données d'un CustomerUpdateDTO
     * @param customer l'entité à mettre à jour
     * @param updateDTO le DTO contenant les nouvelles données
     * @return l'entité mise à jour ou null si un des paramètres est null
     */
    public Customer updateEntityFromDTO(Customer customer, CustomerUpdateDTO updateDTO) {
        if (customer == null || updateDTO == null) {
            return null;
        }

        customer.setNom(updateDTO.getNom());
        customer.setEmail(updateDTO.getEmail());
        customer.setTelephone(updateDTO.getTelephone());
        customer.setAdresse(updateDTO.getAdresse());

        return customer;
    }

    /**
     * Convertit une liste d'entités Customer en liste de CustomerDTO
     * @param customers la liste d'entités
     * @return la liste de DTOs correspondante
     */
    public List<CustomerDTO> toDTOList(List<Customer> customers) {
        if (customers == null) {
            return null;
        }

        return customers.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une liste de CustomerCreateDTO en liste d'entités Customer
     * @param createDTOs la liste de DTOs de création
     * @return la liste d'entités correspondante
     */
    public List<Customer> toEntityList(List<CustomerCreateDTO> createDTOs) {
        if (createDTOs == null) {
            return null;
        }

        return createDTOs.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Convertit un CustomerDTO en entité Customer (pour les cas de mise à jour complète)
     * @param customerDTO le DTO à convertir
     * @return l'entité correspondante ou null si le DTO est null
     */
    public Customer toEntity(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setId(customerDTO.getId());
        customer.setNom(customerDTO.getNom());
        customer.setEmail(customerDTO.getEmail());
        customer.setTelephone(customerDTO.getTelephone());
        customer.setAdresse(customerDTO.getAdresse());
        customer.setDateCreation(customerDTO.getDateCreation());
        customer.setDateModification(customerDTO.getDateModification());

        return customer;
    }

    /**
     * Convertit un CustomerUpdateDTO en CustomerDTO
     * @param updateDTO le DTO de mise à jour
     * @return le DTO correspondant ou null si le DTO d'update est null
     */
    public CustomerDTO updateDTOToDTO(CustomerUpdateDTO updateDTO) {
        if (updateDTO == null) {
            return null;
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setNom(updateDTO.getNom());
        dto.setEmail(updateDTO.getEmail());
        dto.setTelephone(updateDTO.getTelephone());
        dto.setAdresse(updateDTO.getAdresse());

        return dto;
    }
}
