package com.elepha.solutions.rto.configuration;

import com.elepha.solutions.rto.dto.AgencyDetailsDTO;
import com.elepha.solutions.rto.model.VehicleInfo;
import com.elepha.solutions.rto.repository.VehicleInfoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@EnableScheduling
@Configuration
public class ExpiryNotificationScheduledJob {

    private static final String FETCH_AGENCY_DETAILS_QUERY = "select owner_name, agency_name, contact_number from users where username = :username";
    private static final Logger log = LoggerFactory.getLogger(ExpiryNotificationScheduledJob.class);

    private final RestClient restClient;
    private final VehicleInfoRepository vehicleInfoRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final String messageServiceApiKey;
    private final String licenseNumber;
    private final ObjectMapper objectMapper;

    public ExpiryNotificationScheduledJob(RestClient.Builder restClientBuilder, VehicleInfoRepository vehicleInfoRepository
            , NamedParameterJdbcTemplate namedParameterJdbcTemplate, @Value("${message-service.host}") String messageServiceHost
            , @Value("${message-service.api-key}") String messageServiceApiKey, @Value("${message-service.licence-number}") String licenseNumber, ObjectMapper objectMapper) {
        this.restClient = restClientBuilder.baseUrl(messageServiceHost).build();
        this.vehicleInfoRepository = vehicleInfoRepository;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.messageServiceApiKey = messageServiceApiKey;
        this.licenseNumber = licenseNumber;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void fetchAndNotifyExpiringPermits() {
        LocalDate localDate = LocalDate.now().plusDays(30);
        // fetch expiring records
        try (Stream<VehicleInfo> expiringRecords = vehicleInfoRepository.fetchExpiringRecords(localDate)) {
            // fetch agency details for each expiring records
            expiringRecords
                    .map(expiringVehicleInfo -> {
                        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
                        mapSqlParameterSource.addValue("username", expiringVehicleInfo.getUsername());
                        AgencyDetailsDTO agencyDetailsDTO = namedParameterJdbcTemplate.queryForObject(
                                FETCH_AGENCY_DETAILS_QUERY, mapSqlParameterSource,
                                (rs, rowNum) ->
                                        new AgencyDetailsDTO(
                                                rs.getString("owner_name"),
                                                rs.getString("agency_name"),
                                                rs.getString("contact_number")
                                        )
                        );
                        return Pair.of(expiringVehicleInfo, agencyDetailsDTO);
                    })
                    // send notification for each record
                    .forEach(expiringRecordPair -> {
                        List<String> allExpiringPermits = findAllExpiringPermits(expiringRecordPair.getFirst(), localDate);
                        allExpiringPermits.forEach(
                                expiringPermit -> {
                                    log.info("Sending notification for record expiring with vehicle number {} for permit type {}", expiringRecordPair.getFirst().getVehicleNumber(), expiringPermit);
                                    String templateParams = String.join(",", expiringRecordPair.getFirst().getVehicleNumber(), expiringPermit, localDate.toString(), expiringRecordPair.getSecond().ownerName(), expiringRecordPair.getSecond().contactNumber(), expiringRecordPair.getSecond().ownerName());
                                    ResponseEntity<String> response = restClient.get().uri(
                                            uriBuilder ->
                                                    uriBuilder
                                                            .path("/api")
                                                            .path("/sendtemplate.php")
                                                            .queryParam("LicenseNumber", this.licenseNumber)
                                                            .queryParam("APIKey", this.messageServiceApiKey)
                                                            .queryParam("Template", "bac1")
                                                            .queryParam("Contact", expiringRecordPair.getFirst().getContactNumber())
                                                            .queryParam("Param", templateParams).build()
                                    ).retrieve().toEntity(String.class);
                                    try {
                                        Map parsedResponse = this.objectMapper.readValue(response.getBody(), Map.class);
                                        log.info("Received response from message API {}", parsedResponse.get("ApiResponse"));
                                    } catch (JsonProcessingException e) {
                                        log.error("Error parsing Message Notification API response");
                                    }
                                }
                        );
                    });
        }
    }

    private List<String> findAllExpiringPermits(VehicleInfo vehicleInfo, LocalDate checkDate) {
        List<String> allExpiringPermits = new ArrayList<>();
        if (vehicleInfo.getFcExpiryDate().toLocalDateTime().toLocalDate().equals(checkDate))
            allExpiringPermits.add("FC");
        if (vehicleInfo.getInsuranceExpiryDate().toLocalDateTime().toLocalDate().equals(checkDate))
            allExpiringPermits.add("Insurance");
        if (vehicleInfo.getPermitExpiryDate().toLocalDateTime().toLocalDate().equals(checkDate))
            allExpiringPermits.add("Permit");
        if (vehicleInfo.getTaxDueDate().toLocalDateTime().toLocalDate().equals(checkDate))
            allExpiringPermits.add("Tax");
        if (vehicleInfo.getPollutionCertificateExpiryDate().toLocalDateTime().toLocalDate().equals(checkDate))
            allExpiringPermits.add("PUC");
        return allExpiringPermits;
    }
}