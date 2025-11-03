package com.elepha.solutions.rto.service.impl;

import com.elepha.solutions.rto.dto.MetadataApiResponse;
import com.elepha.solutions.rto.dto.RecentActivitiesResponse;
import com.elepha.solutions.rto.model.VehicleInfo;
import com.elepha.solutions.rto.repository.VehicleInfoRepository;
import com.elepha.solutions.rto.service.VehicleInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.envers.RevisionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class VehicleInfoServiceImpl implements VehicleInfoService {

    private static final Logger log = LoggerFactory.getLogger(VehicleInfoServiceImpl.class);
    private static final String FETCH_REVISION_HISTORY = "select inf.revtstmp, aud.vehicle_no, aud.revtype from vehicle_info_aud aud join revinfo inf on aud.rev = inf.rev where aud.username = :username order by inf.revtstmp desc limit 5";
    private static final String FETCH_AGENCY_NAME_FOR_USER = "select agency_name from users where username = :username";

    private final VehicleInfoRepository vehicleInfoRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private VehicleInfoServiceImpl(VehicleInfoRepository vehicleInfoRepository, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.vehicleInfoRepository = vehicleInfoRepository;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Slice<VehicleInfo> findAllVehiclesByUsername(int pageNumber, int pageSize) {
        String username = getUsernameFromSecurityContext();
        log.info("Fetching vehicles with pageNumber {} with pageSize {}", pageNumber, pageSize);
        Sort.TypedSort<VehicleInfo> vehicleInfoTypedSort = Sort.sort(VehicleInfo.class);
        Sort vehicleSort = vehicleInfoTypedSort.by(VehicleInfo::getVehicleNumber).ascending();
        return vehicleInfoRepository.findByUsername(username, PageRequest.of(Math.max(0, pageNumber - 1), pageSize, vehicleSort));
    }

    @Override
    public VehicleInfo saveVehicleInDb(VehicleInfo requestBody, HttpServletRequest httpServletRequest) {
        String username = getUsernameFromSecurityContext();
        requestBody.setUsername(username);
        return vehicleInfoRepository.save(requestBody);
    }

    @Override
    public List<RecentActivitiesResponse> fetchRecentActivities() {
        String username = getUsernameFromSecurityContext();
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("username", username);
        log.info("Fetching last recent activity");
        return namedParameterJdbcTemplate.query(
                FETCH_REVISION_HISTORY,
                sqlParameterSource,
                (resultSet, rowIndex) -> new RecentActivitiesResponse(
                        RevisionType.fromRepresentation(resultSet.getByte("revtype")),
                        resultSet.getString("vehicle_no"),
                        Timestamp.from(Instant.ofEpochMilli(resultSet.getLong("revtstmp")))
                )
        );
    }

    @Override
    public MetadataApiResponse fetchMetadataForUsername() {
        String username = getUsernameFromSecurityContext();
        Timestamp currentTimestamp = Timestamp.from(Instant.now().plus(30, ChronoUnit.DAYS));
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("username", username);
        return new MetadataApiResponse(
                vehicleInfoRepository.countByUsername(username),
                vehicleInfoRepository.countExpiringRecordsForUsername(username, currentTimestamp),
                namedParameterJdbcTemplate.queryForObject(FETCH_AGENCY_NAME_FOR_USER, parameterSource, String.class)
        );
    }

    @Override
    public void deleteVehicleByVehicleNumber(String vehicleNumber) {
        vehicleInfoRepository.removeByUsernameAndVehicleNumber(getUsernameFromSecurityContext(), vehicleNumber)
                .ifPresentOrElse(
                        vehicleInfo -> log.info("Deleted record with number {}", vehicleNumber),
                        () -> log.info("Cannot find any record for the vehicle number {} and username", vehicleNumber)
                );
    }

    private String getUsernameFromSecurityContext() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}