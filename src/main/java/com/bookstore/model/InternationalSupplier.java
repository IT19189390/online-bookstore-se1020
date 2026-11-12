package com.bookstore.model;

public class InternationalSupplier extends Supplier {

    private String country;
    private String customsCode;

    public InternationalSupplier() {
        super();
    }

    public InternationalSupplier(String supplierId, String supplierName, String phoneNumber,
                                 String emailAddress, String companyName, String registrationDate,
                                 String country, String customsCode) {
        super(supplierId, supplierName, phoneNumber, emailAddress, companyName, registrationDate);
        setCountry(country);
        setCustomsCode(customsCode);
    }

    @Override
    public String getSupplierCategory() {
        return "International Supplier (" + country + ")";
    }

    @Override
    public String getTaxRegistrationDetails() {
        return "Customs Code: " + customsCode;
    }

    @Override
    public String toFileString() {
        return String.join("|", getSupplierId(), "INTERNATIONAL", getSupplierName(), getPhoneNumber(),
                getEmailAddress(), getCompanyName(), getRegistrationDate(), country + ":" + customsCode);
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = requireText(country, "country");
    }

    public String getCustomsCode() {
        return customsCode;
    }

    public void setCustomsCode(String customsCode) {
        this.customsCode = requireText(customsCode, "customsCode");
    }

    public String getBusinessRegNumber() {
        return "";
    }
}
