package com.flrxnt.customer.util;

import java.util.regex.Pattern;

/**
 * Utilitaire de validation pour le service Customer
 * Contient des méthodes statiques pour valider différents types de données
 */
public class ValidationUtil {

    // Pattern pour validation d'email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );

    // Pattern pour validation de numéro de téléphone français
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(?:(?:\\+33|0)[1-9](?:[0-9]{8}))$"
    );

    /**
     * Constructeur privé pour empêcher l'instanciation
     */
    private ValidationUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Valide le format d'un email
     * @param email l'email à valider
     * @return true si l'email est valide, false sinon
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Valide le format d'un numéro de téléphone français
     * @param phone le numéro de téléphone à valider
     * @return true si le numéro est valide, false sinon
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Le téléphone est optionnel
        }
        // Nettoyer le numéro (enlever espaces, points, tirets)
        String cleanPhone = phone.replaceAll("[\\s.-]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    /**
     * Valide qu'une chaîne n'est pas null ou vide
     * @param str la chaîne à valider
     * @return true si la chaîne est valide (non null et non vide après trim)
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Valide la longueur d'une chaîne
     * @param str la chaîne à valider
     * @param minLength longueur minimale (incluse)
     * @param maxLength longueur maximale (incluse)
     * @return true si la longueur est dans la plage spécifiée
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) {
            return false;
        }
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Valide qu'un ID est positif et non null
     * @param id l'ID à valider
     * @return true si l'ID est valide
     */
    public static boolean isValidId(Long id) {
        return id != null && id > 0;
    }

    /**
     * Sanitise une chaîne en supprimant les espaces en début/fin
     * et en retournant null si la chaîne est vide
     * @param str la chaîne à sanitiser
     * @return la chaîne sanitisée ou null
     */
    public static String sanitizeString(String str) {
        if (str == null) {
            return null;
        }
        String trimmed = str.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Valide un nom de client
     * @param nom le nom à valider
     * @return true si le nom est valide
     */
    public static boolean isValidCustomerName(String nom) {
        return isNotEmpty(nom) && isValidLength(nom, 2, 100);
    }

    /**
     * Valide une adresse
     * @param adresse l'adresse à valider
     * @return true si l'adresse est valide (peut être null/vide car optionnelle)
     */
    public static boolean isValidAddress(String adresse) {
        if (adresse == null || adresse.trim().isEmpty()) {
            return true; // L'adresse est optionnelle
        }
        return isValidLength(adresse, 1, 255);
    }

    /**
     * Valide complètement les données d'un client
     * @param nom le nom du client
     * @param email l'email du client
     * @param telephone le téléphone du client (optionnel)
     * @param adresse l'adresse du client (optionnelle)
     * @return message d'erreur ou null si tout est valide
     */
    public static String validateCustomerData(String nom, String email, String telephone, String adresse) {
        if (!isValidCustomerName(nom)) {
            return "Le nom doit contenir entre 2 et 100 caractères";
        }

        if (!isValidEmail(email)) {
            return "Format d'email invalide";
        }

        if (!isValidPhoneNumber(telephone)) {
            return "Format de numéro de téléphone invalide";
        }

        if (!isValidAddress(adresse)) {
            return "L'adresse ne peut pas dépasser 255 caractères";
        }

        return null; // Tout est valide
    }

    /**
     * Nettoie et formate un numéro de téléphone
     * @param phone le numéro à formater
     * @return le numéro formaté ou null
     */
    public static String formatPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }

        // Nettoyer le numéro
        String cleaned = phone.replaceAll("[\\s.-]", "");

        // Formater si c'est un numéro français
        if (cleaned.startsWith("+33")) {
            return cleaned;
        } else if (cleaned.startsWith("0") && cleaned.length() == 10) {
            return "+33" + cleaned.substring(1);
        }

        return cleaned;
    }
}
