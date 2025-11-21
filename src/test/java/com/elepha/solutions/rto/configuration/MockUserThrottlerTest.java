package com.elepha.solutions.rto.configuration;

import com.elepha.solutions.rto.repository.VehicleInfoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(value = MockitoExtension.class)
class MockUserThrottlerTest {

    @InjectMocks
    private MockUserThrottler mockUserThrottler;
    @Mock
    private VehicleInfoRepository vehicleInfoRepository;

    @Test
    void throttleApiRequestReturnsFalseWhenCountGreaterThan20() {
        MethodSecurityExpressionOperations mockOperations = mock(MethodSecurityExpressionOperations.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(mockOperations).hasAuthority(anyString());
        doReturn(20L).when(vehicleInfoRepository).countByUsername(nullable(String.class));
        boolean isAuthorized = mockUserThrottler.throttleApiRequest(mockOperations);
        assertThat(isAuthorized).isFalse();
    }

    @Test
    void throttleApiRequestReturnsTrueWhenRoleIsNotMockUser() {
        MethodSecurityExpressionOperations mockOperations = mock(MethodSecurityExpressionOperations.class);
        doReturn(false).when(mockOperations).hasAuthority(anyString());
        boolean isAuthorized = mockUserThrottler.throttleApiRequest(mockOperations);
        assertThat(isAuthorized).isTrue();
    }

    @Test
    void throttleApiRequestReturnsTrueWhenCountLessThen20() {
        MethodSecurityExpressionOperations mockOperations = mock(MethodSecurityExpressionOperations.class, RETURNS_DEEP_STUBS);
        doReturn(true).when(mockOperations).hasAuthority(anyString());
        doReturn(18L).when(vehicleInfoRepository).countByUsername(nullable(String.class));
        boolean isAuthorized = mockUserThrottler.throttleApiRequest(mockOperations);
        assertThat(isAuthorized).isTrue();
    }
}