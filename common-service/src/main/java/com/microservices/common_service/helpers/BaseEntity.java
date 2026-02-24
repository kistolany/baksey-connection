package com.microservices.common_service.helpers;

import com.microservices.common_service.constants.CoreConstants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(updatable = false, nullable = false)
    private UUID uuid;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdatedAt;


//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private CoreConstants.Status status;

    private String createdBy;

    private String lastUpdatedBy;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();

//        if (status == null) {
//            status = CoreConstants.Status.Enabled;
//        }
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdatedAt = LocalDateTime.now();
    }
}
