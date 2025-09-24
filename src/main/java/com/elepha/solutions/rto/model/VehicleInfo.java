package com.elepha.solutions.rto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
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
}