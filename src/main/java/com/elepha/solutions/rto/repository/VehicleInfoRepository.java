package com.elepha.solutions.rto.repository;

import com.elepha.solutions.rto.model.VehicleInfo;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.stream.Stream;

@Repository
public interface VehicleInfoRepository extends PagingAndSortingRepository<VehicleInfo, String>, CrudRepository<VehicleInfo, String> {

    long countByFcExpiryDateBeforeOrInsuranceExpiryDateBeforeOrPermitExpiryDateBeforeOrTaxDueDateBeforeOrPollutionCertificateExpiryDateBefore(Timestamp fcExpiryDate, Timestamp insuranceExpiryDate, Timestamp permitExpiryDate, Timestamp taxDueDate, Timestamp pollutionCertificateExpiryDate);
    @NativeQuery(value = "select * from vehicle_info where date_trunc('day', fc_date) = :check_date or date_trunc('day', ins_date) = :check_date or date_trunc('day', permit_date) = :check_date or date_trunc('day', tax_date) = :check_date or date_trunc('day', puc_date) = :check_date")
    Stream<VehicleInfo> fetchExpiringRecords(@Param(value = "check_date") LocalDate checkDate);
}