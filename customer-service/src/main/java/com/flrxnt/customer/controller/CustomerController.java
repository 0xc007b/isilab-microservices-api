package com.flrxnt.customer.controller;

import com.flrxnt.customer.dto.CustomerCreateDTO;
import com.flrxnt.customer.dto.CustomerDTO;
import com.flrxnt.customer.dto.CustomerUpdateDTO;
import com.flrxnt.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer Management", description = "API pour la gestion des clients")
@CrossOrigin(origins = "*")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Créer un nouveau client", description = "Crée un nouveau client avec les informations fournies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Email déjà existant")
    })
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(
            @Valid @RequestBody CustomerCreateDTO customerCreateDTO) {

        logger.info("Requête de création de client reçue pour email: {}", customerCreateDTO.getEmail());

        CustomerDTO createdCustomer = customerService.createCustomer(customerCreateDTO);

        logger.info("Client créé avec succès. ID: {}", createdCustomer.getId());

        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtenir un client par ID", description = "Récupère les détails d'un client spécifique par son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client trouvé"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(
            @Parameter(description = "ID du client", required = true) @PathVariable Long id) {

        logger.debug("Requête de récupération du client avec ID: {}", id);

        CustomerDTO customer = customerService.getCustomerById(id);

        return ResponseEntity.ok(customer);
    }

    @Operation(summary = "Obtenir un client par email", description = "Récupère les détails d'un client par son adresse email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client trouvé"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<CustomerDTO> getCustomerByEmail(
            @Parameter(description = "Email du client", required = true) @PathVariable String email) {

        logger.debug("Requête de récupération du client avec email: {}", email);

        CustomerDTO customer = customerService.getCustomerByEmail(email);

        return ResponseEntity.ok(customer);
    }

    @Operation(summary = "Mettre à jour un client", description = "Met à jour les informations d'un client existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé"),
            @ApiResponse(responseCode = "409", description = "Email déjà existant")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @Parameter(description = "ID du client", required = true) @PathVariable Long id,
            @Valid @RequestBody CustomerUpdateDTO customerUpdateDTO) {

        logger.info("Requête de mise à jour du client avec ID: {}", id);

        CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerUpdateDTO);

        logger.info("Client mis à jour avec succès. ID: {}", updatedCustomer.getId());

        return ResponseEntity.ok(updatedCustomer);
    }

    @Operation(summary = "Supprimer un client", description = "Supprime un client par son identifiant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Client supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "ID du client", required = true) @PathVariable Long id) {

        logger.info("Requête de suppression du client avec ID: {}", id);

        customerService.deleteCustomer(id);

        logger.info("Client supprimé avec succès. ID: {}", id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtenir tous les clients", description = "Récupère la liste de tous les clients avec pagination optionnelle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des clients récupérée avec succès")
    })
    @GetMapping
    public ResponseEntity<?> getAllCustomers(
            @Parameter(description = "Numéro de page (commence à 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction du tri (asc ou desc)") @RequestParam(defaultValue = "asc") String sortDir,
            @Parameter(description = "Utiliser la pagination") @RequestParam(defaultValue = "true") boolean paginated) {

        logger.debug("Requête de récupération de tous les clients. Pagination: {}", paginated);

        if (!paginated) {
            List<CustomerDTO> customers = customerService.getAllCustomers();
            return ResponseEntity.ok(customers);
        }

        // Validation des paramètres de pagination
        page = Math.max(0, page);
        size = Math.min(Math.max(1, size), 100); // Limiter la taille à 100 éléments max

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CustomerDTO> customersPage = customerService.getAllCustomers(pageable);

        return ResponseEntity.ok(customersPage);
    }

    @Operation(summary = "Rechercher des clients par nom", description = "Recherche des clients par nom avec pagination optionnelle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Résultats de recherche récupérés avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètre de recherche invalide")
    })
    @GetMapping("/search/name")
    public ResponseEntity<?> searchCustomersByName(
            @Parameter(description = "Nom à rechercher", required = true) @RequestParam String name,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Utiliser la pagination") @RequestParam(defaultValue = "true") boolean paginated) {

        logger.debug("Requête de recherche de clients par nom: {}", name);

        if (!paginated) {
            List<CustomerDTO> customers = customerService.findCustomersByName(name);
            return ResponseEntity.ok(customers);
        }

        page = Math.max(0, page);
        size = Math.min(Math.max(1, size), 100);

        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerDTO> customersPage = customerService.findCustomersByName(name, pageable);

        return ResponseEntity.ok(customersPage);
    }

    @Operation(summary = "Rechercher des clients par ville", description = "Recherche des clients par ville dans leur adresse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Résultats de recherche récupérés avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètre de recherche invalide")
    })
    @GetMapping("/search/city")
    public ResponseEntity<List<CustomerDTO>> searchCustomersByCity(
            @Parameter(description = "Ville à rechercher", required = true) @RequestParam String city) {

        logger.debug("Requête de recherche de clients par ville: {}", city);

        List<CustomerDTO> customers = customerService.findCustomersByCity(city);

        return ResponseEntity.ok(customers);
    }

    @Operation(summary = "Recherche multicritères", description = "Recherche des clients selon plusieurs critères")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Résultats de recherche récupérés avec succès")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<CustomerDTO>> searchCustomers(
            @Parameter(description = "Nom à rechercher") @RequestParam(required = false) String name,
            @Parameter(description = "Email à rechercher") @RequestParam(required = false) String email,
            @Parameter(description = "Ville à rechercher") @RequestParam(required = false) String city,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "asc") String sortDir) {

        logger.debug("Requête de recherche multicritères - Nom: {}, Email: {}, Ville: {}", name, email, city);

        page = Math.max(0, page);
        size = Math.min(Math.max(1, size), 100);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CustomerDTO> customersPage = customerService.searchCustomers(name, email, city, pageable);

        return ResponseEntity.ok(customersPage);
    }

    @Operation(summary = "Vérifier l'existence d'un client", description = "Vérifie si un client existe par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Résultat de vérification")
    })
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsCustomer(
            @Parameter(description = "ID du client", required = true) @PathVariable Long id) {

        logger.debug("Vérification d'existence du client avec ID: {}", id);

        boolean exists = customerService.existsById(id);

        return ResponseEntity.ok(exists);
    }

    @Operation(summary = "Vérifier l'existence d'un email", description = "Vérifie si un email est déjà utilisé")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Résultat de vérification")
    })
    @GetMapping("/email/{email}/exists")
    public ResponseEntity<Boolean> existsEmail(
            @Parameter(description = "Email à vérifier", required = true) @PathVariable String email) {

        logger.debug("Vérification d'existence de l'email: {}", email);

        boolean exists = customerService.existsByEmail(email);

        return ResponseEntity.ok(exists);
    }

    @Operation(summary = "Compter les clients", description = "Retourne le nombre total de clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nombre de clients")
    })
    @GetMapping("/count")
    public ResponseEntity<Long> countCustomers() {

        logger.debug("Requête de comptage des clients");

        Long count = customerService.countCustomers();

        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Changer le statut d'un client", description = "Active ou désactive un client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<CustomerDTO> toggleCustomerStatus(
            @Parameter(description = "ID du client", required = true) @PathVariable Long id,
            @Parameter(description = "Nouveau statut") @RequestParam boolean active) {

        logger.info("Requête de changement de statut pour le client ID: {} vers {}", id, active ? "actif" : "inactif");

        CustomerDTO customer = customerService.toggleCustomerStatus(id, active);

        return ResponseEntity.ok(customer);
    }
}
