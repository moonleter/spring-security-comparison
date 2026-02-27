package cz.osu.kunz.springsecuritycomparison.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NoteCreateDto {

    @Size(max = 1000, message = "Content must be at most 1000 characters")
    private String content;
}