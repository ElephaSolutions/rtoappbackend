package com.elepha.solutions.rto.dto;

import org.hibernate.envers.RevisionType;

import java.sql.Timestamp;

public record RecentActivitiesResponse(RevisionType revisionType, String vehicleNumber, Timestamp timestamp) {
}
