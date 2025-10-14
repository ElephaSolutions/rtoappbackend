package com.elepha.solutions.rto.controller;

import com.elepha.solutions.rto.dto.MetadataApiResponse;
import com.elepha.solutions.rto.dto.RecentActivitiesResponse;
import com.elepha.solutions.rto.model.VehicleInfo;
import com.elepha.solutions.rto.repository.VehicleInfoRepository;
import com.elepha.solutions.rto.service.VehicleInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.envers.RevisionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicle")
public class VehicleManagementController {

    private static final Logger log = LoggerFactory.getLogger(VehicleManagementController.class);
    private static final String FETCH_REVISION_HISTORY = "select inf.revtstmp, aud.vehicle_no, aud.revtype from vehicle_info_aud aud join revinfo inf on aud.rev = inf.rev order by inf.revtstmp desc limit 5";

    private final VehicleInfoRepository vehicleInfoRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SecurityContextRepository httpSessionSecurityContextRepository;
    private final VehicleInfoService vehicleInfoService;

    private VehicleManagementController(VehicleInfoRepository vehicleInfoRepository, NamedParameterJdbcTemplate namedParameterJdbcTemplate
            , SecurityContextRepository securityContextRepository, VehicleInfoService vehicleInfoService) {
        this.vehicleInfoRepository = vehicleInfoRepository;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.httpSessionSecurityContextRepository = securityContextRepository;
        this.vehicleInfoService = vehicleInfoService;
    }

    @PostMapping
    public ResponseEntity<VehicleInfo> saveVehicleInfo(@RequestBody VehicleInfo vehicleInfo, HttpServletRequest httpServletRequest) {
        log.atInfo().log("Received save vehicle request");
        DeferredSecurityContext deferredSecurityContext = httpSessionSecurityContextRepository.loadDeferredContext(httpServletRequest);
        vehicleInfo.setUsername(deferredSecurityContext.get().getAuthentication().getName());
        return ResponseEntity.ok(vehicleInfoRepository.save(vehicleInfo));
    }

    @GetMapping
    public ResponseEntity<Slice<VehicleInfo>> fetchAllVehicleDetails(@RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "page_size", defaultValue = "10") int pageSize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Slice<VehicleInfo> vehicleInfoPage = vehicleInfoService.findAllVehiclesByUsername(authentication.getName(), page, pageSize);
        log.info("Returning vehicle info page response for fetch request");
        return ResponseEntity.ok(vehicleInfoPage);
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<RecentActivitiesResponse>> fetchRecentActivity() {
        log.atInfo().log("Received fetch recent activity request");
        List<RecentActivitiesResponse> revisions = namedParameterJdbcTemplate.query(FETCH_REVISION_HISTORY, (rs, rowNum) -> new RecentActivitiesResponse(RevisionType.fromRepresentation(rs.getByte("revtype")), rs.getString("vehicle_no"), Timestamp.from(Instant.ofEpochMilli(rs.getLong("revtstmp")))));
        return ResponseEntity.ok(revisions);
    }

    @GetMapping("/metadata")
    public ResponseEntity<MetadataApiResponse> fetchMetadata() {
        log.atInfo().log("Received fetch metadata request");
        Timestamp currentTimestamp = Timestamp.from(Instant.now().plus(30, ChronoUnit.DAYS));
        return ResponseEntity.ok(
                new MetadataApiResponse(
                        vehicleInfoRepository.count(),
                        vehicleInfoRepository.countByFcExpiryDateBeforeOrInsuranceExpiryDateBeforeOrPermitExpiryDateBeforeOrTaxDueDateBeforeOrPollutionCertificateExpiryDateBefore(currentTimestamp, currentTimestamp, currentTimestamp, currentTimestamp, currentTimestamp)
                )
        );
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteVehicleInfo(@RequestHeader(name = "vehicle_number") String vehicleNumber) {
        vehicleInfoRepository.deleteById(vehicleNumber);
        log.atInfo().log("Deleted vehicle record with number {}", vehicleNumber);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}