package com.flrxnt.order.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO pour représenter un client (utilisé pour la communication avec le service client)
 */
@Schema(description = "Représentation d'un client")
public class ClientDTO {

    @Schema(description = "Identifiant unique du client", example = "1")
    private Long id;

    @Schema(description = "Nom du client", example = "Dupont")
    private String nom;

    @Schema(description = "Prénom du client", example = "Jean")
    private String prenom;

    @Schema(description = "Adresse email du client", example = "jean.dupont@email.com")
    private String email;

    @Schema(description = "Numéro de téléphone du client", example = "+33123456789")
    private String telephone;

    @Schema(description = "Adresse du client", example = "123 Rue de la Paix, 75001 Paris")
    private String adresse;

    @Schema(description = "Date de création du compte client")
    @JsonProperty("date_creation")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateCreation;

    @Schema(description = "Statut du client", example = "ACTIVE")
    private String statut;

    // Constructeurs
    public ClientDTO() {
    }

    public ClientDTO(Long id, String nom, String prenom, String email, String telephone, String adresse) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
    }

    // Méthodes utilitaires
    public String getNomComplet() {
        return (prenom != null ? prenom + " " : "") + (nom != null ? nom : "");
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "ClientDTO{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}
