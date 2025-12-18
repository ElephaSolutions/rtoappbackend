package com.elepha.solutions.rto.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SignUpRequestDTO(
        String username,
        String password,
        @JsonProperty(value = "owner-name") String ownerName,
        @JsonProperty(value = "agency-name") String agencyName,
        @JsonProperty(value = "contact-no") String contactNumber
) {}