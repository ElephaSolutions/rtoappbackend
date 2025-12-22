package com.elepha.solutions.rto.repository;

import com.elepha.solutions.rto.model.VehicleInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface VehicleInfoRepository extends PagingAndSortingRepository<VehicleInfo, String>, CrudRepository<VehicleInfo, String> {

    @NativeQuery(value = "select * from vehicle_info where username in :username and (date_trunc('day', fc_date) = :check_date or date_trunc('day', ins_date) = :check_date or date_trunc('day', permit_date) = :check_date or date_trunc('day', tax_date) = :check_date or date_trunc('day', puc_date) = :check_date)")
    Stream<VehicleInfo> fetchExpiringRecords(@Param(value = "username") List<String> username, @Param(value = "check_date") LocalDate checkDate);
    Page<VehicleInfo> findByUsername(String username, Pageable pageable);
    @Query(value = "select v from VehicleInfo v where v.username = :username and (v.vehicleNumber like %:searchTerm% or v.contactNumber like %:searchTerm%)")
    Page<VehicleInfo> searchByUsernameAndSearchTerm(@Param(value = "username") String username, @Param(value = "searchTerm") String searchTerm, Pageable pageable);
    long countByUsername(String username);
    @Query(
            value = "select count(v) from VehicleInfo v where v.username = :username and (v.fcExpiryDate < :expiring_date or v.insuranceExpiryDate < :expiring_date or v.permitExpiryDate < :expiring_date or v.taxDueDate < :expiring_date or v.pollutionCertificateExpiryDate < :expiring_date)"
    )
    long countExpiringRecordsForUsername(@Param(value = "username") String username, @Param(value = "expiring_date") Timestamp expiringDate);
    @Transactional
    Optional<VehicleInfo> removeByUsernameAndVehicleNumber(String username, String vehicleNumber);
}