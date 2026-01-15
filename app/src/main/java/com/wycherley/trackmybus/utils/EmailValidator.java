package com.wycherley.trackmybus.utils;

import android.util.Patterns;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Comprehensive Email Validator
 * Prevents invalid emails like "akash@gmail.comd"
 */
public class EmailValidator {

    // Comprehensive list of valid TLDs
    private static final Set<String> VALID_TLDS = new HashSet<>(Arrays.asList(
            // Generic TLDs
            "com", "org", "net", "edu", "gov", "mil", "int",

            // Country code TLDs (popular ones)
            "uk", "us", "ca", "au", "de", "fr", "jp", "in", "lk", "cn", "br", "ru",

            // New generic TLDs
            "io", "ai", "app", "dev", "tech", "cloud", "digital", "online",
            "info", "biz", "name", "pro", "xyz", "site", "store", "shop",
            "web", "email", "live", "today", "world", "space", "website",

            // Popular email providers
            "gmail", "yahoo", "outlook", "hotmail", "icloud", "proton",

            // Two-letter country codes with common usage
            "ae", "ag", "ar", "at", "be", "bg", "ch", "cl", "co", "cz",
            "dk", "es", "fi", "gr", "hk", "hr", "hu", "id", "ie", "il",
            "it", "ke", "kr", "lt", "lv", "mx", "my", "nl", "no", "nz",
            "ph", "pk", "pl", "pt", "ro", "rs", "se", "sg", "sk", "th",
            "tr", "tw", "ua", "vn", "za"
    ));

    /**
     * Validates email address with strict rules
     *
     * @param email Email address to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        email = email.toLowerCase().trim();

        // Step 1: Basic pattern check
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }

        // Step 2: Check for spaces
        if (email.contains(" ")) {
            return false;
        }

        // Step 3: Must contain exactly one @
        if (!email.contains("@")) {
            return false;
        }

        String[] parts = email.split("@");
        if (parts.length != 2) {
            return false;
        }

        String localPart = parts[0];
        String domainPart = parts[1];

        // Step 4: Validate local part (before @)
        if (!isValidLocalPart(localPart)) {
            return false;
        }

        // Step 5: Validate domain part (after @)
        if (!isValidDomain(domainPart)) {
            return false;
        }

        return true;
    }

    /**
     * Validates the local part of email (before @)
     */
    private static boolean isValidLocalPart(String localPart) {
        if (localPart == null || localPart.isEmpty()) {
            return false;
        }

        // Length check (RFC 5321)
        if (localPart.length() > 64) {
            return false;
        }

        // Cannot start or end with dot
        if (localPart.startsWith(".") || localPart.endsWith(".")) {
            return false;
        }

        // No consecutive dots
        if (localPart.contains("..")) {
            return false;
        }

        // Only allow alphanumeric, dots, hyphens, underscores
        if (!localPart.matches("[a-z0-9._-]+")) {
            return false;
        }

        return true;
    }

    /**
     * Validates the domain part of email (after @)
     */
    private static boolean isValidDomain(String domain) {
        if (domain == null || domain.isEmpty()) {
            return false;
        }

        // Length check (RFC 5321)
        if (domain.length() > 255) {
            return false;
        }

        // Must contain at least one dot
        if (!domain.contains(".")) {
            return false;
        }

        // No consecutive dots
        if (domain.contains("..")) {
            return false;
        }

        // Split domain into parts
        String[] parts = domain.split("\\.");
        if (parts.length < 2) {
            return false;
        }

        // Validate each part
        for (String part : parts) {
            if (!isValidDomainPart(part)) {
                return false;
            }
        }

        // Validate TLD (last part)
        String tld = parts[parts.length - 1];
        if (!isValidTLD(tld)) {
            return false;
        }

        return true;
    }

    /**
     * Validates a single part of the domain
     */
    private static boolean isValidDomainPart(String part) {
        if (part == null || part.isEmpty()) {
            return false;
        }

        // Length check (RFC 1035)
        if (part.length() > 63) {
            return false;
        }

        // Cannot start or end with hyphen
        if (part.startsWith("-") || part.endsWith("-")) {
            return false;
        }

        // Only alphanumeric and hyphens
        if (!part.matches("[a-z0-9-]+")) {
            return false;
        }

        return true;
    }

    /**
     * Validates top-level domain (TLD)
     */
    private static boolean isValidTLD(String tld) {
        if (tld == null || tld.isEmpty()) {
            return false;
        }

        // TLD must be at least 2 characters
        if (tld.length() < 2) {
            return false;
        }

        // Only letters allowed in TLD
        if (!tld.matches("[a-z]+")) {
            return false;
        }

        // Check against known valid TLDs
        return VALID_TLDS.contains(tld);
    }

    /**
     * Returns a user-friendly error message if email is invalid
     */
    public static String getErrorMessage(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }

        email = email.toLowerCase().trim();

        if (email.contains(" ")) {
            return "Email cannot contain spaces";
        }

        if (!email.contains("@")) {
            return "Email must contain @ symbol";
        }

        String[] parts = email.split("@");
        if (parts.length != 2) {
            return "Invalid email format";
        }

        String localPart = parts[0];
        String domainPart = parts[1];

        if (localPart.isEmpty()) {
            return "Email cannot start with @";
        }

        if (localPart.startsWith(".") || localPart.endsWith(".")) {
            return "Email cannot start or end with a dot";
        }

        if (!domainPart.contains(".")) {
            return "Email domain must contain a dot (e.g., gmail.com)";
        }

        String[] domainParts = domainPart.split("\\.");
        if (domainParts.length < 2) {
            return "Invalid email domain";
        }

        String tld = domainParts[domainParts.length - 1];
        if (!isValidTLD(tld)) {
            return "Invalid domain extension (." + tld + "). Did you mean .com?";
        }

        return "Invalid email format";
    }

    /**
     * Suggests corrections for common typos
     */
    public static String suggestCorrection(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        email = email.toLowerCase().trim();

        // Common typos and corrections
        if (email.endsWith(".comd")) {
            return email.replace(".comd", ".com");
        }
        if (email.endsWith(".comn")) {
            return email.replace(".comn", ".com");
        }
        if (email.endsWith(".cpm")) {
            return email.replace(".cpm", ".com");
        }
        if (email.endsWith(".con")) {
            return email.replace(".con", ".com");
        }
        if (email.endsWith(".comm")) {
            return email.replace(".comm", ".com");
        }
        if (email.endsWith(".col")) {
            return email.replace(".col", ".com");
        }
        if (email.endsWith(".net.com")) {
            return email.replace(".net.com", ".com");
        }
        if (email.contains("gmial")) {
            return email.replace("gmial", "gmail");
        }
        if (email.contains("gmai")) {
            return email.replace("gmai", "gmail");
        }
        if (email.contains("yahooo")) {
            return email.replace("yahooo", "yahoo");
        }

        return null;
    }
}