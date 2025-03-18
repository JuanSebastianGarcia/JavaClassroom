package co.uniquindio.ingesis.dto.studentResource;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) for pagination when retrieving all students.
 * This DTO encapsulates pagination details.
 *
 * @param page The page number requested for pagination.
 */
public record GetAllDto(
    
    @NotBlank
    @NotNull
    int page) {
}
