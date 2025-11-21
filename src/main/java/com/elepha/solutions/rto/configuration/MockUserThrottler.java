package com.elepha.solutions.rto.configuration;

import com.elepha.solutions.rto.repository.VehicleInfoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.stereotype.Component;

/**
 * This class is for restricting all user with roles MOCK_USER
 * from inserting more than 20 vehicles. The MOCK_USER role is
 * for sampling the application only.
 */
@Slf4j
@AllArgsConstructor
@Component(value = "mockUserThrottler")
public class MockUserThrottler {

    protected static final String MOCK_USER = "MOCK_USER";
    private final VehicleInfoRepository vehicleInfoRepository;

    public boolean throttleApiRequest(MethodSecurityExpressionOperations operations) {
        if (operations.hasAuthority(MOCK_USER)) {
            // fetch count of existing records
            String username = operations.getAuthentication().getName();
            long vehicleCount = vehicleInfoRepository.countByUsername(username);
            // if count > 20 return false
            return vehicleCount < 20;
        }
        return true;
    }
}