package com.elepha.solutions.rto.controller;

import com.elepha.solutions.rto.model.VehicleInfo;
import com.elepha.solutions.rto.repository.VehicleInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.ok(vehicleInfoRepository.save(vehicleInfo));
    }

    @GetMapping
    public ResponseEntity<List<VehicleInfo>> fetchAllVehicleDetails(@RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "page_size", defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(vehicleInfoRepository.findAll(PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "vehicleNumber"))).toList());
    }
}