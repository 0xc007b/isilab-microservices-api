package com.flrxnt.product.service;

import com.flrxnt.product.dto.ProductCreateDTO;
import com.flrxnt.product.dto.ProductDTO;
import com.flrxnt.product.dto.ProductUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    /**
     * Récupère tous les produits avec pagination
     */
    Page<ProductDTO> getAllProducts(Pageable pageable);

    /**
     * Récupère tous les produits sans pagination
     */
    List<ProductDTO> getAllProducts();

    /**
     * Récupère un produit par son ID
     */
    ProductDTO getProductById(Long id);

    /**
     * Crée un nouveau produit
     */
    ProductDTO createProduct(ProductCreateDTO createDTO);

    /**
     * Met à jour un produit existant
     */
    ProductDTO updateProduct(Long id, ProductUpdateDTO updateDTO);

    /**
     * Supprime un produit par son ID
     */
    void deleteProduct(Long id);

    /**
     * Met à jour le stock d'un produit
     */
    ProductDTO updateStock(Long id, Integer nouvelleQuantite);

    /**
     * Décrémente le stock d'un produit (utilisé lors des commandes)
     */
    ProductDTO decrementStock(Long id, Integer quantite);

    /**
     * Incrémente le stock d'un produit (utilisé lors des retours ou réapprovisionnements)
     */
    ProductDTO incrementStock(Long id, Integer quantite);

    /**
     * Recherche des produits par nom (recherche partielle, insensible à la casse)
     */
    List<ProductDTO> searchProductsByName(String nom);

    /**
     * Recherche des produits par nom avec pagination
     */
    Page<ProductDTO> searchProductsByName(String nom, Pageable pageable);

    /**
     * Récupère tous les produits d'une catégorie
     */
    List<ProductDTO> getProductsByCategory(String categorie);

    /**
     * Récupère tous les produits d'une catégorie avec pagination
     */
    Page<ProductDTO> getProductsByCategory(String categorie, Pageable pageable);

    /**
     * Recherche des produits dans une fourchette de prix
     */
    List<ProductDTO> getProductsByPriceRange(BigDecimal prixMin, BigDecimal prixMax);

    /**
     * Récupère les produits avec un stock faible (inférieur ou égal au seuil)
     */
    List<ProductDTO> getLowStockProducts(Integer seuil);

    /**
     * Récupère les produits avec un stock supérieur à la quantité spécifiée
     */
    List<ProductDTO> getProductsWithStockGreaterThan(Integer quantite);

    /**
     * Recherche multicritères avec pagination
     */
    Page<ProductDTO> searchProducts(String nom, String categorie, BigDecimal prixMin,
                                   BigDecimal prixMax, Integer stockMin, Pageable pageable);

    /**
     * Vérifie si un produit existe
     */
    boolean existsById(Long id);

    /**
     * Vérifie si un produit avec ce nom existe déjà
     */
    boolean existsByName(String nom);

    /**
     * Vérifie si un produit avec ce nom existe déjà (en excluant un ID donné)
     */
    boolean existsByNameExcludingId(String nom, Long excludeId);

    /**
     * Vérifie si un produit a suffisamment de stock
     */
    boolean hasEnoughStock(Long id, Integer quantiteRequise);

    /**
     * Récupère toutes les catégories disponibles
     */
    List<String> getAllCategories();

    /**
     * Récupère le nombre de produits par catégorie
     */
    List<Object[]> getProductCountByCategory();

    /**
     * Récupère les produits les plus vendus (basé sur le stock restant comme approximation)
     */
    List<ProductDTO> getTopSellingProducts(int limit);

    /**
     * Récupère les produits avec le stock le plus faible
     */
    List<ProductDTO> getProductsWithLowestStock(int limit);
}
