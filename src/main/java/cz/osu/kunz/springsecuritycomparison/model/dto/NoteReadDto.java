package cz.osu.kunz.springsecuritycomparison.model.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NoteReadDto {
    @NotNull
    private UUID id;

    @NotNull
    private String content;

    @NotNull
    private String ownerUsername;

    @NotNull
    private LocalDateTime createdAt;
}