package com.elepha.solutions.rto.controller;

import com.elepha.solutions.rto.dto.VehicleListResponseDTO;
import com.elepha.solutions.rto.model.VehicleInfo;
import com.elepha.solutions.rto.repository.VehicleInfoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/vehicle")
public class VehicleManagementController {

    private final VehicleInfoRepository vehicleInfoRepository;

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
}