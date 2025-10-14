package com.elepha.solutions.rto.service.impl;

import com.elepha.solutions.rto.model.VehicleInfo;
import com.elepha.solutions.rto.repository.VehicleInfoRepository;
import com.elepha.solutions.rto.service.VehicleInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class VehicleInfoServiceImpl implements VehicleInfoService {

    private static final Logger log = LoggerFactory.getLogger(VehicleInfoServiceImpl.class);

    private final VehicleInfoRepository vehicleInfoRepository;
    private final SecurityContextRepository securityContextRepository;

    private VehicleInfoServiceImpl(VehicleInfoRepository vehicleInfoRepository, SecurityContextRepository securityContextRepository) {
        this.vehicleInfoRepository = vehicleInfoRepository;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    public Slice<VehicleInfo> findAllVehiclesByUsername(String username, int pageNumber, int pageSize) {
        log.info("Fetching vehicles with pageNumber {} with pageSize {}", pageNumber, pageSize);
        Sort.TypedSort<VehicleInfo> vehicleInfoTypedSort = Sort.sort(VehicleInfo.class);
        Sort vehicleSort = vehicleInfoTypedSort.by(VehicleInfo::getVehicleNumber).ascending();
        return vehicleInfoRepository.findByUsername(username, PageRequest.of(Math.max(0, pageNumber - 1), pageSize, vehicleSort));
    }

    @Override
    public VehicleInfo saveVehicleInDb(VehicleInfo requestBody, HttpServletRequest httpServletRequest) {
        DeferredSecurityContext deferredSecurityContext = securityContextRepository.loadDeferredContext(httpServletRequest);
        requestBody.setUsername(deferredSecurityContext.get().getAuthentication().getName());
        return vehicleInfoRepository.save(requestBody);
    }
}