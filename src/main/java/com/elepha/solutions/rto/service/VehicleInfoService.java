package com.elepha.solutions.rto.service;

import com.elepha.solutions.rto.model.VehicleInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Slice;

public interface VehicleInfoService {

    Slice<VehicleInfo> findAllVehiclesByUsername(String username, int pageNumber, int pageSize);
    VehicleInfo saveVehicleInDb(VehicleInfo requestBody, HttpServletRequest httpServletRequest);
}