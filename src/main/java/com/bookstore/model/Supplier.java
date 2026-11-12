package com.bookstore.model;

/**
 * Common supplier data and serialization contract.
 */
public abstract class Supplier {

    private String supplierId;
    private String supplierName;
    private String phoneNumber;
    private String emailAddress;
    private String companyName;
    private String registrationDate;

    protected Supplier() {
    }

    protected Supplier(String supplierId, String supplierName, String phoneNumber,
                       String emailAddress, String companyName, String registrationDate) {
        setSupplierId(supplierId);
        setSupplierName(supplierName);
        setPhoneNumber(phoneNumber);
        setEmailAddress(emailAddress);
        setCompanyName(companyName);
        setRegistrationDate(registrationDate);
    }

    public abstract String getSupplierCategory();

    public abstract String getTaxRegistrationDetails();

    public abstract String toFileString();

    public static Supplier fromFileString(String line) {
        if (line == null) {
            return null;
        }
        String trimmedLine = line.trim();
        if (trimmedLine.isEmpty() || trimmedLine.startsWith("#") || trimmedLine.startsWith("//")) {
            return null;
        }

        String[] parts = trimmedLine.split("\\|", -1);
        if (parts.length < 8) {
            throw new IllegalArgumentException("Invalid supplier line: expected at least 8 tokens, got " + parts.length);
        }

        String supplierId = parts[0].trim();
        String type = parts[1].trim();
        String supplierName = parts[2].trim();
        String phoneNumber = parts[3].trim();
        String emailAddress = parts[4].trim();
        String companyName = parts[5].trim();
        String registrationDate = parts[6].trim();

        if ("LOCAL".equalsIgnoreCase(type)) {
            if (parts.length != 8) {
                throw new IllegalArgumentException("Invalid local supplier line: expected 8 tokens");
            }
            return new LocalSupplier(supplierId, supplierName, phoneNumber, emailAddress,
                    companyName, registrationDate, parts[7].trim());
        }
        if ("INTERNATIONAL".equalsIgnoreCase(type)) {
            if (parts.length != 8) {
                throw new IllegalArgumentException("Invalid international supplier line: expected 8 tokens");
            }
            String[] internationalDetails = parts[7].trim().split(":", 2);
            if (internationalDetails.length != 2) {
                throw new IllegalArgumentException("Invalid international supplier tax details");
            }
            return new InternationalSupplier(supplierId, supplierName, phoneNumber, emailAddress,
                    companyName, registrationDate, internationalDetails[0].trim(), internationalDetails[1].trim());
        }
        throw new IllegalArgumentException("Unknown supplier type token: " + type);
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = requireText(supplierId, "supplierId");
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = requireText(supplierName, "supplierName");
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = requireText(phoneNumber, "phoneNumber");
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        String value = requireText(emailAddress, "emailAddress");
        if (!value.contains("@") || value.startsWith("@") || value.endsWith("@")) {
            throw new IllegalArgumentException("emailAddress must be a valid email address");
        }
        this.emailAddress = value;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = requireText(companyName, "companyName");
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = requireText(registrationDate, "registrationDate");
    }

    protected static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
