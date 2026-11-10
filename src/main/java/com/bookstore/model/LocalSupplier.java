package com.bookstore.model;

public class LocalSupplier extends Supplier {

    private String businessRegNumber;

    public LocalSupplier() {
        super();
    }

    public LocalSupplier(String supplierId, String supplierName, String phoneNumber,
                         String emailAddress, String companyName, String registrationDate,
                         String businessRegNumber) {
        super(supplierId, supplierName, phoneNumber, emailAddress, companyName, registrationDate);
        setBusinessRegNumber(businessRegNumber);
    }

    @Override
    public String getSupplierCategory() {
        return "Local Supplier";
    }

    @Override
    public String getTaxRegistrationDetails() {
        return "Local Reg: " + businessRegNumber;
    }

    @Override
    public String toFileString() {
        return String.join("|", getSupplierId(), "LOCAL", getSupplierName(), getPhoneNumber(),
                getEmailAddress(), getCompanyName(), getRegistrationDate(), businessRegNumber);
    }

    public String getBusinessRegNumber() {
        return businessRegNumber;
    }

    public void setBusinessRegNumber(String businessRegNumber) {
        this.businessRegNumber = requireText(businessRegNumber, "businessRegNumber");
    }

    public String getCountry() {
        return "";
    }

    public String getCustomsCode() {
        return "";
    }
}
