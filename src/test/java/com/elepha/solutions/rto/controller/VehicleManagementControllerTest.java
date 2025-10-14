package com.elepha.solutions.rto.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = {"unit"})
class VehicleManagementControllerTest {

    @Autowired
    private WebTestClient webTestClient;

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
}