package com.elepha.solutions.rto.controller;

import com.elepha.solutions.rto.dto.RecentActivitiesResponse;
import com.elepha.solutions.rto.dto.VehicleListResponseDTO;
import com.elepha.solutions.rto.model.VehicleInfo;
import com.elepha.solutions.rto.repository.VehicleInfoRepository;
import org.hibernate.envers.RevisionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicle")
public class VehicleManagementController {

    private static final Logger log = LoggerFactory.getLogger(VehicleManagementController.class);
    private static final String FETCH_REVISION_HISTORY = "select inf.revtstmp, aud.vehicle_no, aud.revtype from vehicle_info_aud aud join revinfo inf on aud.rev = inf.rev order by inf.revtstmp desc limit 5";

    private final VehicleInfoRepository vehicleInfoRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private VehicleManagementController(VehicleInfoRepository vehicleInfoRepository, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.vehicleInfoRepository = vehicleInfoRepository;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @GetMapping("/ping")
    public String ping() {
        return "I'm alive";
    }

    @PostMapping
    public ResponseEntity<VehicleInfo> saveVehicleInfo(@RequestBody VehicleInfo vehicleInfo) {
        log.atInfo().log("Received save vehicle request");
        return ResponseEntity.ok(vehicleInfoRepository.save(vehicleInfo));
    }

    @GetMapping
    public ResponseEntity<VehicleListResponseDTO> fetchAllVehicleDetails(@RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "page_size", defaultValue = "10") int pageSize) {
        List<VehicleInfo> vehicleInfoList = vehicleInfoRepository.findAll(PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "vehicleNumber"))).toList();
        long totalCount = vehicleInfoRepository.count();
        log.atInfo().log("Fetched {} records for fetch request out of {} records", vehicleInfoList.size(), totalCount);
        return ResponseEntity.ok(new VehicleListResponseDTO(vehicleInfoList, totalCount));
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<RecentActivitiesResponse>> fetchRecentActivity() {
        List<RecentActivitiesResponse> revisions = namedParameterJdbcTemplate.query(FETCH_REVISION_HISTORY, (rs, rowNum) -> new RecentActivitiesResponse(RevisionType.fromRepresentation(rs.getByte("revtype")), rs.getString("vehicle_no"), Timestamp.from(Instant.ofEpochMilli(rs.getLong("revtstmp")))));
        return ResponseEntity.ok(revisions);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteVehicleInfo(@RequestHeader(name = "vehicle_number") String vehicleNumber) {
        vehicleInfoRepository.deleteById(vehicleNumber);
        log.atInfo().log("Deleted vehicle record with number {}", vehicleNumber);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}