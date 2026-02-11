package cz.osu.kunz.springsecuritycomparison.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 1000)
    @Size(max = 1000)
    private String content;

    @Column(nullable = false)
    private String ownerUsername;

    private LocalDateTime createdAt = LocalDateTime.now();
}
