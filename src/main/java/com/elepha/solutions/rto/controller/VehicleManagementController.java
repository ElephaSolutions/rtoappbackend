package com.elepha.solutions.rto.controller;

import com.elepha.solutions.rto.dto.MetadataApiResponse;
import com.elepha.solutions.rto.dto.RecentActivitiesResponse;
import com.elepha.solutions.rto.model.VehicleInfo;
import com.elepha.solutions.rto.repository.VehicleInfoRepository;
import com.elepha.solutions.rto.service.VehicleInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicle")
public class VehicleManagementController {

    private static final Logger log = LoggerFactory.getLogger(VehicleManagementController.class);

    private final VehicleInfoRepository vehicleInfoRepository;
    private final VehicleInfoService vehicleInfoService;

    private VehicleManagementController(VehicleInfoRepository vehicleInfoRepository, VehicleInfoService vehicleInfoService) {
        this.vehicleInfoRepository = vehicleInfoRepository;
        this.vehicleInfoService = vehicleInfoService;
    }

    @PostMapping
    public ResponseEntity<VehicleInfo> saveVehicleInfo(@RequestBody VehicleInfo vehicleInfo, HttpServletRequest httpServletRequest) {
        log.atInfo().log("Received save vehicle request");
        return ResponseEntity.ok(vehicleInfoService.saveVehicleInDb(vehicleInfo, httpServletRequest));
    }

    @GetMapping
    public ResponseEntity<Slice<VehicleInfo>> fetchAllVehicleDetails(@RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "page_size", defaultValue = "10") int pageSize) {
        Slice<VehicleInfo> vehicleInfoPage = vehicleInfoService.findAllVehiclesByUsername(page, pageSize);
        log.info("Returning vehicle info page response for fetch request");
        return ResponseEntity.ok(vehicleInfoPage);
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<RecentActivitiesResponse>> fetchRecentActivity() {
        log.atInfo().log("Received fetch recent activity request");
        return ResponseEntity.ok(vehicleInfoService.fetchRecentActivities());
    }

    @GetMapping("/metadata")
    public ResponseEntity<MetadataApiResponse> fetchMetadata() {
        log.atInfo().log("Received fetch metadata request");
        return ResponseEntity.ok(
                vehicleInfoService.fetchMetadataForUsername()
        );
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteVehicleInfo(@RequestHeader(name = "vehicle_number") String vehicleNumber) {
        vehicleInfoRepository.deleteById(vehicleNumber);
        log.atInfo().log("Deleted vehicle record with number {}", vehicleNumber);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}