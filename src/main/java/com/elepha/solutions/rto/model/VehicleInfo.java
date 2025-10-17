package com.elepha.solutions.rto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.envers.Audited;

import java.sql.Timestamp;

@Entity
@Audited
@Table(name = "vehicle_info")
public class VehicleInfo {

    @Id
    @Column(name = "vehicle_no")
    private String vehicleNumber;
    @Column(name = "fc_date")
    private Timestamp fcExpiryDate;
    @Column(name = "ins_date")
    private Timestamp insuranceExpiryDate;
    @Column(name = "permit_date")
    private Timestamp permitExpiryDate;
    @Column(name = "tax_date")
    private Timestamp taxDueDate;
    @Column(name = "puc_date")
    private Timestamp pollutionCertificateExpiryDate;
    @Column(name = "contact_no")
    private String contactNumber;
    private String username;
    @Column(name = "ltt_flag")
    private boolean lifeTimeTaxPaid;

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Timestamp getFcExpiryDate() {
        return fcExpiryDate;
    }

    public void setFcExpiryDate(Timestamp fcExpiryDate) {
        this.fcExpiryDate = fcExpiryDate;
    }

    public Timestamp getInsuranceExpiryDate() {
        return insuranceExpiryDate;
    }

    public void setInsuranceExpiryDate(Timestamp insuranceExpiryDate) {
        this.insuranceExpiryDate = insuranceExpiryDate;
    }

    public Timestamp getPermitExpiryDate() {
        return permitExpiryDate;
    }

    public void setPermitExpiryDate(Timestamp permitExpiryDate) {
        this.permitExpiryDate = permitExpiryDate;
    }

    public Timestamp getTaxDueDate() {
        return taxDueDate;
    }

    public void setTaxDueDate(Timestamp taxDueDate) {
        this.taxDueDate = taxDueDate;
    }

    public Timestamp getPollutionCertificateExpiryDate() {
        return pollutionCertificateExpiryDate;
    }

    public void setPollutionCertificateExpiryDate(Timestamp pollutionCertificateExpiryDate) {
        this.pollutionCertificateExpiryDate = pollutionCertificateExpiryDate;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public boolean isLifeTimeTaxPaid() {
        return lifeTimeTaxPaid;
    }

    public void setLifeTimeTaxPaid(boolean lifeTimeTaxPaid) {
        this.lifeTimeTaxPaid = lifeTimeTaxPaid;
    }
}