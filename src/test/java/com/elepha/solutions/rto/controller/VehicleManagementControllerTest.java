package com.elepha.solutions.rto.controller;

import com.elepha.solutions.rto.model.VehicleInfo;
import com.elepha.solutions.rto.repository.VehicleInfoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = {"unit"})
class VehicleManagementControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockitoSpyBean
    private VehicleInfoRepository vehicleInfoRepository;

    @Test
    @WithMockUser(username = "batman", password = "myPassword", authorities = {"USER"})
    void fetchByUsernameReturnsRecordsInPageOnlyTest() {
        Map<String, Object> response = webTestClient.get().uri("/api/v1/vehicle?page=1").exchange()
                .expectStatus().isOk().expectBody(new ParameterizedTypeReference<Map<String, Object>>() {}).returnResult().getResponseBody();
        List<Map<String, String>> content = (List<Map<String, String>>) response.get("content");
        assertThat(content).hasSize(2);
        assertThat(content.stream().allMatch(contentRecord -> contentRecord.get("username").equals("batman"))).isTrue();
        assertThat((boolean)response.get("last")).isTrue();
    }

    @Test
    @WithMockUser(username = "batman", password = "myPassword", authorities = {"USER"})
    void fetchByUsernameReturnsRecordsInPageWithPageSizeOnlyTest() {
        Map<String, Object> response = webTestClient.get().uri("/api/v1/vehicle?page=1&page_size=1").exchange()
                .expectStatus().isOk().expectBody(new ParameterizedTypeReference<Map<String, Object>>() {}).returnResult().getResponseBody();
        List<Map<String, String>> content = (List<Map<String, String>>) response.get("content");
        assertThat(content).hasSize(1);
        assertThat(content.stream().allMatch(contentRecord -> contentRecord.get("username").equals("batman"))).isTrue();
        assertThat((boolean)response.get("last")).isFalse();
    }

    @Test
    @WithMockUser(username = "deadshot", password = "myPassword", authorities = {"USER"})
    void saveVehicleInfoStoresInDbWithUsername() {
        VehicleInfo vehicleInfo = new VehicleInfo();
        vehicleInfo.setVehicleNumber("EE-14-R-5678");
        vehicleInfo.setContactNumber("1234567890");
        Timestamp currentTimeStamp = Timestamp.from(Instant.now());
        vehicleInfo.setFcExpiryDate(currentTimeStamp);
        vehicleInfo.setPermitExpiryDate(currentTimeStamp);
        vehicleInfo.setInsuranceExpiryDate(currentTimeStamp);
        vehicleInfo.setPollutionCertificateExpiryDate(currentTimeStamp);
        vehicleInfo.setTaxDueDate(currentTimeStamp);
        webTestClient.post().uri("/api/v1/vehicle").bodyValue(vehicleInfo).exchange().expectStatus().isOk();
        ArgumentCaptor<VehicleInfo> vehicleInfoArgumentCaptor = ArgumentCaptor.forClass(VehicleInfo.class);
        verify(vehicleInfoRepository, times(1)).save(vehicleInfoArgumentCaptor.capture());
        assertThat(vehicleInfoArgumentCaptor.getValue().getUsername()).isEqualTo("deadshot");
    }
}