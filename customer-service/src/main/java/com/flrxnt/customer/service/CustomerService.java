package com.flrxnt.customer.service;

import com.flrxnt.customer.dto.CustomerCreateDTO;
import com.flrxnt.customer.dto.CustomerDTO;
import com.flrxnt.customer.dto.CustomerUpdateDTO;
import com.flrxnt.customer.exception.CustomerAlreadyExistsException;
import com.flrxnt.customer.exception.CustomerNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {

    /**
     * Crée un nouveau client
     * @param customerCreateDTO les données du client à créer
     * @return le client créé
     * @throws CustomerAlreadyExistsException si l'email existe déjà
     */
    CustomerDTO createCustomer(CustomerCreateDTO customerCreateDTO);

    /**
     * Récupère un client par son ID
     * @param id l'ID du client
     * @return le client trouvé
     * @throws CustomerNotFoundException si le client n'existe pas
     */
    CustomerDTO getCustomerById(Long id);

    /**
     * Récupère un client par son email
     * @param email l'email du client
     * @return le client trouvé
     * @throws CustomerNotFoundException si le client n'existe pas
     */
    CustomerDTO getCustomerByEmail(String email);

    /**
     * Met à jour un client existant
     * @param id l'ID du client à mettre à jour
     * @param customerUpdateDTO les nouvelles données
     * @return le client mis à jour
     * @throws CustomerNotFoundException si le client n'existe pas
     * @throws CustomerAlreadyExistsException si le nouvel email existe déjà
     */
    CustomerDTO updateCustomer(Long id, CustomerUpdateDTO customerUpdateDTO);

    /**
     * Supprime un client par son ID
     * @param id l'ID du client à supprimer
     * @throws CustomerNotFoundException si le client n'existe pas
     */
    void deleteCustomer(Long id);

    /**
     * Récupère tous les clients avec pagination
     * @param pageable paramètres de pagination
     * @return page des clients
     */
    Page<CustomerDTO> getAllCustomers(Pageable pageable);

    /**
     * Récupère tous les clients sans pagination
     * @return liste de tous les clients
     */
    List<CustomerDTO> getAllCustomers();

    /**
     * Recherche des clients par nom
     * @param nom le nom ou partie du nom à rechercher
     * @return liste des clients trouvés
     */
    List<CustomerDTO> findCustomersByName(String nom);

    /**
     * Recherche des clients par nom avec pagination
     * @param nom le nom ou partie du nom à rechercher
     * @param pageable paramètres de pagination
     * @return page des clients trouvés
     */
    Page<CustomerDTO> findCustomersByName(String nom, Pageable pageable);

    /**
     * Recherche des clients par ville
     * @param ville la ville à rechercher dans l'adresse
     * @return liste des clients trouvés
     */
    List<CustomerDTO> findCustomersByCity(String ville);

    /**
     * Recherche des clients selon plusieurs critères
     * @param nom nom du client (optionnel)
     * @param email email du client (optionnel)
     * @param ville ville du client (optionnel)
     * @param pageable paramètres de pagination
     * @return page des clients correspondants aux critères
     */
    Page<CustomerDTO> searchCustomers(String nom, String email, String ville, Pageable pageable);

    /**
     * Vérifie si un client existe par son ID
     * @param id l'ID du client
     * @return true si le client existe
     */
    boolean existsById(Long id);

    /**
     * Vérifie si un email existe déjà
     * @param email l'email à vérifier
     * @return true si l'email existe
     */
    boolean existsByEmail(String email);

    /**
     * Compte le nombre total de clients
     * @return nombre total de clients
     */
    Long countCustomers();

    /**
     * Active ou désactive un client (pour une suppression logique future)
     * @param id l'ID du client
     * @param active true pour activer, false pour désactiver
     * @return le client mis à jour
     * @throws CustomerNotFoundException si le client n'existe pas
     */
    CustomerDTO toggleCustomerStatus(Long id, boolean active);

    /**
     * Valide les données d'un client avant création/mise à jour
     * @param customerCreateDTO les données à valider
     * @throws IllegalArgumentException si les données ne sont pas valides
     */
    void validateCustomerData(CustomerCreateDTO customerCreateDTO);

    /**
     * Valide les données de mise à jour d'un client
     * @param id l'ID du client à mettre à jour
     * @param customerUpdateDTO les données à valider
     * @throws IllegalArgumentException si les données ne sont pas valides
     */
    void validateCustomerUpdateData(Long id, CustomerUpdateDTO customerUpdateDTO);
}
