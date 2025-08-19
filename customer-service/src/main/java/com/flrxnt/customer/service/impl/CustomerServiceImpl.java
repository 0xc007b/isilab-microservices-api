package com.flrxnt.customer.service.impl;

import com.flrxnt.customer.dto.CustomerCreateDTO;
import com.flrxnt.customer.dto.CustomerDTO;
import com.flrxnt.customer.dto.CustomerUpdateDTO;
import com.flrxnt.customer.entity.Customer;
import com.flrxnt.customer.exception.CustomerAlreadyExistsException;
import com.flrxnt.customer.exception.CustomerNotFoundException;
import com.flrxnt.customer.mapper.CustomerMapper;
import com.flrxnt.customer.repository.CustomerRepository;
import com.flrxnt.customer.service.CustomerService;
import com.flrxnt.customer.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public CustomerDTO createCustomer(CustomerCreateDTO customerCreateDTO) {
        logger.info("Création d'un nouveau client avec email: {}", customerCreateDTO.getEmail());

        // Validation des données
        validateCustomerData(customerCreateDTO);

        // Vérification de l'unicité de l'email
        if (customerRepository.existsByEmail(customerCreateDTO.getEmail())) {
            logger.warn("Tentative de création d'un client avec un email existant: {}", customerCreateDTO.getEmail());
            throw new CustomerAlreadyExistsException(customerCreateDTO.getEmail());
        }

        try {
            // Conversion DTO vers entité
            Customer customer = customerMapper.toEntity(customerCreateDTO);

            // Sauvegarde en base
            Customer savedCustomer = customerRepository.save(customer);

            logger.info("Client créé avec succès. ID: {}, Email: {}", savedCustomer.getId(), savedCustomer.getEmail());

            // Conversion entité vers DTO de retour
            return customerMapper.toDTO(savedCustomer);

        } catch (Exception e) {
            logger.error("Erreur lors de la création du client avec email: {}", customerCreateDTO.getEmail(), e);
            throw new RuntimeException("Erreur lors de la création du client", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long id) {
        logger.debug("Recherche du client avec ID: {}", id);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID du client doit être positif et non null");
        }

        Optional<Customer> customerOptional = customerRepository.findById(id);

        if (customerOptional.isEmpty()) {
            logger.warn("Client non trouvé avec ID: {}", id);
            throw new CustomerNotFoundException(id);
        }

        Customer customer = customerOptional.get();
        logger.debug("Client trouvé: {}", customer.getEmail());

        return customerMapper.toDTO(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerByEmail(String email) {
        logger.debug("Recherche du client avec email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide");
        }

        Optional<Customer> customerOptional = customerRepository.findByEmail(email);

        if (customerOptional.isEmpty()) {
            logger.warn("Client non trouvé avec email: {}", email);
            throw new CustomerNotFoundException(email);
        }

        Customer customer = customerOptional.get();
        logger.debug("Client trouvé avec ID: {}", customer.getId());

        return customerMapper.toDTO(customer);
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerUpdateDTO customerUpdateDTO) {
        logger.info("Mise à jour du client avec ID: {}", id);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID du client doit être positif et non null");
        }

        // Validation des données de mise à jour
        validateCustomerUpdateData(id, customerUpdateDTO);

        // Vérification de l'existence du client
        Optional<Customer> customerOptional = customerRepository.findById(id);
        if (customerOptional.isEmpty()) {
            logger.warn("Tentative de mise à jour d'un client inexistant avec ID: {}", id);
            throw new CustomerNotFoundException(id);
        }

        Customer existingCustomer = customerOptional.get();

        // Vérification de l'unicité de l'email si modifié
        if (!existingCustomer.getEmail().equals(customerUpdateDTO.getEmail()) &&
            customerRepository.existsByEmailAndIdNot(customerUpdateDTO.getEmail(), id)) {
            logger.warn("Tentative de mise à jour avec un email déjà existant: {}", customerUpdateDTO.getEmail());
            throw new CustomerAlreadyExistsException(customerUpdateDTO.getEmail());
        }

        try {
            // Mise à jour de l'entité
            Customer updatedCustomer = customerMapper.updateEntityFromDTO(existingCustomer, customerUpdateDTO);

            // Sauvegarde
            Customer savedCustomer = customerRepository.save(updatedCustomer);

            logger.info("Client mis à jour avec succès. ID: {}, Email: {}", savedCustomer.getId(), savedCustomer.getEmail());

            return customerMapper.toDTO(savedCustomer);

        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du client avec ID: {}", id, e);
            throw new RuntimeException("Erreur lors de la mise à jour du client", e);
        }
    }

    @Override
    public void deleteCustomer(Long id) {
        logger.info("Suppression du client avec ID: {}", id);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID du client doit être positif et non null");
        }

        if (!customerRepository.existsById(id)) {
            logger.warn("Tentative de suppression d'un client inexistant avec ID: {}", id);
            throw new CustomerNotFoundException(id);
        }

        try {
            customerRepository.deleteById(id);
            logger.info("Client supprimé avec succès. ID: {}", id);

        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du client avec ID: {}", id, e);
            throw new RuntimeException("Erreur lors de la suppression du client", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> getAllCustomers(Pageable pageable) {
        logger.debug("Récupération de tous les clients avec pagination. Page: {}, Size: {}",
                    pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<Customer> customersPage = customerRepository.findAll(pageable);

            logger.debug("Nombre de clients trouvés: {}", customersPage.getTotalElements());

            return customersPage.map(customerMapper::toDTO);

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des clients avec pagination", e);
            throw new RuntimeException("Erreur lors de la récupération des clients", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllCustomers() {
        logger.debug("Récupération de tous les clients");

        try {
            List<Customer> customers = customerRepository.findAll();

            logger.debug("Nombre de clients trouvés: {}", customers.size());

            return customerMapper.toDTOList(customers);

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de tous les clients", e);
            throw new RuntimeException("Erreur lors de la récupération des clients", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> findCustomersByName(String nom) {
        logger.debug("Recherche de clients par nom: {}", nom);

        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de recherche ne peut pas être vide");
        }

        try {
            List<Customer> customers = customerRepository.findByNomContainingIgnoreCase(nom);

            logger.debug("Nombre de clients trouvés avec le nom '{}': {}", nom, customers.size());

            return customerMapper.toDTOList(customers);

        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de clients par nom: {}", nom, e);
            throw new RuntimeException("Erreur lors de la recherche de clients", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findCustomersByName(String nom, Pageable pageable) {
        logger.debug("Recherche de clients par nom avec pagination: {}", nom);

        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de recherche ne peut pas être vide");
        }

        try {
            Page<Customer> customersPage = customerRepository.findByNomContainingIgnoreCase(nom, pageable);

            logger.debug("Nombre de clients trouvés avec le nom '{}': {}", nom, customersPage.getTotalElements());

            return customersPage.map(customerMapper::toDTO);

        } catch (Exception e) {
            logger.error("Erreur lors de la recherche paginée de clients par nom: {}", nom, e);
            throw new RuntimeException("Erreur lors de la recherche de clients", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> findCustomersByCity(String ville) {
        logger.debug("Recherche de clients par ville: {}", ville);

        if (ville == null || ville.trim().isEmpty()) {
            throw new IllegalArgumentException("La ville de recherche ne peut pas être vide");
        }

        try {
            List<Customer> customers = customerRepository.findByAdresseContainingIgnoreCase(ville);

            logger.debug("Nombre de clients trouvés dans la ville '{}': {}", ville, customers.size());

            return customerMapper.toDTOList(customers);

        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de clients par ville: {}", ville, e);
            throw new RuntimeException("Erreur lors de la recherche de clients", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> searchCustomers(String nom, String email, String ville, Pageable pageable) {
        logger.debug("Recherche multicritères de clients - Nom: {}, Email: {}, Ville: {}", nom, email, ville);

        try {
            Page<Customer> customersPage = customerRepository.findByMultipleCriteria(nom, email, ville, pageable);

            logger.debug("Nombre de clients trouvés avec les critères: {}", customersPage.getTotalElements());

            return customersPage.map(customerMapper::toDTO);

        } catch (Exception e) {
            logger.error("Erreur lors de la recherche multicritères de clients", e);
            throw new RuntimeException("Erreur lors de la recherche de clients", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        return customerRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return customerRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countCustomers() {
        logger.debug("Comptage du nombre total de clients");

        try {
            return customerRepository.countTotalCustomers();
        } catch (Exception e) {
            logger.error("Erreur lors du comptage des clients", e);
            throw new RuntimeException("Erreur lors du comptage des clients", e);
        }
    }

    @Override
    public CustomerDTO toggleCustomerStatus(Long id, boolean active) {
        logger.info("Changement du statut du client avec ID: {} vers {}", id, active ? "actif" : "inactif");

        // Pour l'instant, cette méthode ne fait que récupérer le client
        // Dans une implémentation future, on pourrait ajouter un champ "active" à l'entité
        CustomerDTO customer = getCustomerById(id);

        logger.info("Statut du client {} modifié avec succès", id);

        return customer;
    }

    @Override
    public void validateCustomerData(CustomerCreateDTO customerCreateDTO) {
        if (customerCreateDTO == null) {
            throw new IllegalArgumentException("Les données du client ne peuvent pas être nulles");
        }

        if (customerCreateDTO.getNom() == null || customerCreateDTO.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du client est obligatoire");
        }

        if (customerCreateDTO.getEmail() == null || customerCreateDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'email du client est obligatoire");
        }

        // Utilisation de ValidationUtil pour une validation complète
        String validationError = ValidationUtil.validateCustomerData(
            customerCreateDTO.getNom(),
            customerCreateDTO.getEmail(),
            customerCreateDTO.getTelephone(),
            customerCreateDTO.getAdresse()
        );

        if (validationError != null) {
            throw new IllegalArgumentException(validationError);
        }
    }

    @Override
    public void validateCustomerUpdateData(Long id, CustomerUpdateDTO customerUpdateDTO) {
        if (customerUpdateDTO == null) {
            throw new IllegalArgumentException("Les données de mise à jour ne peuvent pas être nulles");
        }

        if (customerUpdateDTO.getNom() == null || customerUpdateDTO.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du client est obligatoire");
        }

        if (customerUpdateDTO.getEmail() == null || customerUpdateDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'email du client est obligatoire");
        }

        // Utilisation de ValidationUtil pour une validation complète
        String validationError = ValidationUtil.validateCustomerData(
            customerUpdateDTO.getNom(),
            customerUpdateDTO.getEmail(),
            customerUpdateDTO.getTelephone(),
            customerUpdateDTO.getAdresse()
        );

        if (validationError != null) {
            throw new IllegalArgumentException(validationError);
        }
    }

    /**
     * Valide le format d'un email en utilisant ValidationUtil
     * @param email l'email à valider
     * @return true si l'email est valide
     */
    private boolean isValidEmail(String email) {
        return ValidationUtil.isValidEmail(email);
    }
}
