package com.snapit.memory;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "memory")
public class Memory {

    private Integer Id;
    private MemoryType type;
    private LocalDateTime createdAt;
}
