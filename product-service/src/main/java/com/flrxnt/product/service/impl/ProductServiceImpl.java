package com.flrxnt.product.service.impl;

import com.flrxnt.product.dto.ProductCreateDTO;
import com.flrxnt.product.dto.ProductDTO;
import com.flrxnt.product.dto.ProductUpdateDTO;
import com.flrxnt.product.entity.Product;
import com.flrxnt.product.exception.InsufficientStockException;
import com.flrxnt.product.exception.ProductNotFoundException;
import com.flrxnt.product.mapper.ProductMapper;
import com.flrxnt.product.repository.ProductRepository;
import com.flrxnt.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        logger.debug("Récupération de tous les produits avec pagination: page {}, taille {}",
                    pageable.getPageNumber(), pageable.getPageSize());

        Page<Product> products = productRepository.findAll(pageable);
        return products.map(productMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        logger.debug("Récupération de tous les produits sans pagination");

        List<Product> products = productRepository.findAll();
        return productMapper.toDTOList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        logger.debug("Récupération du produit avec l'ID: {}", id);

        if (id == null) {
            throw new IllegalArgumentException("L'ID du produit ne peut pas être null");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return productMapper.toDTO(product);
    }

    @Override
    public ProductDTO createProduct(ProductCreateDTO createDTO) {
        logger.debug("Création d'un nouveau produit: {}", createDTO.getNom());

        if (createDTO == null) {
            throw new IllegalArgumentException("Les données du produit ne peuvent pas être null");
        }

        // Vérifier si un produit avec le même nom existe déjà
        if (existsByName(createDTO.getNom())) {
            throw new IllegalArgumentException("Un produit avec ce nom existe déjà: " + createDTO.getNom());
        }

        Product product = productMapper.toEntity(createDTO);
        Product savedProduct = productRepository.save(product);

        logger.info("Produit créé avec succès: ID {}, Nom {}", savedProduct.getId(), savedProduct.getNom());
        return productMapper.toDTO(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductUpdateDTO updateDTO) {
        logger.debug("Mise à jour du produit avec l'ID: {}", id);

        if (id == null) {
            throw new IllegalArgumentException("L'ID du produit ne peut pas être null");
        }

        if (updateDTO == null || !updateDTO.hasUpdates()) {
            throw new IllegalArgumentException("Aucune donnée de mise à jour fournie");
        }

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // Vérifier l'unicité du nom si il est modifié
        if (updateDTO.getNom() != null && !updateDTO.getNom().equals(existingProduct.getNom())) {
            if (existsByNameExcludingId(updateDTO.getNom(), id)) {
                throw new IllegalArgumentException("Un produit avec ce nom existe déjà: " + updateDTO.getNom());
            }
        }

        productMapper.updateEntity(existingProduct, updateDTO);
        Product updatedProduct = productRepository.save(existingProduct);

        logger.info("Produit mis à jour avec succès: ID {}", id);
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        logger.debug("Suppression du produit avec l'ID: {}", id);

        if (id == null) {
            throw new IllegalArgumentException("L'ID du produit ne peut pas être null");
        }

        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }

        productRepository.deleteById(id);
        logger.info("Produit supprimé avec succès: ID {}", id);
    }

    @Override
    public ProductDTO updateStock(Long id, Integer nouvelleQuantite) {
        logger.debug("Mise à jour du stock du produit ID {} vers {}", id, nouvelleQuantite);

        if (id == null) {
            throw new IllegalArgumentException("L'ID du produit ne peut pas être null");
        }

        if (nouvelleQuantite == null || nouvelleQuantite < 0) {
            throw new IllegalArgumentException("La quantité doit être positive ou nulle");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setQuantiteStock(nouvelleQuantite);
        Product updatedProduct = productRepository.save(product);

        logger.info("Stock mis à jour pour le produit ID {}: nouvelle quantité {}", id, nouvelleQuantite);
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    public ProductDTO decrementStock(Long id, Integer quantite) {
        logger.debug("Décrémentation du stock du produit ID {} de {}", id, quantite);

        if (id == null) {
            throw new IllegalArgumentException("L'ID du produit ne peut pas être null");
        }

        if (quantite == null || quantite <= 0) {
            throw new IllegalArgumentException("La quantité à décrémenter doit être positive");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (product.getQuantiteStock() < quantite) {
            throw new InsufficientStockException(id, quantite, product.getQuantiteStock());
        }

        product.decrementStock(quantite);
        Product updatedProduct = productRepository.save(product);

        logger.info("Stock décrémenté pour le produit ID {}: quantité {}, nouveau stock {}",
                   id, quantite, updatedProduct.getQuantiteStock());
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    public ProductDTO incrementStock(Long id, Integer quantite) {
        logger.debug("Incrémentation du stock du produit ID {} de {}", id, quantite);

        if (id == null) {
            throw new IllegalArgumentException("L'ID du produit ne peut pas être null");
        }

        if (quantite == null || quantite <= 0) {
            throw new IllegalArgumentException("La quantité à incrémenter doit être positive");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.incrementStock(quantite);
        Product updatedProduct = productRepository.save(product);

        logger.info("Stock incrémenté pour le produit ID {}: quantité {}, nouveau stock {}",
                   id, quantite, updatedProduct.getQuantiteStock());
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProductsByName(String nom) {
        logger.debug("Recherche de produits par nom: {}", nom);

        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de recherche ne peut pas être vide");
        }

        List<Product> products = productRepository.findByNomContainingIgnoreCase(nom.trim());
        return productMapper.toDTOList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProductsByName(String nom, Pageable pageable) {
        logger.debug("Recherche de produits par nom avec pagination: {}", nom);

        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de recherche ne peut pas être vide");
        }

        Page<Product> products = productRepository.findByNomContainingIgnoreCase(nom.trim(), pageable);
        return products.map(productMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategory(String categorie) {
        logger.debug("Récupération des produits par catégorie: {}", categorie);

        if (categorie == null || categorie.trim().isEmpty()) {
            throw new IllegalArgumentException("La catégorie ne peut pas être vide");
        }

        List<Product> products = productRepository.findByCategorie(categorie.trim());
        return productMapper.toDTOList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsByCategory(String categorie, Pageable pageable) {
        logger.debug("Récupération des produits par catégorie avec pagination: {}", categorie);

        if (categorie == null || categorie.trim().isEmpty()) {
            throw new IllegalArgumentException("La catégorie ne peut pas être vide");
        }

        Page<Product> products = productRepository.findByCategorie(categorie.trim(), pageable);
        return products.map(productMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByPriceRange(BigDecimal prixMin, BigDecimal prixMax) {
        logger.debug("Récupération des produits dans la fourchette de prix: {} - {}", prixMin, prixMax);

        if (prixMin == null || prixMax == null) {
            throw new IllegalArgumentException("Les prix minimum et maximum ne peuvent pas être null");
        }

        if (prixMin.compareTo(prixMax) > 0) {
            throw new IllegalArgumentException("Le prix minimum ne peut pas être supérieur au prix maximum");
        }

        List<Product> products = productRepository.findByPrixBetween(prixMin, prixMax);
        return productMapper.toDTOList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getLowStockProducts(Integer seuil) {
        logger.debug("Récupération des produits avec stock faible (seuil: {})", seuil);

        if (seuil == null || seuil < 0) {
            throw new IllegalArgumentException("Le seuil doit être positif ou nul");
        }

        List<Product> products = productRepository.findByQuantiteStockLessThanEqual(seuil);
        return productMapper.toDTOList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsWithStockGreaterThan(Integer quantite) {
        logger.debug("Récupération des produits avec stock supérieur à: {}", quantite);

        if (quantite == null || quantite < 0) {
            throw new IllegalArgumentException("La quantité doit être positive ou nulle");
        }

        List<Product> products = productRepository.findByQuantiteStockGreaterThan(quantite);
        return productMapper.toDTOList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProducts(String nom, String categorie, BigDecimal prixMin,
                                          BigDecimal prixMax, Integer stockMin, Pageable pageable) {
        logger.debug("Recherche multicritères - nom: {}, catégorie: {}, prix: {}-{}, stock min: {}",
                    nom, categorie, prixMin, prixMax, stockMin);

        // Validation des prix
        if (prixMin != null && prixMax != null && prixMin.compareTo(prixMax) > 0) {
            throw new IllegalArgumentException("Le prix minimum ne peut pas être supérieur au prix maximum");
        }

        if (stockMin != null && stockMin < 0) {
            throw new IllegalArgumentException("Le stock minimum ne peut pas être négatif");
        }

        Page<Product> products = productRepository.findProductsByCriteria(
                nom != null ? nom.trim() : null,
                categorie != null ? categorie.trim() : null,
                prixMin, prixMax, stockMin, pageable);

        return products.map(productMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        return productRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return false;
        }
        return productRepository.existsByNom(nom.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameExcludingId(String nom, Long excludeId) {
        if (nom == null || nom.trim().isEmpty() || excludeId == null) {
            return false;
        }
        return productRepository.existsByNomAndIdNot(nom.trim(), excludeId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasEnoughStock(Long id, Integer quantiteRequise) {
        if (id == null || quantiteRequise == null || quantiteRequise <= 0) {
            return false;
        }

        return productRepository.hasEnoughStock(id, quantiteRequise).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        logger.debug("Récupération de toutes les catégories");
        return productRepository.findDistinctCategories();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getProductCountByCategory() {
        logger.debug("Récupération du nombre de produits par catégorie");
        return productRepository.countProductsByCategory();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getTopSellingProducts(int limit) {
        logger.debug("Récupération des {} produits les plus vendus", limit);

        if (limit <= 0) {
            throw new IllegalArgumentException("La limite doit être positive");
        }

        Pageable pageable = PageRequest.of(0, limit);
        List<Product> products = productRepository.findTopSellingProducts(pageable);
        return productMapper.toDTOList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsWithLowestStock(int limit) {
        logger.debug("Récupération des {} produits avec le stock le plus faible", limit);

        if (limit <= 0) {
            throw new IllegalArgumentException("La limite doit être positive");
        }

        Pageable pageable = PageRequest.of(0, limit);
        List<Product> products = productRepository.findLowStockProducts(pageable);
        return productMapper.toDTOList(products);
    }
}
