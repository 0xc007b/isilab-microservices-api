package com.flrxnt.product.mapper;

import com.flrxnt.product.dto.ProductCreateDTO;
import com.flrxnt.product.dto.ProductDTO;
import com.flrxnt.product.dto.ProductUpdateDTO;
import com.flrxnt.product.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    /**
     * Convertit une entité Product en ProductDTO
     */
    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        return new ProductDTO(
                product.getId(),
                product.getNom(),
                product.getDescription(),
                product.getPrix(),
                product.getQuantiteStock(),
                product.getCategorie(),
                product.getDateCreation(),
                product.getDateModification()
        );
    }

    /**
     * Convertit un ProductCreateDTO en entité Product
     */
    public Product toEntity(ProductCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }

        Product product = new Product();
        product.setNom(createDTO.getNom());
        product.setDescription(createDTO.getDescription());
        product.setPrix(createDTO.getPrix());
        product.setQuantiteStock(createDTO.getQuantiteStock());
        product.setCategorie(createDTO.getCategorie());

        return product;
    }

    /**
     * Met à jour une entité Product avec les données d'un ProductUpdateDTO
     * Ne met à jour que les champs non nuls du DTO
     */
    public void updateEntity(Product product, ProductUpdateDTO updateDTO) {
        if (product == null || updateDTO == null) {
            return;
        }

        if (updateDTO.getNom() != null) {
            product.setNom(updateDTO.getNom());
        }

        if (updateDTO.getDescription() != null) {
            product.setDescription(updateDTO.getDescription());
        }

        if (updateDTO.getPrix() != null) {
            product.setPrix(updateDTO.getPrix());
        }

        if (updateDTO.getQuantiteStock() != null) {
            product.setQuantiteStock(updateDTO.getQuantiteStock());
        }

        if (updateDTO.getCategorie() != null) {
            product.setCategorie(updateDTO.getCategorie());
        }
    }

    /**
     * Convertit une liste d'entités Product en liste de ProductDTO
     */
    public List<ProductDTO> toDTOList(List<Product> products) {
        if (products == null) {
            return null;
        }

        return products.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une liste de ProductCreateDTO en liste d'entités Product
     */
    public List<Product> toEntityList(List<ProductCreateDTO> createDTOs) {
        if (createDTOs == null) {
            return null;
        }

        return createDTOs.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
