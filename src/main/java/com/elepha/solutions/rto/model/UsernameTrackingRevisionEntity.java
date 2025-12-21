package com.elepha.solutions.rto.model;

import com.elepha.solutions.rto.model.enver.listener.UsernameTrackingRevisionEntityListener;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.enhanced.SequenceIdRevisionMapping;

@Entity
@Table(name = "revinfo")
@RevisionEntity(value = UsernameTrackingRevisionEntityListener.class)
public class UsernameTrackingRevisionEntity extends SequenceIdRevisionMapping {
    String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}