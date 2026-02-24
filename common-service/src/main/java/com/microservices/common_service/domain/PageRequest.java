package com.microservices.common_service.domain;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {

    @Min(value = 0, message = "Page number must be greater than or equal to 0")
    @Builder.Default
    private Integer page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    @Builder.Default
    private Integer size = 10;

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortDirection = "ASC"; // ASC or DESC

    /**
     * Get sort direction as Spring Data Sort.Direction
     */
    public Sort.Direction getSortDirectionEnum() {
        return sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
    }

    /**
     * Convert to Spring Data Pageable
     */
    public Pageable toPageable() {
        return org.springframework.data.domain.PageRequest.of(
                page,
                size,
                Sort.by(getSortDirectionEnum(), sortBy)
        );
    }
}
