package com.elepha.solutions.rto.model;

import com.elepha.solutions.rto.model.enver.listener.UsernameTrackingRevisionEntityListener;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.enhanced.SequenceIdRevisionEntity;

@Entity
@Table(name = "revinfo")
@RevisionEntity(value = UsernameTrackingRevisionEntityListener.class)
public class UsernameTrackingRevisionEntity extends SequenceIdRevisionEntity {
    String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}