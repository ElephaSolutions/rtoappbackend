package com.elepha.solutions.rto.model.enver.listener;

import com.elepha.solutions.rto.model.UsernameTrackingRevisionEntity;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;

public class UsernameTrackingRevisionEntityListener implements RevisionListener {
    @Override
    public void newRevision(Object o) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UsernameTrackingRevisionEntity revisionEntity = (UsernameTrackingRevisionEntity) o;
        revisionEntity.setUsername(username);
    }
}