package com.ytpor.api.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(BaseEntityListener.class)
public abstract class BaseEntity implements Serializable {

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

}

class BaseEntityListener {

    @PrePersist
    protected void onCreate(BaseEntity entity) {
        entity.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(BaseEntity entity) {
        entity.updatedAt = LocalDateTime.now();
    }

}