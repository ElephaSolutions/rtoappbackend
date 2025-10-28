package com.elepha.solutions.rto.service;

import com.elepha.solutions.rto.dto.MetadataApiResponse;
import com.elepha.solutions.rto.dto.RecentActivitiesResponse;
import com.elepha.solutions.rto.model.VehicleInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface VehicleInfoService {

    Slice<VehicleInfo> findAllVehiclesByUsername(int pageNumber, int pageSize);
    VehicleInfo saveVehicleInDb(VehicleInfo requestBody, HttpServletRequest httpServletRequest);
    List<RecentActivitiesResponse> fetchRecentActivities();
    MetadataApiResponse fetchMetadataForUsername();
    void deleteVehicleByVehicleNumber(String vehicleNumber);
}