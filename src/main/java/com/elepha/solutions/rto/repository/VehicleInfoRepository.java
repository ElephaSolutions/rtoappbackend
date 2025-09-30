package com.elepha.solutions.rto.repository;

import com.elepha.solutions.rto.model.VehicleInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface VehicleInfoRepository extends PagingAndSortingRepository<VehicleInfo, String>, CrudRepository<VehicleInfo, String> {

    long countByFcExpiryDateBeforeOrInsuranceExpiryDateBeforeOrPermitExpiryDateBeforeOrTaxDueDateBeforeOrPollutionCertificateExpiryDateBefore(Timestamp fcExpiryDate, Timestamp insuranceExpiryDate, Timestamp permitExpiryDate, Timestamp taxDueDate, Timestamp pollutionCertificateExpiryDate);
}