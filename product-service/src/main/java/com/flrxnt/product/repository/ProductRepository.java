package com.flrxnt.product.repository;

import com.flrxnt.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Trouve tous les produits par catégorie
     */
    List<Product> findByCategorie(String categorie);

    /**
     * Trouve tous les produits par catégorie avec pagination
     */
    Page<Product> findByCategorie(String categorie, Pageable pageable);

    /**
     * Trouve les produits dont le nom contient le texte spécifié (insensible à la casse)
     */
    List<Product> findByNomContainingIgnoreCase(String nom);

    /**
     * Trouve les produits dont le nom contient le texte spécifié avec pagination
     */
    Page<Product> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    /**
     * Trouve les produits dans une fourchette de prix
     */
    List<Product> findByPrixBetween(BigDecimal prixMin, BigDecimal prixMax);

    /**
     * Trouve les produits avec un stock supérieur à la quantité spécifiée
     */
    List<Product> findByQuantiteStockGreaterThan(Integer quantite);

    /**
     * Trouve les produits avec un stock inférieur ou égal à la quantité spécifiée (produits en rupture/faible stock)
     */
    List<Product> findByQuantiteStockLessThanEqual(Integer quantite);

    /**
     * Trouve les produits par catégorie et avec un stock supérieur à zéro
     */
    List<Product> findByCategorieAndQuantiteStockGreaterThan(String categorie, Integer quantite);

    /**
     * Vérifie si un produit existe avec le nom spécifié
     */
    boolean existsByNom(String nom);

    /**
     * Vérifie si un produit existe avec le nom spécifié en excluant un ID donné (utile pour les mises à jour)
     */
    boolean existsByNomAndIdNot(String nom, Long id);

    /**
     * Requête personnalisée pour rechercher des produits par plusieurs critères
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(:nom IS NULL OR LOWER(p.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
           "(:categorie IS NULL OR p.categorie = :categorie) AND " +
           "(:prixMin IS NULL OR p.prix >= :prixMin) AND " +
           "(:prixMax IS NULL OR p.prix <= :prixMax) AND " +
           "(:stockMin IS NULL OR p.quantiteStock >= :stockMin)")
    Page<Product> findProductsByCriteria(
            @Param("nom") String nom,
            @Param("categorie") String categorie,
            @Param("prixMin") BigDecimal prixMin,
            @Param("prixMax") BigDecimal prixMax,
            @Param("stockMin") Integer stockMin,
            Pageable pageable);

    /**
     * Met à jour le stock d'un produit
     */
    @Modifying
    @Query("UPDATE Product p SET p.quantiteStock = :nouvelleQuantite WHERE p.id = :id")
    int updateStock(@Param("id") Long id, @Param("nouvelleQuantite") Integer nouvelleQuantite);

    /**
     * Décrémente le stock d'un produit
     */
    @Modifying
    @Query("UPDATE Product p SET p.quantiteStock = p.quantiteStock - :quantite WHERE p.id = :id AND p.quantiteStock >= :quantite")
    int decrementStock(@Param("id") Long id, @Param("quantite") Integer quantite);

    /**
     * Incrémente le stock d'un produit
     */
    @Modifying
    @Query("UPDATE Product p SET p.quantiteStock = p.quantiteStock + :quantite WHERE p.id = :id")
    int incrementStock(@Param("id") Long id, @Param("quantite") Integer quantite);

    /**
     * Trouve les produits les plus vendus (nécessiterait une table de commandes pour être vraiment utile)
     * Pour l'instant, on trie par stock décroissant comme approximation
     */
    @Query("SELECT p FROM Product p ORDER BY p.quantiteStock DESC")
    List<Product> findTopSellingProducts(Pageable pageable);

    /**
     * Trouve les catégories distinctes
     */
    @Query("SELECT DISTINCT p.categorie FROM Product p ORDER BY p.categorie")
    List<String> findDistinctCategories();

    /**
     * Compte le nombre de produits par catégorie
     */
    @Query("SELECT p.categorie, COUNT(p) FROM Product p GROUP BY p.categorie")
    List<Object[]> countProductsByCategory();

    /**
     * Trouve les produits avec le stock le plus faible
     */
    @Query("SELECT p FROM Product p WHERE p.quantiteStock > 0 ORDER BY p.quantiteStock ASC")
    List<Product> findLowStockProducts(Pageable pageable);

    /**
     * Vérifie si un produit a suffisamment de stock
     */
    @Query("SELECT CASE WHEN p.quantiteStock >= :quantiteRequise THEN true ELSE false END FROM Product p WHERE p.id = :id")
    Optional<Boolean> hasEnoughStock(@Param("id") Long id, @Param("quantiteRequise") Integer quantiteRequise);
}
