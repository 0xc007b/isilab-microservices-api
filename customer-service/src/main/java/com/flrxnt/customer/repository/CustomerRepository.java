package com.flrxnt.customer.repository;

import com.flrxnt.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Recherche un client par son email
     * @param email l'email du client
     * @return Optional contenant le client s'il existe
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Vérifie si un email existe déjà
     * @param email l'email à vérifier
     * @return true si l'email existe
     */
    boolean existsByEmail(String email);

    /**
     * Vérifie si un email existe déjà pour un autre client
     * @param email l'email à vérifier
     * @param id l'id du client à exclure
     * @return true si l'email existe pour un autre client
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Recherche des clients par nom (insensible à la casse)
     * @param nom le nom ou partie du nom
     * @return liste des clients correspondants
     */
    @Query("SELECT c FROM Customer c WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%'))")
    List<Customer> findByNomContainingIgnoreCase(@Param("nom") String nom);

    /**
     * Recherche des clients par nom avec pagination
     * @param nom le nom ou partie du nom
     * @param pageable paramètres de pagination
     * @return page des clients correspondants
     */
    @Query("SELECT c FROM Customer c WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%'))")
    Page<Customer> findByNomContainingIgnoreCase(@Param("nom") String nom, Pageable pageable);

    /**
     * Recherche des clients par ville dans l'adresse
     * @param ville la ville recherchée
     * @return liste des clients de cette ville
     */
    @Query("SELECT c FROM Customer c WHERE LOWER(c.adresse) LIKE LOWER(CONCAT('%', :ville, '%'))")
    List<Customer> findByAdresseContainingIgnoreCase(@Param("ville") String ville);

    /**
     * Compte le nombre total de clients
     * @return nombre total de clients
     */
    @Query("SELECT COUNT(c) FROM Customer c")
    Long countTotalCustomers();

    /**
     * Recherche des clients par critères multiples
     * @param nom nom du client (peut être null)
     * @param email email du client (peut être null)
     * @param ville ville dans l'adresse (peut être null)
     * @param pageable paramètres de pagination
     * @return page des clients correspondants
     */
    @Query("SELECT c FROM Customer c WHERE " +
           "(:nom IS NULL OR LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) AND " +
           "(:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:ville IS NULL OR LOWER(c.adresse) LIKE LOWER(CONCAT('%', :ville, '%')))")
    Page<Customer> findByMultipleCriteria(@Param("nom") String nom,
                                        @Param("email") String email,
                                        @Param("ville") String ville,
                                        Pageable pageable);
}
