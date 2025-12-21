package com.elepha.solutions.rto.controller;

import com.elepha.solutions.rto.dto.MetadataApiResponse;
import com.elepha.solutions.rto.dto.RecentActivitiesResponse;
import com.elepha.solutions.rto.model.VehicleInfo;
import com.elepha.solutions.rto.repository.VehicleInfoRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.hibernate.envers.RevisionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles(value = {"unit"})
@ExtendWith(value = OutputCaptureExtension.class)
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

    @Test
    @WithMockUser(username = "peacemaker", password = "myPassword", authorities = {"USER"})
    void fetchRecentActivityDataFetchesDataForOnlyTheUserTest() {
        List<RecentActivitiesResponse> response = webTestClient.get().uri("/api/v1/vehicle/recent-activity").exchange().expectStatus().isOk().expectBody(new ParameterizedTypeReference<List<RecentActivitiesResponse>>() {}).returnResult().getResponseBody();
        assertThat(response).hasSize(1);
        assertThat(response.get(0).revisionType()).isEqualTo(RevisionType.ADD);
    }

    @ParameterizedTest
    @ArgumentsSource(VehicleInfoArgumentsProvider.class)
    @WithMockUser(username = "harley_quinn", password = "myPassword", authorities = {"USER"})
    void metadataApiReturnsCorrectCountsTest(ImmutableTriple<VehicleInfo, Integer, Integer> vehicleInfoTotalCountExpiringCountTuple) {
        webTestClient.post().uri("/api/v1/vehicle").bodyValue(vehicleInfoTotalCountExpiringCountTuple.getLeft()).exchange().expectStatus().isOk();
        MetadataApiResponse response = webTestClient.get().uri("/api/v1/vehicle/metadata").exchange().expectStatus().isOk().expectBody(MetadataApiResponse.class).returnResult().getResponseBody();
        assertThat(response.totalVehicles()).isEqualTo(vehicleInfoTotalCountExpiringCountTuple.getMiddle().longValue());
        assertThat(response.expiringSoon()).isEqualTo(vehicleInfoTotalCountExpiringCountTuple.getRight().longValue());
        assertThat(response.agencyName()).isEqualTo("daily planet");
    }

    @Test
    @WithMockUser(username = "killer_croc", password = "myPassword", authorities = {"USER"})
    void deleteApiPrintsDeletedSuccessfullyDeletedTest(CapturedOutput capturedOutput) {
        webTestClient.delete().uri("/api/v1/vehicle").header("vehicle_number", "UP56CA0999").exchange().expectStatus().isOk();
        assertThat(capturedOutput.getAll()).contains("Deleted record with number UP56CA0999");
    }

    @Test
    @WithMockUser(username = "killer_croc", password = "myPassword", authorities = {"USER"})
    void deleteApiPrintsNoRecordDeletedTest(CapturedOutput capturedOutput) {
        webTestClient.delete().uri("/api/v1/vehicle").header("vehicle_number", "UP56CA0990").exchange().expectStatus().isOk();
        assertThat(capturedOutput.getAll()).contains("Cannot find any record for the vehicle number UP56CA0990 and username");
    }

    static class VehicleInfoArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            Timestamp nextMonthTimestamp = Timestamp.from(Instant.now().plus(25, ChronoUnit.DAYS));
            Timestamp nextYearTimestamp = Timestamp.from(Instant.now().plus(365, ChronoUnit.DAYS));
            return Stream.of(
                    ImmutableTriple.of(buildAndReturnVehicleInfo(StringUtils.leftPad("1", 5, "0"), nextMonthTimestamp, nextYearTimestamp, nextYearTimestamp, nextYearTimestamp, nextYearTimestamp, StringUtils.leftPad("1", 10, "0"), false), 1, 1),
                    ImmutableTriple.of(buildAndReturnVehicleInfo(StringUtils.leftPad("2", 5, "0"), nextYearTimestamp, nextMonthTimestamp, nextYearTimestamp, nextYearTimestamp, nextYearTimestamp, StringUtils.leftPad("2", 10, "0"), false), 2, 2),
                    ImmutableTriple.of(buildAndReturnVehicleInfo(StringUtils.leftPad("3", 5, "0"), nextYearTimestamp, nextYearTimestamp, nextMonthTimestamp, nextYearTimestamp, nextYearTimestamp, StringUtils.leftPad("3", 10, "0"), false), 3, 3),
                    ImmutableTriple.of(buildAndReturnVehicleInfo(StringUtils.leftPad("4", 5, "0"), nextYearTimestamp, nextYearTimestamp, nextYearTimestamp, nextMonthTimestamp, nextYearTimestamp, StringUtils.leftPad("4", 10, "0"), false), 4, 4),
                    ImmutableTriple.of(buildAndReturnVehicleInfo(StringUtils.leftPad("5", 5, "0"), nextYearTimestamp, nextYearTimestamp, nextYearTimestamp, nextYearTimestamp, nextMonthTimestamp, StringUtils.leftPad("5", 10, "0"), false), 5, 5),
                    ImmutableTriple.of(buildAndReturnVehicleInfo(StringUtils.leftPad("6", 5, "0"), nextYearTimestamp, nextYearTimestamp, nextYearTimestamp, nextYearTimestamp, nextYearTimestamp, StringUtils.leftPad("6", 10, "0"), false), 6, 5),
                    ImmutableTriple.of(buildAndReturnVehicleInfo(StringUtils.leftPad("7", 5, "0"), nextYearTimestamp, nextYearTimestamp, nextYearTimestamp, null, nextYearTimestamp, StringUtils.leftPad("7", 10, "0"), true), 7, 5)
            ).map(Arguments::of);
        }

        private VehicleInfo buildAndReturnVehicleInfo(String vehicleNumber, Timestamp fcExpiryDate, Timestamp insuranceExpiryDate
                , Timestamp permitExpiryDate, Timestamp taxDueDate, Timestamp pollutionCertificateExpiryDate, String contactNumber, boolean isLifeTimeTaxPaid) {
            VehicleInfo vehicleInfo = new VehicleInfo();
            vehicleInfo.setVehicleNumber(vehicleNumber);
            vehicleInfo.setFcExpiryDate(fcExpiryDate);
            vehicleInfo.setInsuranceExpiryDate(insuranceExpiryDate);
            vehicleInfo.setPermitExpiryDate(permitExpiryDate);
            vehicleInfo.setTaxDueDate(taxDueDate);
            vehicleInfo.setPollutionCertificateExpiryDate(pollutionCertificateExpiryDate);
            vehicleInfo.setContactNumber(contactNumber);
            vehicleInfo.setLifeTimeTaxPaid(isLifeTimeTaxPaid);
            return vehicleInfo;
        }
    }
}