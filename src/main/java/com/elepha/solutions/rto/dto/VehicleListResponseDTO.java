package com.elepha.solutions.rto.dto;

import com.elepha.solutions.rto.model.VehicleInfo;

import java.util.List;

public record VehicleListResponseDTO(List<VehicleInfo> vehicles, long totalVehicles) {
}
