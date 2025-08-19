package com.flrxnt.product.controller;

import com.flrxnt.product.dto.ProductCreateDTO;
import com.flrxnt.product.dto.ProductDTO;
import com.flrxnt.product.dto.ProductUpdateDTO;
import com.flrxnt.product.service.ProductService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Controller", description = "API pour la gestion des produits")
@CrossOrigin(origins = "*")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Récupérer tous les produits avec pagination",
               description = "Récupère la liste de tous les produits avec support de la pagination et du tri")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des produits récupérée avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @Parameter(description = "Numéro de la page (commence à 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction du tri (asc ou desc)")
            @RequestParam(defaultValue = "asc") String sortDir) {

        logger.debug("Récupération de tous les produits - page: {}, taille: {}, tri: {} {}",
                    page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductDTO> products = productService.getAllProducts(pageable);

        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Récupérer tous les produits sans pagination",
               description = "Récupère la liste complète de tous les produits")
    @ApiResponse(responseCode = "200", description = "Liste complète des produits")
    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAllProductsWithoutPagination() {
        logger.debug("Récupération de tous les produits sans pagination");

        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Récupérer un produit par ID",
               description = "Récupère un produit spécifique en utilisant son identifiant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produit trouvé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
        @ApiResponse(responseCode = "400", description = "ID invalide")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "ID du produit", required = true)
            @PathVariable Long id) {

        logger.debug("Récupération du produit avec ID: {}", id);

        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @Operation(summary = "Créer un nouveau produit",
               description = "Crée un nouveau produit avec les informations fournies")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Produit créé avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "409", description = "Produit avec ce nom existe déjà")
    })
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @Parameter(description = "Données du produit à créer", required = true)
            @Valid @RequestBody ProductCreateDTO createDTO) {

        logger.debug("Création d'un nouveau produit: {}", createDTO.getNom());

        ProductDTO createdProduct = productService.createProduct(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @Operation(summary = "Mettre à jour un produit",
               description = "Met à jour un produit existant avec les nouvelles informations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produit mis à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "ID du produit à mettre à jour", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données du produit", required = true)
            @Valid @RequestBody ProductUpdateDTO updateDTO) {

        logger.debug("Mise à jour du produit avec ID: {}", id);

        ProductDTO updatedProduct = productService.updateProduct(id, updateDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Supprimer un produit",
               description = "Supprime un produit en utilisant son identifiant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Produit supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID du produit à supprimer", required = true)
            @PathVariable Long id) {

        logger.debug("Suppression du produit avec ID: {}", id);

        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Mettre à jour le stock d'un produit",
               description = "Met à jour la quantité en stock d'un produit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock mis à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
        @ApiResponse(responseCode = "400", description = "Quantité invalide")
    })
    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductDTO> updateStock(
            @Parameter(description = "ID du produit", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nouvelle quantité en stock", required = true)
            @RequestBody Map<String, Integer> stockUpdate) {

        Integer nouvelleQuantite = stockUpdate.get("quantite");
        logger.debug("Mise à jour du stock du produit ID {} vers {}", id, nouvelleQuantite);

        ProductDTO updatedProduct = productService.updateStock(id, nouvelleQuantite);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Décrémenter le stock d'un produit",
               description = "Diminue la quantité en stock d'un produit (utilisé lors des commandes)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock décrémenté avec succès"),
        @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
        @ApiResponse(responseCode = "400", description = "Stock insuffisant ou quantité invalide")
    })
    @PutMapping("/{id}/stock/decrement")
    public ResponseEntity<ProductDTO> decrementStock(
            @Parameter(description = "ID du produit", required = true)
            @PathVariable Long id,
            @Parameter(description = "Quantité à décrémenter", required = true)
            @RequestBody Map<String, Integer> stockUpdate) {

        Integer quantite = stockUpdate.get("quantite");
        logger.debug("Décrémentation du stock du produit ID {} de {}", id, quantite);

        ProductDTO updatedProduct = productService.decrementStock(id, quantite);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Incrémenter le stock d'un produit",
               description = "Augmente la quantité en stock d'un produit (utilisé lors des réapprovisionnements)")
    @ApiResponse(responseCode = "200", description = "Stock incrémenté avec succès")
    @PutMapping("/{id}/stock/increment")
    public ResponseEntity<ProductDTO> incrementStock(
            @Parameter(description = "ID du produit", required = true)
            @PathVariable Long id,
            @Parameter(description = "Quantité à incrémenter", required = true)
            @RequestBody Map<String, Integer> stockUpdate) {

        Integer quantite = stockUpdate.get("quantite");
        logger.debug("Incrémentation du stock du produit ID {} de {}", id, quantite);

        ProductDTO updatedProduct = productService.incrementStock(id, quantite);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Rechercher des produits par nom",
               description = "Recherche des produits dont le nom contient le texte spécifié")
    @ApiResponse(responseCode = "200", description = "Résultats de la recherche")
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @Parameter(description = "Nom à rechercher")
            @RequestParam(required = false) String nom,
            @Parameter(description = "Catégorie à filtrer")
            @RequestParam(required = false) String categorie,
            @Parameter(description = "Prix minimum")
            @RequestParam(required = false) BigDecimal prixMin,
            @Parameter(description = "Prix maximum")
            @RequestParam(required = false) BigDecimal prixMax,
            @Parameter(description = "Stock minimum")
            @RequestParam(required = false) Integer stockMin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        logger.debug("Recherche multicritères - nom: {}, catégorie: {}, prix: {}-{}, stock min: {}",
                    nom, categorie, prixMin, prixMax, stockMin);

        Sort sort = sortDir.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductDTO> products = productService.searchProducts(
            nom, categorie, prixMin, prixMax, stockMin, pageable);

        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Récupérer les produits par catégorie",
               description = "Récupère tous les produits d'une catégorie spécifique")
    @ApiResponse(responseCode = "200", description = "Produits de la catégorie")
    @GetMapping("/category/{categorie}")
    public ResponseEntity<Page<ProductDTO>> getProductsByCategory(
            @Parameter(description = "Nom de la catégorie", required = true)
            @PathVariable String categorie,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        logger.debug("Récupération des produits de la catégorie: {}", categorie);

        Sort sort = sortDir.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductDTO> products = productService.getProductsByCategory(categorie, pageable);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Récupérer les produits avec stock faible",
               description = "Récupère les produits dont le stock est inférieur ou égal au seuil spécifié")
    @ApiResponse(responseCode = "200", description = "Produits avec stock faible")
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts(
            @Parameter(description = "Seuil de stock faible")
            @RequestParam(defaultValue = "10") Integer seuil) {

        logger.debug("Récupération des produits avec stock faible (seuil: {})", seuil);

        List<ProductDTO> products = productService.getLowStockProducts(seuil);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Récupérer toutes les catégories",
               description = "Récupère la liste de toutes les catégories disponibles")
    @ApiResponse(responseCode = "200", description = "Liste des catégories")
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        logger.debug("Récupération de toutes les catégories");

        List<String> categories = productService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Récupérer les statistiques des produits",
               description = "Récupère le nombre de produits par catégorie")
    @ApiResponse(responseCode = "200", description = "Statistiques des produits")
    @GetMapping("/stats/by-category")
    public ResponseEntity<List<Object[]>> getProductStats() {
        logger.debug("Récupération des statistiques des produits");

        List<Object[]> stats = productService.getProductCountByCategory();
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Vérifier l'existence d'un produit",
               description = "Vérifie si un produit existe avec l'ID spécifié")
    @ApiResponse(responseCode = "200", description = "Résultat de la vérification")
    @GetMapping("/{id}/exists")
    public ResponseEntity<Map<String, Boolean>> checkProductExists(
            @Parameter(description = "ID du produit", required = true)
            @PathVariable Long id) {

        logger.debug("Vérification de l'existence du produit ID: {}", id);

        boolean exists = productService.existsById(id);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @Operation(summary = "Vérifier le stock disponible",
               description = "Vérifie si un produit a suffisamment de stock pour la quantité demandée")
    @ApiResponse(responseCode = "200", description = "Résultat de la vérification du stock")
    @GetMapping("/{id}/stock/check")
    public ResponseEntity<Map<String, Boolean>> checkStock(
            @Parameter(description = "ID du produit", required = true)
            @PathVariable Long id,
            @Parameter(description = "Quantité requise", required = true)
            @RequestParam Integer quantite) {

        logger.debug("Vérification du stock du produit ID {} pour quantité {}", id, quantite);

        boolean hasEnoughStock = productService.hasEnoughStock(id, quantite);
        return ResponseEntity.ok(Map.of("hasEnoughStock", hasEnoughStock));
    }
}
