package cz.osu.kunz.springsecuritycomparison.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class NoteEditDto {
    @Size(max = 1000, message = "Content must be at most 1000 characters")
    private String content;
}