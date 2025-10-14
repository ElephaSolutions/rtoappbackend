package com.elepha.solutions.rto.service;

import com.elepha.solutions.rto.model.VehicleInfo;
import org.springframework.data.domain.Slice;

public interface VehicleInfoService {

    Slice<VehicleInfo> findAllVehiclesByUsername(String username, int pageNumber, int pageSize);
}